(ns sketchpad.auto-complete.auto-complete
  (:use [sketchpad.auto-complete.completion-builder])
  (:require [sketchpad.config.config :as config]))

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
  (let [completion-provider (org.fife.ui.autocomplete.AutoCompletion. provider)]
    (config/apply-auto-completion-prefs! completion-provider)
    (.install completion-provider rta)))

(defn install-project-auto-completion
"Adds all project ns completions to a text area. Takes a text-area and a SketchPad project."
  [rsta  completion-provider]
    (config/apply-auto-completion-prefs! completion-provider)
    (.install completion-provider rsta))

(defn create-provider
  ([]
   (let [cp (org.fife.ui.autocomplete.ClojureCompletionProvider. )]
     (.setParameterizedCompletionParams cp \space " " \))
     cp)))

(defn build-project-completion-provider
"Builds a Completion Provider for a project."
  [project-path]
  (build-project-completions (create-provider) project-path))