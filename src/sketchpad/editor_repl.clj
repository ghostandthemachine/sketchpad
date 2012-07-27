(ns sketchpad.editor-repl
  (:require [clojure.string :as string]
    [sketchpad.buffer.action :as buffer.action])
  (:use [sketchpad repl-communication sketchpad-repl]
        [seesaw meta])
  (:import (java.util.concurrent LinkedBlockingDeque)))
  
(def editor-repl-history {:items (atom (list "")) :pos (atom 0)}) 

(defn sketchpad-reader [q prompt exit]
    (read-string (.take q)))

(defn sketchpad-prompt [rsta]
  (buffer.action/append-text rsta (str \newline (ns-name *ns*) "=> "))
  (.setCaretPosition rsta (.getLastVisibleOffset rsta)))

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

      