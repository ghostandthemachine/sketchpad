(ns sketchpad.repl.print
  (:require [clojure.string :as string]
            [sketchpad.buffer.action :as buffer.action]))

(defn prompt [rsta]
  (buffer.action/append-text rsta (str \newline (ns-name *ns*) "=> "))
  (.setCaretPosition rsta (.getLastVisibleOffset rsta)))

(defn pln [rsta value]
  (buffer.action/append-text rsta (str value \newline)))