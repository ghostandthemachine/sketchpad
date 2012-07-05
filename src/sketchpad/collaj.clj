(ns sketchpad.repl
  (:import (java.io
             BufferedReader BufferedWriter
             InputStreamReader
             File PipedReader PipedWriter PrintWriter Writer
                    StringReader PushbackReader)
           (clojure.lang LineNumberingPushbackReader)
           (java.awt Rectangle)
           (java.util.concurrent LinkedBlockingDeque)
           (java.net URL URLClassLoader URLDecoder))
  (:use [sketchpad.utils :only (attach-child-action-keys attach-action-keys
                            awt-event get-file-ns
                            append-text when-lets get-text-str get-directories)]
        [clooj.brackets :only (find-line-group find-enclosing-brackets)]
        [clooj.help :only (get-var-maps)]
        [sketchpad.utils :only (gen-map get-temp-file)]
        [clj-inspector.jars :only (get-entries-in-jar jar-files)]
        [seesaw core color border meta]
        [sketchpad rsyntaxtextarea tab-manager auto-complete app-cmd default-mode sketchpad-repl]
        [clojure.tools.nrepl.server :only (start-server stop-server)])
  (:require [clojure.string :as string]
            [clooj.rsyntax :as rsyntax]
            [clojure.java.io :as io]
            [sketchpad.editor-kit :as kit]
            [sketchpad.config :as config]
            [sketchpad.tab-ui :as tab]
            [sketchpad.rsyntaxtextarea :as rsta]
            [clojure.tools.nrepl :as repl])
  (:import (org.fife.ui.rtextarea RTextScrollPane)
           (java.io.IOException)))

(use 'clojure.java.javadoc)

(def repl-history {:items (atom nil) :pos (atom 0) :last-end-pos (atom 0)})

(def editor-repl-history {:items (atom (list "")) :pos (atom 0)}) 

(def repl-history (atom {}))	

; (defn creatae-new-repl-history [])

(def repls (atom {}))

(defn offer! 
  "adds x to the back of queue q"
  [q x] (.offer q x) q)

(defn take! 
  "takes from the front of queue q.  blocks if q is empty"
  [q] (.take q))

(def ^:dynamic *printStackTrace-on-error* false)

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
    (filter identity [(str project-path "lib/*")
                      (str project-path "/src")
                      (when clojure-jar-term
                        clojure-jar-term)])))

(defn create-outside-repl
  "This function creates an outside process with a clojure repl."
  [result-writer project-path]
  (let [clojure-jar (clojure-jar-location project-path)
        java (str (System/getProperty "java.home")
                  File/separator "bin" File/separator "java")
        classpath (outside-repl-classpath project-path)
        classpath-str (apply str (interpose File/pathSeparatorChar classpath))
        _ (println classpath-str)
        builder (ProcessBuilder.
                  [java "-cp" classpath-str "clojure.main"])]
    (.redirectErrorStream builder true)
    (.directory builder (File. (or project-path ".")))
    (try
      (let [proc (.start builder)
            input-writer  (-> proc .getOutputStream (PrintWriter. true))
            repl {:input-writer input-writer
                  :project-path project-path
                  :thread nil
                  :proc proc
                  :var-maps (agent nil)
                  :last-start-line 1}
            is (.getInputStream proc)]
        (send-off (repl :var-maps) #(merge % (get-var-maps project-path classpath)))
        (future (io/copy is result-writer :buffer-size 1))
        (swap! repls assoc project-path repl)
        repl)
      (catch java.io.IOException e
        (println "Could not create outside REPL for path: " project-path)))))



(defn- get-classpath []
   (sort (map (memfn getPath) 
              (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader))))))

(def nrepl-index (atom 5000))
(defn next-nrepl-index [] 
	(swap! nrepl-index inc)
	@nrepl-index)

(defn send-nrepl-cmd []
	(with-open [conn (repl/connect :port 5001)]
	     (-> (repl/client conn 1000)
	       (repl/message {:op :eval :code "(+ 1 1)"})
	       repl/response-values)))
;	
;(defn create-nrepl-server []
;	(let [server-port (next-nrepl-index)
;				classpath-str (get-classpath)
;				server (start-server :port server-port)
;				nrepl-server {:server server :port server-port :class-path classpath-str}]
;		nrepl-server))

(defn create-nrepl-server
  "This function creates an outside process with a clojure repl."
  []
  (let [server-port (next-nrepl-index)
  			project-path (get-classpath)
  			clojure-jar (clojure-jar-location project-path)
        java (str (System/getProperty "java.home")
                  File/separator "bin" File/separator "java")
        classpath (outside-repl-classpath project-path)
        classpath-str (apply str (interpose File/pathSeparatorChar classpath))
        _ (println classpath-str)
;        server-init-cmd (str 
;        									"(do "
;	        									"(use '[clojure.tools.nrepl.server :only (start-server stop-server)])"
;  	      									"(start-server :port" server-port "))")
  	    server-init-cmd (str "Users/jon/code/projets/sketchpad/src/sketchpad/nrepl_server.clj " server-port)
        														
        builder (ProcessBuilder.
                  [java "-cp" classpath-str "clojure.main" server-init-cmd])]
    {:cmd (str java "-cp" classpath-str "clojure.main" server-init-cmd) :process builder :port server-port :classpath classpath}))
        
(defn create-nrepl-connection [server]
	(with-open [conn (repl/connect :port (:port server))]
		conn))
	
(defn send-nrepl-cmd [conn]
	(-> (repl/client conn 1000)    ; message receive timeout required
      (repl/message {:op :eval :code "(+ 2 3)"})
      repl/response-values))



(defn append-text-update [rsta s]
  (append-text rsta (str " " s))
  (.setCaretPosition rsta (.getLastVisibleOffset rsta)))

(defn sketchpad-reader [q prompt exit]
    (read-string (.take q))
  )

(defn sketchpad-prompt [rsta]
  (append-text rsta (str \newline (ns-name *ns*) "=> "))
  (.setCaretPosition rsta (.getLastVisibleOffset rsta))
  ; (swap! (:last-end-pos repl-history) (fn [_] (.getLastVisibleOffset rsta)))
  ; (printf "%s=> " (ns-name *ns*))
  )

(defn sketchpad-printer [rsta value]
  ;; append text area
  (append-text rsta (str value))
  ; (swap! (:last-end-pos repl-history) (fn [_] (.getLastVisibleOffset rsta)))
  )

(defn create-editor-repl [repl-rsta]
  (let [editor-repl-q (LinkedBlockingDeque. )
        reader (partial sketchpad-reader editor-repl-q)
        printer (partial sketchpad-printer repl-rsta)
        prompt (partial sketchpad-prompt repl-rsta)]
    (future 
      (sketchpad-repl repl-rsta
        :read reader
        :print printer
        :prompt prompt))
    editor-repl-q))

(defn replace-first [coll x]
  (cons x (next coll)))

(defn update-repl-history [app]
  (swap! (:items repl-history) replace-first
         (get-text-str (app :editor-repl))))

(defn correct-expression? [cmd]
  (when-not (empty? (.trim cmd))
    (let [rdr (-> cmd StringReader. PushbackReader.)]
      (try (while (read rdr nil nil))
           true
           (catch IllegalArgumentException e true) ;explicitly show duplicate keys etc.
           (catch Exception e false)))))

(defn read-string-at [source-text start-line]
  `(let [sr# (java.io.StringReader. ~source-text)
         rdr# (proxy [clojure.lang.LineNumberingPushbackReader] [sr#]
               (getLineNumber []
                              (+ ~start-line (proxy-super getLineNumber))))]
     (take-while #(not= % :EOF_REACHED)
                 (repeatedly #(try (read rdr#)
                                   (catch Exception e# :EOF_REACHED))))))

(defn cmd-attach-file-and-line [cmd file line]
  (let [read-string-code (read-string-at cmd line)
        short-file (last (.split file "/"))
        namespaces (namespaces-from-code cmd)]
    ;(println namespaces)
    (pr-str
      `(do
         (dorun (map #(try (require %) (catch Exception _#)) '~namespaces))
         (binding [*source-path* ~short-file
                   *file* ~file]
           (last (map eval ~read-string-code)))))))
      

(defn send-to-editor-repl
  ([app cmd] (send-to-editor-repl app cmd "NO_SOURCE_PATH" 0) :repl)
  ([app cmd file line] (send-to-editor-repl app cmd file line :file))
  ([app cmd file line src-key]
  (println "editor repl cmd: " cmd)
   (awt-event
    (let [cmd-ln (str \newline (.trim cmd) \newline)
          cmd-trim (.trim cmd)]
      (cond
          (= src-key :repl)
            ;; with one repl panel we just want to go to the next line
            (append-text (app :editor-repl) (str \newline))
          (= src-key :file)
            (append-text (app :editor-repl) (str \newline))
            ; (append-text (app :editor-repl) cmd-ln)
            )
      (let [cmd-str (cmd-attach-file-and-line cmd file line)]
        (offer! (app :repl-que) cmd-str)
        )
      (when (not= cmd-trim (second @(:items editor-repl-history)))
        	(swap! (:items editor-repl-history)
          	     replace-first cmd-trim)
        	(swap! (:items editor-repl-history) conj ""))
      (reset! (:pos editor-repl-history) 0)
      ))))

(defn send-to-repl
  ([app cmd] (send-to-repl app cmd "NO_SOURCE_PATH" 0) :repl)
  ([app cmd file line] (send-to-repl app cmd file line :file))
  ([app cmd file line src-key]
  (awt-event
    (let [cmd-ln (str \newline (.trim cmd) \newline)
          cmd-trim (.trim cmd)]
      (cond
          (= src-key :repl)
            ;; with one repl panel we just want to go to the next line
            (append-text (app :editor-repl) (str \newline))
          (= src-key :file)
            (append-text (app :editor-repl) (str \newline))
            ; (append-text (app :editor-repl) cmd-ln)
            )
      (let [cmd-str (cmd-attach-file-and-line cmd file line)]
        (binding [*out* (:input-writer @(app :repl))]
          (println cmd-str)
          (flush)))
      (when (not= cmd-trim (second @(:items repl-history)))
        (swap! (:items repl-history)
               replace-first cmd-trim)
        (swap! (:items repl-history) conj ""))
      (reset! (:pos repl-history) 0)))))
        
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

(defn send-selected-to-repl [app]
  (let [ta (app :doc-text-area)
        region (selected-region ta)
        txt (:text region)]
    (if-not (and txt (correct-expression? txt))
        (.setText (app :arglist-label) "Malformed expression")
         (let [line (.getLineOfOffset ta (:start region))]
           (send-to-repl app txt (relative-file app) line)))))

(defn send-doc-to-repl [app]
  (let [text (->> app :doc-text-area .getText)]
    (send-to-repl app text (relative-file app) 0)))

(defn make-repl-writer [ta-out app-atom]
  (->
    (let [buf (agent (StringBuffer.))]
      (proxy [Writer] []
        (write
          ([char-array offset length]
            ; (println "char array:" (apply str char-array) (count char-array))
            (awt-event 
              (append-text ta-out (apply str char-array))
              (swap! (:last-end-pos repl-history) (fn [_] (.getLastVisibleOffset ta-out)))
              ))
          ([^Integer t]
            ; (println "Integer: " t (type t))
            (awt-event (append-text ta-out (str (char t))))))
        (flush [] (awt-event (scroll-to-last ta-out)))
        (close [] nil)))
    (PrintWriter. true)))
  
(defn update-repl-text [app]
  (let [rsta (:editor-repl app)
        last-pos @(:last-end-pos repl-history)
        items @(:items repl-history)]
    (when (pos? (count items))
      ; (println "last-pos: " last-pos " last-visible-offset: " (.getLastVisibleOffset rsta) " last-pos - last-string-size: " (- last-pos (count (nth items (- @(:pos repl-history) 1)))) )
      ; (println (- (.getLastVisibleOffset rsta) last-pos))
      ;; clear the last history if needed
      (if (> (- (.getLastVisibleOffset rsta) last-pos) 0)
        (do 
        ; (println "remove last string from: " (- last-pos (count (nth items (- @(:pos repl-history) 1)))))
          ; (.remove (.. rsta getDocument) (- last-pos (count (nth items (- @(:pos repl-history) 1)))) (count (nth items (- @(:pos repl-history) 1))))
          ;; insert the text
           (.insert rsta 
                (nth items @(:pos repl-history))
                (- last-pos (count (nth items (- @(:pos repl-history) 1)))))
        )
        (do 
          ; (println "remove last string from: " last-pos " of length: " (- (.getLastVisibleOffset rsta) last-pos))
          ;; insert the text
          (.insert rsta 
            (nth items @(:pos repl-history))
            last-pos)
          )
          )
        )
      ; (println "insert repl histoy string: " (nth items @(:pos repl-history)) (- last-pos (count (nth items (- @(:pos repl-history) 1)))))
      ))

(defn show-previous-repl-entry [app]
  (when (zero? @(:pos repl-history))
    (update-repl-history app))
  (swap! (:pos repl-history)
         #(min (dec (count @(:items repl-history))) (inc %)))
  (update-repl-text app))

(defn show-next-repl-entry [app]
  (when (pos? @(:pos repl-history))
    (swap! (:pos repl-history)
           #(Math/max 0 (dec %)))
    (update-repl-text app)))

(defn load-file-in-repl [app]
  (when-lets [f0 @(:current-file app)
              f (or (get-temp-file f0) f0)]
    (send-to-repl app (str "(load-file \"" (.getAbsolutePath f) "\")"))))

(defn apply-namespace-to-repl [app]
  (when-let [current-ns (get-file-ns (config (app :doc-text-area) :text))]
    (send-to-repl app (str "(ns " current-ns ")"))
    (swap! repls assoc-in
           [(-> app :repl deref :project-path) :ns]
           current-ns)))

(defn restart-repl [app project-path]
  (append-text (app :editor-repl)
               (str "\n=== RESTARTING " project-path " REPL ===\n"))
  (when-let [proc (-> app :repl deref :proc)]
    (.destroy proc))
  (reset! (:repl app) (create-outside-repl (app :repl-out-writer) project-path))
  (apply-namespace-to-repl app))

(defn switch-repl [app project-path]
  (when (and project-path
             (not= project-path (-> app :repl deref :project-path)))
    (append-text (app :editor-repl)
                 (str "\n\n=== Switching to " project-path " REPL ===\n"))
    (let [repl (or (get @repls project-path)
                   (create-outside-repl (app :repl-out-writer) project-path))]
      (reset! (:repl app) repl))))

(defn get-last-cmd [rta]
  (let [text (config rta :text)]
    (last (string/split text #"=>"))))

(defn clear-repl-input [rsta]
  (let [end (rsta/last-visible-offset rsta)
        trim-str (get-last-cmd rsta)
        start (- end (count trim-str))]
    (rsta/replace-range! rsta nil start end)))

(defn append-history-text [rsta m]
  (let [pos @(m :pos)
        history-str (str (nth @(m :items) pos))]
    (append-text-update rsta history-str)))

(defn update-repl-history-display-position [app kw]
	(let [rsta (app :editor-repl)
        history-pos @(editor-repl-history :pos)
        cmd (get-last-cmd rsta)]
        (println history-pos)
    (cond 
  		(= kw :dec)
  			(if (< history-pos (- (count @(editor-repl-history :items)) 1))
  					(swap! (editor-repl-history :pos) (fn [pos] (+ pos 1)))
  					(swap! (editor-repl-history :pos) (fn [pos] pos)) ;; go back to start of list
  					)
  		(= kw :inc)
  			(if (> history-pos 1)
  					(swap! (editor-repl-history :pos) (fn [pos] (- pos 1)))
  					(swap! (editor-repl-history :pos) (fn [pos] pos)) ;; go to end of list
  					))
    (clear-repl-input rsta)
    (append-history-text rsta editor-repl-history)))

(defn add-repl-input-handler [app]
  (rsta/set-input-map! (app :editor-repl) (default-input-map))

  (let [ta-in (app :editor-repl)
        get-caret-pos #(.getCaretPosition ta-in)
        ready #(let [caret-pos (get-caret-pos)
                     txt (.getText ta-in)
                     trim-txt (string/trimr txt)]
                 (and
                   (pos? (.length trim-txt))
                   (<= (.length trim-txt)
                       caret-pos)
                   (= -1 (first (find-enclosing-brackets
                                  txt
                                  caret-pos)))))
        submit #(when-let [txt (get-last-cmd (:editor-repl app))]
                  (let [cmd-type (cmd-prefix? txt)]
                    (cond
                      ;; handle an application command
                      (= :app-cmd cmd-type)
                        (do
                          ;(println txt)
                          )
                      ;; handle a normal command
                      (= :repl-cmd cmd-type)
                        (do
                          ;(println txt)
                          (if (correct-expression? txt)
                            (do 
                              (send-to-editor-repl app txt)
                              (reset! (editor-repl-history :pos) 0)
                              )))
                      :else
                        (.setText (app :arglist-label) "Malformed expression"))))
        at-top #(zero? (.getLineOfOffset ta-in (get-caret-pos)))
        at-bottom #(= (.getLineOfOffset ta-in (get-caret-pos))
                      (.getLineOfOffset ta-in (.. ta-in getText length)))
        prev-hist #(update-repl-history-display-position app :dec)
        next-hist #(update-repl-history-display-position app :inc)]
    (attach-child-action-keys ta-in ;["control UP" at-top prev-hist]
                                    ;["control DOWN" at-bottom next-hist]
                                    ["ENTER" ready submit])
    (attach-action-keys ta-in ["cmd1 UP" prev-hist]
                              ["cmd1 DOWN" next-hist])
                              ; ["cmd1 ENTER" submit] ;; blocks dumb completion
                              ; )
    ))

(defn print-stack-trace [app]
    (send-to-repl app "(.printStackTrace *e)"))

;; view




; (defn make-repl-panel [app-atom]

;   )



(defn repl
  [app-atom]
  (let [repl-tabbed-panel   (tabbed-panel :placement :top
                                            :overflow :wrap
                                            :background (color :black)
                                            :border nil)
        editor-repl (rsyntax/text-area 
                                      :syntax         "clojure"     
                                      :border          nil                          
                                      :id             :editor-repl-text-area
                                      :class          [:repl :syntax-editor])

				
                                      
        ; repl-out-writer   (make-repl-writer editor-repl app-atom)
        repl-in-scroll-pane (RTextScrollPane. editor-repl false) ;; default to no linenumbers
        repl-container (vertical-panel 
                                      :items          [repl-in-scroll-pane]                                      
                                      :id             :repl-container
                                      :class          :repl)
        repl-undo-count (atom 0)
        repl-que (create-editor-repl editor-repl)]
            ;; set tab ui
    (.setUI repl-tabbed-panel (tab/sketchpad-tab-ui repl-tabbed-panel))
    (listen repl-tabbed-panel :selection 
       (fn [e] 
         (let [num-tabs (tab-count repl-tabbed-panel)]
          ; (println "num-tabs: " num-tabs)
          (if (> 0 num-tabs)
            ;; update the current rsta  
            (swap! app-atom (fn [app] (assoc app :current-repl (current-text-area (app :repl-tabbed-panel)))))            
            ))))

    ;; add the default repl tab
    (add-tab! repl-tabbed-panel "sketchpad" repl-container)

    ; (put-meta! editor-repl repl-undo-count)
    (config! repl-in-scroll-pane :background config/app-color)
    (install-auto-completion editor-repl)
    (set-input-map! editor-repl (default-input-map))
    (config/apply-editor-prefs! config/default-editor-prefs editor-repl)
    (swap! app-atom conj (gen-map
                            repl-tabbed-panel
                            repl-que
                            editor-repl
                            repl-in-scroll-pane
                            repl-container
                            ; repl-out-writer
                            ))
    (add-repl-input-handler @app-atom)
    repl-tabbed-panel
    ))
















