(ns sketchpad.buffer-edit
  (:require [clojure.string :as string]))

(defn trim-enclosing-char [s cl cr]
  (let [drop-first (string/replace s cl "")
        drop-last (string/replace drop-first cr "")]
    drop-last))

(defn trim-parens [s]
  (trim-enclosing-char s "(" ")"))

(defn trim-brackets [s]
  (trim-enclosing-char s "[" "]"))

(defn append-text [text-pane text]
  (when-let [doc (.getDocument text-pane)]
    (try
      (.insertString doc (.getLength doc) text nil)
      (catch java.lang.ClassCastException e ))))

(defn append-text-update [buffer s]
  (append-text buffer (str s))
  (.setCaretPosition buffer (.getLastVisibleOffset buffer)))
