(ns sketchpad.buffer.spell-check
	(:require [sketchpad.config.config :as config])
	(:import (org.fife.ui.rsyntaxtextarea.spell SpellingParser)))

(defonce dic-resource (clojure.java.io/resource "english_dic.zip"))
; (def dr (clojure.java.io/resource "english_dic.zip"))

(println dic-resource)
(println (type dic-resource))
(println (.getFile dic-resource))
; (println (clojure.java.io/file dic-resource))



; (defonce english-dic-zip (clojure.java.io/file dic-resource))

; (defonce english-spell-checker (SpellingParser/createEnglishSpellingParser english-dic-zip true))
; (do
; 	(config/apply-spell-checker-prefs! english-spell-checker))


(defn add-english-spell-checker
"Add an English language spell checker to a given RSyntaxTextArea."
	[rsta]
	; (.addParser rsta english-spell-checker)
	)

(defn remove-english-spell-checker
"Add an English language spell checker to a given RSyntaxTextArea."
	[rsta]
	; (.removeParser rsta english-spell-checker)
	)