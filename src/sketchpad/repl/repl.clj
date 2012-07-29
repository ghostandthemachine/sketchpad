(ns sketchpad.repl.repl
	(:require [seesaw.core :as seesaw]
		[clojure.string :as string]
    [clooj.brackets :as brackets]
    [sketchpad.utils :as utils]
		[sketchpad.repl.component :as repl.component]
		[sketchpad.repl.server :as repl.server]
    [sketchpad.repl.history :as repl.history]
		[sketchpad.repl.connection :as repl.connection]
		[sketchpad.repl.tab :as repl.tab]
    [sketchpad.buffer.action :as buffer.action]
    [sketchpad.option-windows :as option-windows]
		[sketchpad.config :as config]
    [sketchpad.tab :as tab]
    [sketchpad.state :as state]
    [sketchpad.project.project :as sketchpad.project]
    [clojure.tools.nrepl :as nrepl])
  (:import  (java.util UUID)
            (java.io
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
	        cmd-str (cmd-attach-file-and-line (buffer.action/get-last-cmd (:text-area repl)  ) file line)]
      (buffer.action/append-text-update text-area (str \newline))
      (buffer.action/append-text-update text-area (str "=> "))
		    ; (when-let [response (-> (nrepl/client conn config/repl-response-timeout)
	   		; 				        (nrepl/message {:op :eval :code cmd})
						; 		    nrepl/response-values)]
      ;     (let [response-str (str (first response))
	     ;         prompt-ns (-> (nrepl/client conn config/repl-response-timeout)
	     ;                 	(nrepl/message {:op :eval :code "(ns-name *ns*)"}) 
	     ;                 	nrepl/response-values)
	     ;         promp-str (str \newline (first prompt-ns) "=> ")]
		    ; (buffer.action/append-text-update text-area response-str)
	     ;    (buffer.action/append-text-update text-area promp-str)))
	   (when (not= cmd-str (first @items))
	      (swap! items replace-first cmd-str)
	      (swap! items conj ""))
	  	(swap! (repl-history :pos) (fn [pos] 0))))))

(defn add-repl-behaviors [repl]
  (let [text-area (:text-area repl)
        repl-history (:repl-history repl)
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
        submit #(let [txt (buffer.action/get-last-cmd text-area)
                      pos (get-in repl [:history :pos])]
                    (if (correct-expression? txt)
                      (do 
                        (send-repl-cmd repl txt)
                        (reset! pos 0))))
        prev-hist #(repl.history/update-repl-history-display-position repl :dec)
        next-hist #(repl.history/update-repl-history-display-position repl :inc)]
    (utils/attach-child-action-keys text-area ["ENTER" ready submit])
    (utils/attach-action-keys text-area ["control UP" prev-hist]
                              ["control DOWN" next-hist])))

(defn add-repl-mouse-handlers
"Takes a leinengin project, the parent tabbed panel, the repl buffer, and a function which takes 2 args: the lein project for this repl and the repl-buffer."
[repl project]
  (seesaw/listen (get-in repl [:tab :button])
          :mouse-clicked (fn [e] (let [yes-no-option (option-windows/close-repl-dialogue)]
                                   (if (= yes-no-option 0)
                                     (do
                                        (tab/remove-repl repl)
                                        (sketchpad.project/remove-repl-from-project repl)))))))

(defn create-repl-map [component history project uuid conn server-port]
  {:type :repl
   :container (:container component)
   :component component
   :text-area (:text-area component)
   :server-port server-port
   :conn conn
   :title (:title component)
   :project (:path project)
   :uuid uuid
   :history history})

(defn build-ui [sketchpad-project]
  (let [component (repl.component/repl-component)
        server-port (repl.server/repl-server sketchpad-project)
        conn (repl.connection/connection server-port)
        uuid (.. UUID randomUUID toString)
        repl-history (repl.history/history)
        repl (create-repl-map component repl-history sketchpad-project uuid conn server-port)
        tab (repl.tab/button-tab repl)]
    (assoc repl :tab tab)))

(defn add-repl-to-project [project repl]
  (sketchpad.project/add-repl-to-project project repl))

(defn init-new-repl-tab 
"Initialize REPL component handlers and add the component to the REPL tabbed panel component."
[repl project]
  (let [repl-component (:container repl)
        repl-text-area (:text-area repl)]
    (add-repl-to-project (:path project) repl)
    (add-repl-behaviors repl)
    (add-repl-mouse-handlers repl project)
    (tab/add-repl repl)
    (tab/show-repl repl)
    (tab/focus-repl repl))
  (tab/repl-tab-component! repl)
  repl)

(defn repl
"Builds a new REPL component for a given project."
[project]
  (init-new-repl-tab (build-ui project) project))


; (first (filter #(= uuid (:uuid %)) (mapcat :repls @projects)))
