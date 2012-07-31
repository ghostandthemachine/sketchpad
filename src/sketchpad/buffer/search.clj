(ns sketchpad.buffer.search
  (:use [sketchpad.wrapper.search-context]
        [seesaw.core :only [invoke-later]])
  (:require [sketchpad.wrapper.search-engine :as search]))

(defn search
  [rta search-str]
  (invoke-later
    (cond
      (= java.lang.String (type search-str))
      (do
        (let [context (search-context search-str)]
          (let [finder (search/find rta context)]
            finder)))
      (= java.util.regex.Pattern (type search-str))
      (do
        (let [context (search-context (str search-str))]
          (regular-expression! context true)
          (let [finder (search/find rta context)]
            finder))))))

(defn search-replace
  [rta search-str replace-str]
  (invoke-later
    (cond
      (= java.lang.String (type search-str))
      (do
        (let [context (search-context search-str)]
          (replace-with! context replace-str)
          (let [finder (search/replace rta context)]
            finder)))
      (= java.util.regex.Pattern (type search-str))
      (let [context (search-context (str search-str))]
        (regular-expression! context true)
        (replace-with! context replace-str)
        (let [finder (search/replace rta context)]
          finder)))))

(defn search-replace-all
  [rta search-str replace-str]
  (invoke-later
    (cond
      (= java.lang.String (type search-str))
      (do
        (let [context (search-context search-str)]
          (replace-with! context replace-str)
          (let [finder (search/replace-all rta context)]
            finder)))
      (= java.util.regex.Pattern (type search-str))
      (do
        (let [context (search-context (str search-str))]
          (regular-expression! context true)
          (replace-with! context replace-str)
          (let [finder (search/replace-all rta context)]
            finder))))))






