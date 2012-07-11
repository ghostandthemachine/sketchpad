(ns sketchpad.auto-complete
	(:use [sketchpad completion-builder])
	(:require [sketchpad.config :as config]))

(defn create-completion-provider
  ([] (create-completion-provider :default))
  ([kw]
  (let [cp (org.fife.ui.autocomplete.ClojureCompletionProvider. )]
	  (add-all-ns-completions cp)
    (.setParameterizedCompletionParams cp \space " " \))
     cp)))
     
(def provider (create-completion-provider))

(defn install-auto-completion
  [rta]
  (let [auto-complete (org.fife.ui.autocomplete.AutoCompletion. provider)]
    (config/apply-auto-completion-prefs! config/default-auto-completion-prefs auto-complete)
    (.install auto-complete rta)))