(ns sketchpad.repl.print
  (:require [clojure.string :as string]
            [sketchpad.buffer.action :as buffer.action]))

(defn prompt [rsta]
  (buffer.action/append-text rsta (str \newline (ns-name *ns*) "=> "))
  (.setCaretPosition rsta (.getLastVisibleOffset rsta)))

(defn pln [rsta & values]
  (buffer.action/append-text rsta (str values \newline)))