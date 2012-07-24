(ns sketchpad.repl.repl
  (:use [sketchpad buffer])
	(:require [seesaw.core :as seesaw]
		[clojure.string :as string]
    [clooj.brackets :as brackets]
    [sketchpad.utils :as utils]
		[sketchpad.repl.component :as repl.component]
		[sketchpad.repl.server :as repl.server]
    [sketchpad.repl.history :as repl.history]
		[sketchpad.repl.connection :as repl.connection]
		[sketchpad.repl-button-tab :as button-tab]
    [sketchpad.buffer :as buffer]
    [sketchpad.option-windows :as option-windows]
		[sketchpad.config :as config]
    [sketchpad.tab :as tab]
    [clojure.tools.nrepl :as nrepl])
  (:import (java.io
             BufferedReader BufferedWriter
             InputStreamReader
             File PipedReader PipedWriter PrintWriter Writer
                    StringReader PushbackReader)))

(defn correct-expression? [cmd]
  (when-not (empty? (.trim cmd))
    (let [rdr (-> cmd StringReader. PushbackReader.)]
      (try (while (read rdr nil nil))
           true
           (catch IllegalArgumentException e true)
           (catch Exception e false)))))

(defn read-string-at [source-text start-line]
  `(let [sr# (java.io.StringReader. ~source-text)
         rdr# (proxy [clojure.lang.LineNumberingPushbackReader] [sr#]
               (getLineNumber []
                              (+ ~start-line (proxy-super getLineNumber))))]
     (take-while #(not= % :EOF_REACHED)
                 (repeatedly #(try (read rdr#)
                                   (catch Exception e# :EOF_REACHED))))))

(defn replace-first [coll x]
  (cons x (next coll)))

(defn tokens
  "Finds all the tokens in a given string."
  [text]
  (re-seq #"[\w/\.]+" text))

(defn namespaces-from-code
  "Take tokens from text and extract namespace symbols."
  [text]
  (->> text tokens (filter #(.contains % "/"))
       (map #(.split % "/"))
       (map first)
       (map #(when-not (empty? %) (symbol %)))
       (remove nil?)))

(defn cmd-attach-file-and-line [cmd file line]
  (let [read-string-code (read-string-at cmd line)
        short-file (last (.split file "/"))
        namespaces (namespaces-from-code cmd)]
    (pr-str
      `(do
         (dorun (map #(try (require %) (catch Exception _#)) '~namespaces))
         (binding [*source-path* ~short-file
                   *file* ~file]
           (last (map eval ~read-string-code)))))))

(defn cmd-attach-file-and-line [cmd file line]
  (let [read-string-code (read-string-at cmd line)
        short-file (last (.split file "/"))
        namespaces (namespaces-from-code cmd)]
    (pr-str
      `(do
         (dorun (map #(try (require %) (catch Exception _#)) '~namespaces))
         (binding [*source-path* ~short-file
                   *file* ~file]
           (last (map eval ~read-string-code)))))))

(defn send-repl-cmd
"Send a given command to the specified outside REPL."
([repl cmd] (send-repl-cmd repl cmd "NO_SOURCE_PATH" 0))
([repl cmd file line]
  (utils/awtevent
	  (let [repl-server (:server repl)
          conn (:conn repl)
	        repl-history (:history repl)
          text-area (:text-area repl)
	        items (:items repl-history)
	        cmd-str (cmd-attach-file-and-line (buffer/get-last-cmd text-area) file line)]
      (append-text-update text-area (str \newline))
		    (when-let [response (-> (nrepl/client conn config/repl-response-timeout)
	   						        (nrepl/message {:op :eval :code cmd})
								    nrepl/response-values)]
          (let [response-str (str (first response))
	             prompt-ns (-> (nrepl/client conn config/repl-response-timeout)
	                     	(nrepl/message {:op :eval :code "(ns-name *ns*)"})
	                     	nrepl/response-values)
	             promp-str (str \newline (first prompt-ns) "=> ")]
		    (append-text-update text-area response-str)
	        (append-text-update text-area promp-str)))
	   (when (not= cmd-str (first @items))
	      (swap! items replace-first cmd-str)
	      (swap! items conj ""))
	  	(swap! (repl-history :pos) (fn [pos] 0))))))

(defn add-repl-behaviors [repl]
  (let [text-area (:text-area repl)
        editor-repl-history (:repl-history repl)
        get-caret-pos #(.getCaretPosition text-area)
        ready #(let [caret-pos (get-caret-pos)
                     txt (.getText text-area)
                     trim-txt (string/trimr txt)]
                 (and
                   (pos? (.length trim-txt))
                   (<= (.length trim-txt)
                       caret-pos)
                   (= -1 (first (brackets/find-enclosing-brackets
                                  txt
                                  caret-pos)))))
        submit #(when-let [txt (buffer/get-last-cmd text-area)]
                  (let [pos (editor-repl-history :pos)]
                    (if (correct-expression? txt)
                      (do 
                        (send-repl-cmd text-area txt)
                        (swap! pos (fn [p] 0))))))
        prev-hist #(repl.history/update-repl-history-display-position repl :dec)
        next-hist #(repl.history/update-repl-history-display-position repl :inc)]
    (utils/attach-child-action-keys text-area ["ENTER" ready submit])
    (utils/attach-action-keys text-area ["control UP" prev-hist]
                              ["control DOWN" next-hist])))

(defn add-repl-mouse-handlers
"Takes a leinengin project, the parent tabbed panel, the repl buffer, and a function which takes 2 args: the lein project for this repl and the repl-buffer."
[lein-project btn repl-tabbed-panel repl-buffer shut-down-fn]
  (seesaw/listen btn
          :mouse-clicked (fn [e] (let [yes-no-option (option-windows/close-repl-dialogue)]
                                   (if (= yes-no-option 0)
                                     (do
                                        (apply shut-down-fn lein-project repl-buffer)
                                        (tab/remove-tab! repl-tabbed-panel repl-buffer)))))))

(defn repl
"Create a new REPL from a project."
  [project]
  (let [component (repl.component/repl-component)
 		text-area (:text-area component)
 		container (:container component)
        server-port (repl.server/server project)
        conn   (repl.connection/connection server-port)
        repl-history (repl.history/history)
	 	repl (map :type :repl 
    			    :component component
    		      :text-area text-area
    		      :container container
    		      :server-port server-port
    		      :connection conn
        	    :history history
        		  :project project)
        custom-repl-tab (button-tab/add-button-tab repl)]
    	(add-repl-behaviors repl conn history)
    	(assoc repl :tab custom-repl-tab)))

(defn init-new-repl-tab 
"Initialize REPL component handlers and add the component to the REPL tabbed panel component."
[repl]
  (let [repl-tabbed-panel (@state/app :repl-tabbed-panel)
        repl-component (:container repl)
        repl-text-area (:text-area repl)]
    (add-repl-behaviors repl)
    (add-repl-mouse-handlers repl)
    (tab/add-tab! repl-tabbed-panel repl-tab-component)
    (tab/show-tab! repl-tabbed-panel (index-of-component repl-tabbed-panel repl-component))
    (tab/focus-buffer repl-tabbed-panel repl-text-area)))

(defn new-repl-tab!
"Builds a new REPL component for a given project."
[project]
  (init-new-repl-tab (repl proejct)))
