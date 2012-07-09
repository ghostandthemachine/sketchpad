(ns sketchpad.repl
  (:import (java.io
             BufferedReader BufferedWriter
             InputStreamReader
             File PipedReader PipedWriter PrintWriter Writer
                    StringReader PushbackReader)
           (clojure.lang LineNumberingPushbackReader)
           (java.awt Rectangle)
           (java.net URL URLClassLoader URLDecoder))
  (:use [sketchpad.utils :only (attach-child-action-keys attach-action-keys
                            gen-map get-temp-file awt-event get-file-ns
                            when-lets get-text-str get-directories)]
        [clooj.brackets :only (find-line-group find-enclosing-brackets)]
        [clooj.help :only (get-var-maps)]
        [clj-inspector.jars :only (get-entries-in-jar jar-files)]
        [seesaw core color border meta]
        [sketchpad buffer-edit config repl-communication editor-repl rsyntaxtextarea tab-manager auto-complete app-cmd default-mode sketchpad-repl]
        [clojure.tools.nrepl.server :only (start-server stop-server)])
  (:require [clojure.string :as string]
            [sketchpad.rsyntax :as rsyntax]
            [clojure.java.io :as io]
            [sketchpad.editor-kit :as kit]
            [sketchpad.config :as config]
            [sketchpad.repl-tab-ui :as rtab]
            [sketchpad.rtextscrollpane :as sp]
            [clojure.tools.nrepl :as repl])
  (:import (org.fife.ui.rtextarea RTextScrollPane)
           (java.io.IOException)))

(use 'clojure.java.javadoc)

(def repl-history {:items (atom nil) :pos (atom 0) :last-end-pos (atom 0)})


(def repl-history (atom {}))	

; (defn creatae-new-repl-history [])

(def repls (atom {}))

; (defn offer! 
;   "adds x to the back of queue q"
;   [q x] (.offer q x) q)

; (defn take! 
;   "takes from the front of queue q.  blocks if q is empty"
;   [q] (.take q))

(def ^:dynamic *printStackTrace-on-error* false)

; (defn tokens
;   "Finds all the tokens in a given string."
;   [text]
;   (re-seq #"[\w/\.]+" text))

; (defn namespaces-from-code
;   "Take tokens from text and extract namespace symbols."
;   [text]
;   (->> text tokens (filter #(.contains % "/"))
;        (map #(.split % "/"))
;        (map first)
;        (map #(when-not (empty? %) (symbol %)))
;        (remove nil?)))

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

(defn create-outside-repl
  "This function creates an outside process with a clojure repl."
  [result-writer project-path]
  (let [clojure-jar (clojure-jar-location project-path)
        java (str (System/getProperty "java.home")
                  File/separator "bin" File/separator "java")
        classpath (outside-repl-classpath project-path)
        _ (println classpath)
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


; (defn replace-first [coll x]
;   (cons x (next coll)))

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

; (defn read-string-at [source-text start-line]
;   `(let [sr# (java.io.StringReader. ~source-text)
;          rdr# (proxy [clojure.lang.LineNumberingPushbackReader] [sr#]
;                (getLineNumber []
;                               (+ ~start-line (proxy-super getLineNumber))))]
;      (take-while #(not= % :EOF_REACHED)
;                  (repeatedly #(try (read rdr#)
;                                    (catch Exception e# :EOF_REACHED))))))


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
            (append-text (app :doc-text-area) (str \newline))
          (= src-key :file)
            (append-text (app :doc-text-area) (str \newline))
            ; (append-text rsta cmd-ln)
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

; (defn make-repl-writer [ta-out]
;   (->
;     (let [buf (agent (StringBuffer.))]
;       (proxy [Writer] []
;         (write
;           ([char-array offset length]
;             ; (println "char array:" (apply str char-array) (count char-array))
;             (awt-event 
;               (append-text-update ta-out (apply str char-array))
;               ; (swap! (:last-end-pos repl-history) (fn [_] (.getLastVisibleOffset ta-out)))
;               ))
;           ([^Integer t]
;             ; (println "Integer: " t (type t))
;             (awt-event (append-text-update ta-out (str (char t))))))
;         (flush [] (awt-event (scroll-to-last ta-out)))
;         (close [] nil)))
;     (PrintWriter. true)))
  
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

(defn add-repl-input-handler [rsta]
  (let [ta-in rsta
  			editor-repl-history (get-meta rsta :repl-history)
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
        submit #(when-let [txt (get-last-cmd rsta)]
                  (let [pos (editor-repl-history :pos)]
                    (if (correct-expression? txt)
                      (do 
                        (send-to-editor-repl rsta txt)
                        (swap! pos (fn [p] 0))
;                        (.setText (app :arglist-label) "Malformed expression")
                        ))))

        at-top #(zero? (.getLineOfOffset ta-in (get-caret-pos)))
        at-bottom #(= (.getLineOfOffset ta-in (get-caret-pos))
                      (.getLineOfOffset ta-in (.. ta-in getText length)))
        prev-hist #(update-repl-history-display-position ta-in :dec)
        next-hist #(update-repl-history-display-position ta-in :inc)]

        ; (println editor-repl-history)
    (attach-child-action-keys ta-in ;["control UP" at-top prev-hist]
                                    ;["control DOWN" at-bottom next-hist]
                                    ["ENTER" ready submit])
    (attach-action-keys ta-in ["cmd1 UP" prev-hist]
                              ["cmd1 DOWN" next-hist])
                              ; ["cmd1 ENTER" submit] ;; blocks dumb completion
                              ; )
    ))


(defn add-repl-rsta-input-handler [rsta]
  (let [ta-in rsta
        editor-repl-history (get-meta rsta :repl-history)
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
        submit #(when-let [txt (get-last-cmd rsta)]
                  (if (correct-expression? txt)
                    (do 
                      (send-to-project-repl rsta txt)
                      (swap! (editor-repl-history :pos) (fn [pos] 0)))))

        at-top #(zero? (.getLineOfOffset ta-in (get-caret-pos)))
        at-bottom #(= (.getLineOfOffset ta-in (get-caret-pos))
                      (.getLineOfOffset ta-in (.. ta-in getText length)))
        prev-hist #(update-repl-history-display-position rsta :dec)
        next-hist #(update-repl-history-display-position rsta :inc)
        ]
    (attach-child-action-keys ta-in ;["control UP" at-top prev-hist]
                                    ;["control DOWN" at-bottom next-hist]
                                    ["ENTER" ready submit])
    (attach-action-keys ta-in ["cmd1 UP" prev-hist]
                              ["cmd1 DOWN" next-hist])
                              ;["cmd1 ENTER" submit] ;; blocks dumb completion
                              ; )
    ))

(defn print-stack-trace [app]
    (send-to-repl app "(.printStackTrace *e)"))

(defn repl
  [app-atom]
  (let [repl-tabbed-panel   (tabbed-panel :placement :top
                                            :overflow :wrap
                                            :background (color :black)
                                            :border nil)
        editor-repl (rsyntax/text-area 
                                      :syntax          :clojure    
                                      :border          nil                          
                                      :id             :editor
                                      :class          :repl)

			 	repl-history {:items (atom nil) :pos (atom 0) :last-end-pos (atom 0)}
                                      
        ; repl-out-writer   (make-repl-writer editor-repl app-atom)
        repl-in-scroll-pane (RTextScrollPane. editor-repl false) ;; default to no linenumbers
        repl-container (vertical-panel 
                                      :items          [repl-in-scroll-pane]                                      
                                      :id             :repl-container
                                      :class          :repl)
        repl-undo-count (atom 0)
        repl-que (create-editor-repl editor-repl)]

		;; setup editor repl component
 		(put-meta! editor-repl :repl-history repl-history)
 		(put-meta! editor-repl :repl-que repl-que)
            ;; set tab ui
    (.setUI repl-tabbed-panel (rtab/sketchpad-repl-tab-ui repl-tabbed-panel))
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
    ;;input map
    (set-input-map! editor-repl (default-input-map))
    ;; attach handlers
    (add-repl-input-handler editor-repl)
    ;; auto completion
    (install-auto-completion editor-repl)
    ;; apply config prefs
    (config! repl-in-scroll-pane :background config/app-color)
    (config/apply-editor-prefs! config/default-editor-prefs editor-repl)
    ; (load-initial-editor-ns editor-repl)
    (swap! app-atom conj (gen-map
                            repl-tabbed-panel
                            repl-que
                            editor-repl
                            repl-in-scroll-pane
                            repl-container
                            ))
    repl-tabbed-panel
    ))
















