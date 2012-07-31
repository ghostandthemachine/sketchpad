(ns sketchpad.repl.app.repl
  (:require [clojure.string :as string]
    [sketchpad.buffer.action :as buffer.action])
  (:use [sketchpad.repl.app.sketchpad-repl]
        [seesaw core meta]
        [sketchpad.wrapper.rsyntaxtextarea])
  (:import (java.util.concurrent LinkedBlockingDeque)))
  
(def editor-repl-history {:items (atom (list "")) :pos (atom 0)}) 

(defn offer! 
  "adds x to the back of queue q"
  [q x] (.offer q x) q)

(defn take! 
  "takes from the front of queue q.  blocks if q is empty"
  [q] (.take q))

(defn- get-classpath []
   (sort (map (memfn getPath) 
              (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader))))))
    
(defn get-last-cmd [buffer]
  (let [text (config buffer :text)]
   (string/trim (last (string/split text #"=>")))))

(defn clear-repl-input [rsta]
  (let [end (last-visible-offset rsta)
        trim-str (get-last-cmd rsta)
        start (- end (count trim-str))]
    (replace-range! rsta nil start end)))

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

(defn send-to-editor-repl
  ([rsta cmd] (send-to-editor-repl rsta cmd "NO_SOURCE_PATH" 0) :repl)
  ([rsta cmd file line] (send-to-editor-repl rsta cmd file line :file))
  ([rsta cmd file line src-key]
   (invoke-later 
    (let [repl-history (get-meta rsta :repl-history)
          cmd-ln (str \newline (.trim cmd) \newline)
          cmd-trim (.trim cmd)]
      ;; go to next line
      (buffer.action/append-text-update rsta (str \newline))
      (let [cmd-str (cmd-attach-file-and-line cmd file line)
            repl-history (get-meta rsta :repl-history)]
        (offer! (get-meta rsta :repl-que) cmd-str))
      (when-let [items (repl-history :items)]
        (when (not= cmd-trim (first @items))
          (swap! items
                 replace-first cmd-trim)
          (swap! items conj ""))
        (swap! (repl-history :pos) (fn [pos] 0)))))))

(defn sketchpad-reader [q prompt exit]
    (read-string (.take q)))

(defn sketchpad-prompt [rsta]
  (buffer.action/append-text rsta (str \newline (ns-name *ns*) "=> "))
  (.setCaretPosition rsta (.getLastVisibleOffset rsta))
  (.discardAllEdits rsta))

(defn sketchpad-printer [rsta value]
  (buffer.action/append-text rsta (str value)))

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

(defn append-history-text [rsta m]
  (let [pos @(m :pos)
        history-str (string/trim (str (nth @(m :items) pos)))]
    (buffer.action/append-text-update rsta history-str)))

(defn update-repl-history-display-position [rsta kw]
  (let [repl-history (get-meta rsta :repl-history)
        history-pos (repl-history :pos)
        cmd (get-last-cmd rsta)]
    (if (= @(repl-history :pos) 0)
      (when-let [items (repl-history :items)]
          (swap! items replace-first cmd)))
    (cond 
      (= kw :dec)
        (if (< @history-pos (- (count @(repl-history :items)) 1))
            (swap! history-pos (fn [pos] (+ pos 1)))
            (swap! history-pos (fn [pos] pos)))
      (= kw :inc)
        (if (> @history-pos 1)
            (swap! history-pos (fn [pos] (- pos 1)))
            (swap! history-pos (fn [pos] pos))))
    (clear-repl-input rsta)
    (append-history-text rsta repl-history)))

      