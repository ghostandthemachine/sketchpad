(ns sketchpad.repl.app.repl
  (:import (java.io
             BufferedReader BufferedWriter
             InputStreamReader
             File PipedReader PipedWriter PrintWriter Writer
                    StringReader PushbackReader)
           (clojure.lang LineNumberingPushbackReader)
           (java.awt Rectangle)
           (java.net URL URLClassLoader URLDecoder))
  (:use [sketchpad.repl.app.util]
        [sketchpad.util.utils :only (attach-child-action-keys attach-action-keys
                            gen-map get-temp-file awt-event get-file-ns
                            when-lets get-text-str get-directories)]
        [clooj.brackets :only (find-line-group find-enclosing-brackets)]
        [clooj.help :only (get-var-maps)]
        [clj-inspector.jars :only (get-entries-in-jar jar-files)]
        [seesaw core color border meta]
        [sketchpad.util.tab]
        [clojure.tools.nrepl.server :only (start-server stop-server)])
  (:require [clojure.string :as string]
            [sketchpad.rsyntax :as rsyntax]
            [clojure.java.io :as io]
            [sketchpad.project.project :as sketchpad.project]
            [sketchpad.config.config :as config]
            [sketchpad.editor.buffer :as buffer]
            [sketchpad.buffer.search :as search]
            [sketchpad.buffer.grep :as buffer.grep]
            [sketchpad.buffer.action :as buffer.action]
            [sketchpad.editor.ui :as editor.ui]
            [sketchpad.wrapper.rtextscrollpane :as sp]
            [sketchpad.wrapper.rsyntaxtextarea :as wrapper.rsyntaxtextarea]
            [sketchpad.state.state :as state]
            [sketchpad.buffer.token :as token]
            [sketchpad.auto-complete.auto-complete :as auto-complete]
            [sketchpad.input.default :as input.default]
            [clojure.tools.nrepl :as repl]
            [sketchpad.repl.tab :as repl.tab]
            [sketchpad.repl.app.component :as repl.app.component]
            [leiningen.core.project :as lein])
  (:import (java.io.IOException)))

(use 'clojure.java.javadoc)

(def repl-history (atom {}))  

(def repls (atom {}))

(def ^:dynamic *printStackTrace-on-error* false)

(defn is-eof-ex? [throwable]
  (and (instance? clojure.lang.LispReader$ReaderException throwable)
       (or
         (.startsWith (.getMessage throwable) "java.lang.Exception: EOF while reading")
         (.startsWith (.getMessage throwable) "java.io.IOException: Write end dead"))))

(defn get-repl-ns [app]
  (let [repl-map @repls]
    (-> app :repl deref :project-path repl-map :ns)))

(defn setup-classpath [project-path]
  (when project-path
    (let [project-dir (File. project-path)]
      (when (and (.exists project-dir) (.isDirectory project-dir))
        (let [sub-dirs (get-directories project-dir)]
          (concat sub-dirs
                  (filter #(.endsWith (.getName %) ".jar")
                          (mapcat #(.listFiles %) (file-seq project-dir)))))))))

(defn selfish-class-loader [url-array parent]
  (proxy [URLClassLoader] [url-array nil]
    (findClass [classname]
      (try (proxy-super findClass classname)
           (catch ClassNotFoundException e
                  (.findClass parent classname))))))

(defn create-class-loader [project-path parent]
  (when project-path
    (let [files (setup-classpath project-path)
          urls (map #(.toURL %) files)]
      (println " Classpath:")
      (dorun (map #(println " " (.getAbsolutePath %)) files))
      (URLClassLoader.
        (into-array URL urls) parent
        ))))
    
(defn find-clojure-jar [class-loader]
  (when-let [url (.findResource class-loader "clojure/lang/RT.class")]
    (-> url .getFile URL. .getFile URLDecoder/decode (.split "!/") first)))

(defn clojure-jar-location
  "Find the location of a clojure jar in a project."
  [^String project-path]
  (let [lib-dir (str project-path "/lib")
        jars (filter #(.contains (.getName %) "clojure")
                     (jar-files lib-dir))]
    (first
      (remove nil?
              (for [jar jars]
                (when-not
                  (empty?
                    (filter #(= "clojure/lang/RT.class" %)
                            (map #(.getName %) (get-entries-in-jar jar))))
                  jar))))))
                       
        
(defn outside-repl-classpath [project-path]
  (let [clojure-jar-term (when-not (clojure-jar-location project-path)
                           (find-clojure-jar (.getClassLoader clojure.lang.RT)))]
    (filter identity [(str project-path "/src")
                      (when clojure-jar-term
                        clojure-jar-term)])))


(defn update-repl-history [app]
  (swap! (:items repl-history) replace-first
    (get-text-str (app :application-repl))))
  
(defn scroll-to-last [text-area]
  (.scrollRectToVisible text-area
                        (Rectangle. 0 (.getHeight text-area) 1 1)))

(defn relative-file [app]
  (let [prefix (str (-> app :repl deref :project-path) File/separator
                    "src"  File/separator)]
    (when-lets [f @(app :current-file)
                path (.getAbsolutePath f)]
      (subs path (count prefix)))))

(defn selected-region [ta]
  (if-let [text (.getSelectedText ta)]
    {:text text
     :start (.getSelectionStart ta)
     :end   (.getSelectionEnd ta)}
    (let [[a b] (find-line-group ta)]
      (when (and a b (< a b))
        {:text (.. ta getDocument (getText a (- b a)))
         :start a
         :end b}))))

(defn add-repl-input-handler [rsta]
  (let [ta-in rsta
        application-repl-history (get-meta rsta :repl-history)
        get-caret-pos #(.getCaretPosition ta-in)
        submit #(invoke-later
      	            (when-let [txt (buffer.action/get-last-cmd rsta)]
	                    (let [pos (application-repl-history :pos)]
	                      (if (correct-expression? txt)
	                        (do
	                          (send-to-application-repl rsta txt)
	                          (swap! pos (fn [p] 0)))))))
        at-top #(zero? (.getLineOfOffset ta-in (get-caret-pos)))
        at-bottom #(= (.getLineOfOffset ta-in (get-caret-pos))
                      (.getLineOfOffset ta-in (.. ta-in getText length)))
        prev-hist #(update-repl-history-display-position ta-in :dec)
        next-hist #(update-repl-history-display-position ta-in :inc)]
    (attach-action-keys ta-in ["ENTER" submit])
    (attach-action-keys ta-in ["control UP" prev-hist]
                              ["control DOWN" next-hist])))

(defn update-last-repl
	[e]
	(seesaw.core/invoke-later
		(let [uuid (get-repl-uuid)
			repls (current-repls)
			repl (get repls uuid)]
		  	(when-not (nil? repl)
		  		(let [projects @(:projects @state/app)
		  			project (get projects (:project repl))
		  			last-repl-atom (get project :last-focused-repl)]
		  			(reset! last-repl-atom uuid))))))

(defn attach-tab-change-handler [repl-tabbed-panel]
  (listen repl-tabbed-panel :selection update-last-repl)
  (listen repl-tabbed-panel :selection 
    (fn [e] 
      (let [num-tabs (tab-count repl-tabbed-panel)]
       (when (> 0 num-tabs)
          (swap! state/app assoc :doc-title-atom (current-repl)))))))


(defn- double-click?
[e]
(= (.getClickCount e) 2))

(defn- check-project-path
	[abs-path]
	 (first 
	(filter #(re-find (re-pattern %) abs-path) (keys @(:projects @state/app)))))

 (defn repl-double-click-handler
 	[text-area e]
 	(when (double-click? e)
 		(let [line-str (apply str  (token/line-token text-area))
 			line-seq (clojure.string/split line-str #"\:")]
 			(when (token/can-be-opened? line-seq)
 				(invoke-later
	 				(let [abs-path (first line-seq)
	 					project-path (check-project-path abs-path)
	 					buffer (buffer/open-buffer abs-path project-path)]
	 					(.setCaretPosition (get-in buffer [:component :text-area]) 0)
	 					(search/search  (get-in buffer [:component :text-area]) (last line-seq))
	 					(let [selection-end (.getSelectionEnd text-area)]
	 						(doto text-area
	 							(.setSelectionStart selection-end)
	 							(.setSelectionEnd selection-end)))))))))

(defn attach-repl-mouse-click-handler
	[repl]
	(let [text-area (get-in repl [:component :text-area])]
		(listen text-area :mouse-clicked (partial repl-double-click-handler text-area))))

(defn init-repl-tabbed-panel [repl-tabbed-panel repl]
  (let [text-area (get-in repl [:component :text-area])
        scroller (get-in repl [:component :scroller])
        container (get-in repl [:component :container])]
    (.setUI repl-tabbed-panel (editor.ui/sketchpad-tab-ui repl-tabbed-panel))
    (add-tab! repl-tabbed-panel "Sketchpad" container)
    (repl-tab-component! repl-tabbed-panel repl)
    (wrapper.rsyntaxtextarea/set-input-map! text-area (input.default/default-input-map))
    (add-repl-input-handler text-area)
    (config! scroller :background config/app-color)
    (auto-complete/install-auto-completion repl)
    (config/apply-repl-prefs! text-area)
    (send-to-application-repl text-area "(require 'sketchpad.user)\n\t\t(in-ns 'sketchpad.user)")))

(defn repl-tabbed-panel
  []
  (let [repl-tabbed-panel   (tabbed-panel :placement :top
                                            :overflow :wrap
                                            :background (color :black)
                                            :border nil)
        application-repl (repl.app.component/application-repl-component)]
    (init-repl-tabbed-panel repl-tabbed-panel application-repl)
    (attach-tab-change-handler repl-tabbed-panel)
    (attach-repl-mouse-click-handler application-repl)
    (swap! state/app conj (gen-map
                            repl-tabbed-panel
                            application-repl))
    {:type :repl-tabbed-panel
     :auto-complete (atom nil)
     :component {:container repl-tabbed-panel}
     :application-repl application-repl}))

	