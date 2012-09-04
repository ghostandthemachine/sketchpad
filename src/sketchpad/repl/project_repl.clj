(ns sketchpad.repl.project-repl
  (:use [sketchpad.repl.app.util])
	(:require [seesaw.core :as seesaw]
        [clojure.string :as string]
        [clooj.brackets :as brackets]
        [sketchpad.repl.info-utils :as repl.info-utils]
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
        [sketchpad.auto-complete.auto-complete :as auto-complete]
	      [sketchpad.state.state :as state]
	      [sketchpad.project.project :as sketchpad.project]
	      [clojure.tools.nrepl :as nrepl])
  (:import  (java.util UUID)
            (java.io
             BufferedReader BufferedWriter
             InputStreamReader
             File PipedReader PipedWriter PrintWriter Writer
                    StringReader PushbackReader)))

(defonce creating-project-repl (atom false))

(defn start-creating-repl
  []
  (reset! creating-project-repl true))

(defn stop-creating-repl
  []
  (reset! creating-project-repl false))

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
	(let [repl-server (:server repl)
		repl-history (:history repl)
		conn (:conn repl)
		text-area (get-in repl [:component :text-area])
		items (:items repl-history)]
		(buffer.action/append-text text-area (str \newline))
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
            (buffer.action/append-text text-area response-str)
	          (buffer.action/append-text text-area prompt-str)
	          (.discardAllEdits text-area))
	   (when (not= cmd (first @items))
	      (swap! items replace-first cmd)
	      (swap! items conj ""))
	  	(swap! (repl-history :pos) (fn [pos] 0))))))

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

(defn submit [repl]
  (let [text-area (get-in repl [:component :text-area])
        txt (buffer.action/get-last-cmd text-area)
        pos (get-in repl [:history :pos])]
      (if (correct-expression? txt)
        (do
          (send-repl-cmd repl txt)
          (reset! pos 0)))))

(defn- ready [text-area]
  (seesaw/invoke-later
    (let [get-caret-pos #(.getCaretPosition text-area)
          caret-pos (get-caret-pos)
                       txt (.getText text-area)
                       trim-txt (string/trimr txt)]
                   (and
                     (pos? (.length trim-txt))
                     (<= (.length trim-txt)
                         caret-pos)
                     (= -1 (first (brackets/find-enclosing-brackets
                                    txt
                                    caret-pos)))))))

  (defn add-repl-behaviors [repl]
  (let [text-area (get-in repl [:component :text-area])
        repl-history (:repl-history repl)
        ready (partial ready text-area)
        submit (partial submit repl)
        prev-hist #(repl.history/update-repl-history-display-position repl :dec)
        next-hist #(repl.history/update-repl-history-display-position repl :inc)
        completions #(get-completions repl)]
    (utils/attach-action-keys text-area ["TAB" completions])
    (utils/attach-action-keys text-area ["ENTER" submit])
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
  (let [component (repl.component/repl-component project)
        repl {:type :repl
              :component   component
              :text-area   (:text-area component)
              :title       (:title component)
              :project     (:path project)
              :auto-complete (atom nil)
              :uuid        (seesaw.meta/get-meta (:text-area component) :uuid)
              :history     (repl.history/history)}
        tab (repl.tab/button-tab repl)]
    (assoc repl :tab tab)))

(defn repl
"Builds a new REPL component for a given project."
[project]
  (start-creating-repl)
  (let [server-port (future (repl.server/repl-server project))
        msg-str (str "Creating new REPL for " (last (clojure.string/split (:path project) #"/")) "...")
        info-timer (repl.info-utils/post-msg msg-str)]
    (future
      (let [port @server-port
            conn (nrepl/connect :port port)]
        (let[repl (repl-panel project)
             repl (assoc repl :port port :conn conn)]
            (sketchpad.project/add-repl-to-project (:path project) repl)
            (add-repl-behaviors repl)
            (add-repl-mouse-handlers repl project)
            (auto-complete/install-auto-completion repl)
            (repl.info/attach-caret-handler (get-in repl [:component :text-area]))
            (tab/add-repl repl)
            (tab/show-repl repl)
            (tab/focus-repl repl)
            (tab/repl-tab-component! repl)
  (repl.info-utils/post-msg "")
  (stop-creating-repl)  
  repl)))))