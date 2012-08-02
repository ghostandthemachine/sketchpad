(ns sketchpad.repl.project-repl
	(:require [seesaw.core :as seesaw]
		[clojure.string :as string]
    [clooj.brackets :as brackets]
    [sketchpad.util.utils :as utils]
		[sketchpad.repl.component :as repl.component]
		[sketchpad.repl.server :as repl.server]
    [sketchpad.repl.history :as repl.history]
		[sketchpad.repl.connection :as repl.connection]
		[sketchpad.repl.tab :as repl.tab]
    [sketchpad.buffer.action :as buffer.action]
    [sketchpad.util.option-windows :as option-windows]
		[sketchpad.config.config :as config]
    [sketchpad.util.tab :as tab]
    [sketchpad.repl.info :as repl.info]
    [sketchpad.state.state :as state]
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

;; from reply
(def exit-str
  (str 
    "(shutdown-agents)
    (flush)
    (System/exit 0)"))

(defn send-repl-cmd
"Send a given command to the specified outside REPL."
([repl cmd] (send-repl-cmd repl cmd "NO_SOURCE_PATH" 0))
([repl cmd file line]
  (utils/awtevent
  	  (let [repl-server (:server repl)
  	        repl-history (:history repl)
  	        conn (:conn repl)
            text-area (get-in repl [:component :text-area])
  	        items (:items repl-history)
  	        cmd-str (cmd-attach-file-and-line (buffer.action/get-last-cmd (get-in repl [:component :text-area])  ) file line)]
        (buffer.action/append-text-update text-area (str \newline))
  		    (when-let [response (-> (nrepl/client conn config/repl-response-timeout)
  	   						        (nrepl/message {:op :eval :code cmd})
  								    nrepl/combine-responses)]
            (let [response-str 
                    (str  
                      (cond
                        (contains? response :err)
                            (:err response)
                        (contains? response :out)
                            (:out response)
                        :default
                          (first (:value response)))
                      \newline)
                  ns-response (-> (nrepl/client conn config/repl-response-timeout)
                          (nrepl/message {:op :eval :code "(str *ns*)"})
                      nrepl/combine-responses)
                  prompt-str (str (:ns ns-response) "=> ")]
              (buffer.action/append-text-update text-area response-str)
  	          (buffer.action/append-text-update text-area prompt-str)
  	          (.discardAllEdits text-area))
  	   (when (not= cmd (first @items))
  	      (swap! items replace-first cmd)
  	      (swap! items conj ""))
  	  	(swap! (repl-history :pos) (fn [pos] 0)))))))

(defn sys-cmd 
  ([repl kwarg] (sys-cmd repl kwarg nil))
  ([repl kwarg word]
    (let [conn (:conn repl)
          cmd-str
    (cond
      (= kwarg :quit)
        (do
          (println "Closing repl  for path: " (:project repl))
          exit-str)
      (= kwarg :completions)
        (str `(doall 
                (map 
                 #(print % \"   \")
                 (into 
            (sorted-set)
          (flatten
            (doall
              (map 
                #(complete.core/completions (str \" ~word \") %)
                (all-ns)))))))))]
        (when-let [response (-> (nrepl/client conn config/repl-response-timeout)
						        (nrepl/message {:op :eval :code cmd-str})
					    nrepl/combine-responses)]))))

(defn completion-cmd-str [cmd complete-ns]
  (println (str "(complete.core/completions \"" cmd "\" " " '"complete-ns ")"))
  (str "(complete.core/completions \"" cmd "\" " " '"complete-ns ")"))

(defn get-completions [repl]
  (let [text-area (get-in repl [:component :text-area])
        project (sketchpad.project/project-from-path (:project repl))
        lein-project (:lein-project project)
        deps (take-nth 2 (flatten (:dependencies lein-project)))
        cmd (buffer.action/get-last-cmd text-area)]
;    (doall (map #(send-repl-cmd repl (completion-cmd-str cmd (str %))) deps))
  (send-repl-cmd repl 
    (str
    "(doall 
      (map 
        #(print % \"   \")
        (into 
          (sorted-set)
          (flatten
            (doall
              (map 
                #(complete.core/completions " cmd " %)
                (all-ns)))))))"
                ))))

(defn add-repl-behaviors [repl]
  (let [text-area (get-in repl [:component :text-area])
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
        next-hist #(repl.history/update-repl-history-display-position repl :inc)
        completions #(get-completions repl)]
    (utils/attach-action-keys text-area ["TAB" completions])
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
                                        (sys-cmd repl :quit)
                                        (tab/remove-repl repl)
                                        (sketchpad.project/remove-repl-from-project repl)))))))

(defn- repl-panel
  [project]
  (let [component    (repl.component/repl-component)
        server-port  (repl.server/repl-server project)
        repl {:type :repl
              :component   component
              :text-area   (:text-area component)
              :server-port server-port
              :conn        (nrepl/connect :port server-port)
              :title       (:title component)
              :project     (:path project)
              :uuid        (.. UUID randomUUID toString)
              :history     (repl.history/history)}
        tab (repl.tab/button-tab repl)]
    (assoc repl :tab tab)))

(defn repl
"Builds a new REPL component for a given project."
[project]
  (let [repl (repl-panel project)]
    (sketchpad.project/add-repl-to-project (:path project) repl)
    (add-repl-behaviors repl)
    (add-repl-mouse-handlers repl project)
    (repl.info/attach-caret-handler (get-in repl [:component :text-area]))
    (tab/add-repl repl)
    (tab/show-repl repl)
    (tab/focus-repl repl)
    (tab/repl-tab-component! repl)
  repl))


; (first (filter #(= uuid (:uuid %)) (mapcat :repls @projects)))
