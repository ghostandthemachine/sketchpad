(ns sketchpad.repl.history
	(:require [sketchpad.buffer.action :as buffer]
            [clojure.string :as string]
            [sketchpad.wrapper.rsyntaxtextarea :as rsta]))

(defn replace-first [coll x]
  (cons x (next coll)))

(defn history []
	{:items (atom nil) :pos (atom 0) :last-end-pos (atom 0)})

(defn update-repl-history
	[history cmd]
	(let [items (history :items)]
		(when (not= cmd (first @items))
			(swap! items replace-first cmd)
			(swap! items conj ""))
		(swap! (history :pos) (fn [pos] 0))))

(defn clear-repl-input [text-area]
  (let [end (rsta/last-visible-offset text-area)
        trim-str (buffer/get-last-cmd text-area)
        start (- end (count trim-str))]
    (rsta/replace-range! text-area nil start end)))


(defn append-history-text [text-area m]
  (let [pos @(m :pos)
        history-str (string/trim (str (nth @(m :items) pos)))]
    (buffer/append-text text-area history-str)))

(defn update-repl-history-display-position [repl kw]
  (let [repl-history (:history repl)
        history-pos (repl-history :pos)
        text-area (:text-area repl)
        cmd (buffer/get-last-cmd text-area)]
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
    (clear-repl-input text-area)
    (append-history-text text-area repl-history)))