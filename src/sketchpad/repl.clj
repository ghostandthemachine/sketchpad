; Copyright (c) 2011, Arthur Edelstein
; All rights reserved.
; Eclipse Public License 1.0
; arthuredelstein@gmail.com

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
                            awt-event get-file-ns
                            append-text when-lets get-text-str get-directories)]
        [clooj.brackets :only (find-line-group find-enclosing-brackets)]
        [sketchpad.help :only (get-var-maps)]
        [sketchpad.utils :only (gen-map get-temp-file)]
        [clj-inspector.jars :only (get-entries-in-jar jar-files)]
        [seesaw.core] 
        [seesaw.color]
        [seesaw.border]
        [sketchpad app-cmd auto-complete default-mode rsyntaxtextarea tab-manager])
  (:require [clojure.string :as string]
            [clooj.rsyntax :as rsyntax]
            [clojure.java.io :as io]
            [sketchpad.editor-kit :as kit]
            [sketchpad.config :as config])
  (:import (org.fife.ui.rtextarea RTextScrollPane)
           (java.io.IOException)))

(use 'clojure.java.javadoc)

(def repl-history {:items (atom nil) :pos (atom 0) :last-end-pos (atom 0)})

(def repls (atom {}))

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
  
    ; (println clojure-jar-term)

    (filter identity [(str project-path "/lib/*")
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

(defn replace-first [coll x]
  (cons x (next coll)))

(defn update-repl-history [app]
  (swap! (:items repl-history) replace-first
         (get-text-str (app :repl-in-text-area))))

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
           
; (defn send-to-repl
;   ([app cmd] (send-to-repl app cmd "NO_SOURCE_PATH" 0))
;   ([app cmd file line]
;   (awt-event
;     (let [cmd-ln (str \newline (.trim cmd) \newline)
;           cmd-trim (.trim cmd)]
;       (append-text (app :repl-out-text-area) cmd-ln)
;       (let [cmd-str (cmd-attach-file-and-line cmd file line)]
;         (binding [*out* (:input-writer @(app :repl))]
;           (println cmd-str)
;           (flush)))
;       (when (not= cmd-trim (second @(:items repl-history)))
;         (swap! (:items repl-history)
;                replace-first cmd-trim)
;         (swap! (:items repl-history) conj ""))
;       (reset! (:pos repl-history) 0)))))

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
            (append-text (app :repl-in-text-area) (str \newline))
          (= src-key :file)
            (append-text (app :repl-in-text-area) cmd-ln))
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
  
(defn update-repl-in [app]
  (when (pos? (count @(:items repl-history)))
    (.setText (:repl-in-text-area app)
              (nth @(:items repl-history) @(:pos repl-history)))))

(defn show-previous-repl-entry [app]
  (when (zero? @(:pos repl-history))
    (update-repl-history app))
  (swap! (:pos repl-history)
         #(min (dec (count @(:items repl-history))) (inc %)))
  (update-repl-in app))

(defn show-next-repl-entry [app]
  (when (pos? @(:pos repl-history))
    (swap! (:pos repl-history)
           #(Math/max 0 (dec %)))
    (update-repl-in app)))

(defn load-file-in-repl [app]
  (when-lets [f0 @(:current-file app)
              f (or (get-temp-file f0) f0)]
    (send-to-repl app (str "(load-file \"" (.getAbsolutePath f) "\")"))))

(defn apply-namespace-to-repl [app]
  (try 
    (when-let [current-ns (get-file-ns (.getText (current-text-area app)))]
      (println "apply-namespace-to-repl current-ns: " current-ns)
      (send-to-repl app (str "(ns " current-ns ")"))
      (swap! repls assoc-in
             [(-> app :repl deref :project-path) :ns]
             current-ns))
    (catch java.lang.IllegalArgumentException e
      (println "Illegal Argument Error: could not load file namespace into the repl"))))

; (defn restart-repl [app project-path]
;   (append-text (app :repl-out-text-area)
;                (str "\n=== RESTARTING " project-path " REPL ===\n"))
;   (when-let [proc (-> app :repl deref :proc)]
;     (.destroy proc))
;   (reset! (:repl app) (create-outside-repl (app :repl-out-writer) project-path))
;   (apply-namespace-to-repl app))

(defn restart-repl [app project-path]
  (append-text (app :repl-in-text-area)
               (str "\n=== RESTARTING " project-path " REPL ===\n"))
  (when-let [proc (-> app :repl deref :proc)]
    (.destroy proc))
  (reset! (:repl app) (create-outside-repl (app :repl-out-writer) project-path))
  (apply-namespace-to-repl app))

; (defn switch-repl [app project-path]
;   (when (and project-path
;              (not= project-path (-> app :repl deref :project-path)))
;     (append-text (app :repl-out-text-area)
;                  (str "\n\n=== Switching to " project-path " REPL ===\n"))
;     (let [repl (or (get @repls project-path)
;                    (create-outside-repl (app :repl-out-writer) project-path))]
;       (reset! (:repl app) repl))))

(defn switch-repl [app project-path]
  (when (and project-path
             (not= project-path (-> app :repl deref :project-path)))
    (append-text (app :repl-in-text-area)
                 (str "\n\n=== Switching to " project-path " REPL ===\n"))
    (let [repl (or (get @repls project-path)
                   (create-outside-repl (app :repl-out-writer) project-path))]
      (reset! (:repl app) repl))))

(defn get-last-cmd [app]
  (let [rta (app :repl-in-text-area)
        last-char (kit/text rta (kit/last-visible-offset rta) 1)
        start (if (= ")" last-char)
                ;; if we are ending with a close paren then eval 
                ;; from the last matching opening paren
                (kit/matching-bracket-position rta)
                ;; otherwise go for hack match of last pos
                ;; FIX THIS 
                @(:last-end-pos repl-history))
        len (- (kit/last-visible-offset rta) start)]
        (kit/text rta start len)))

(defn add-repl-input-handler [app]
  (let [ta-in (app :repl-in-text-area)
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
        submit #(when-let [txt (get-last-cmd app)]
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
                              (send-to-repl app txt)
                              )))
                      :else
                        (.setText (app :arglist-label) "Malformed expression"))))
        at-top #(zero? (.getLineOfOffset ta-in (get-caret-pos)))
        at-bottom #(= (.getLineOfOffset ta-in (get-caret-pos))
                      (.getLineOfOffset ta-in (.. ta-in getText length)))
        prev-hist #(show-previous-repl-entry app)
        next-hist #(show-next-repl-entry app)]
    (attach-child-action-keys ta-in ["control UP" at-top prev-hist]
                                    ["control DOWN" at-bottom next-hist]
                                    ["ENTER" ready submit])
    ; (attach-action-keys ta-in ["cmd1 UP" prev-hist]
    ;                           ["cmd1 DOWN" next-hist]
    ;                           ["cmd1 ENTER" submit])
    ))

(defn print-stack-trace [app]
    (send-to-repl app "(.printStackTrace *e)"))

;; view

(defn repl
  [app-atom]
  (let [
        repl-in-text-area (rsyntax/text-area 
                                      :syntax         "clojure"     
                                      :border         (line-border 
                                                            :thickness 4
                                                            :color (color "#FFFFFF" 0))                                
                                      :id             :repl-text-area
                                      :class          [:repl :syntax-editor])
        repl-out-writer   (make-repl-writer repl-in-text-area app-atom)
        repl-in-scroll-pane (RTextScrollPane. repl-in-text-area false) ;; default to no linenumbers
        repl-input-vertical-panel (vertical-panel 
                                      :items          [repl-in-scroll-pane]                                      
                                      :id             :repl-input-vertical-panel
                                      :class          :repl)]
    (config! repl-in-scroll-pane :background config/app-color)
    (install-auto-completion repl-in-text-area)
    ;; set default input map
    (set-input-map! repl-in-text-area (default-input-map))
    (swap! app-atom conj (gen-map
                            ; repl-out-scroll-pane
                            ; repl-out-text-area
                            ; repl-split-pane
                            repl-in-text-area
                            repl-in-scroll-pane
                            repl-input-vertical-panel
                            repl-out-writer))
    repl-in-scroll-pane))
