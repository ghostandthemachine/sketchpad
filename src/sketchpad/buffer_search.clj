(ns sketchpad.buffer-search
  (:use [sketchpad search-context])
  (:require [sketchpad.search-engine :as search]
            [sketchpad.tab-manager :as sketchpad.tab-manager]))

(defn search
  [rta search-str]
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
          finder)))))

(defn search-replace
  [rta search-str replace-str]
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
        finder))))

(defn search-replace-all
  [rta search-str replace-str]
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
          finder)))))






