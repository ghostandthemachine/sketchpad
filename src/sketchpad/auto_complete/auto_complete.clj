(ns sketchpad.auto-complete.auto-complete
  (:import (org.fife.rsta.ac.html.HtmlCompletionProvider)
    (java.util.Vector)
    (org.fife.ui.autocomplete ShorthandCompletion))
  (:use [sketchpad.auto-complete.completion-builder])
  (:require [sketchpad.config.config :as config]
            [sketchpad.wrapper.rsyntaxtextarea :as wrapper.rsyntaxtextarea]
            [sketchpad.input.default :as input.default]
            [sketchpad.state.state :as state]
            [sketchpad.auto-complete.template :as template]))

(defn create-completion-provider
  ([] (create-completion-provider :default))
  ([kw]
   (let [cp (org.fife.ui.autocomplete.ClojureCompletionProvider. )]
     (add-all-ns-completions cp)
     (.setParameterizedCompletionParams cp \space " " \))
     (.setAutoActivationRules cp true "")
     cp)))

(defonce completion-provider (create-completion-provider))

(defonce default-auto-completion (org.fife.ui.autocomplete.AutoCompletion. completion-provider))
(do
	(template/install-templates default-auto-completion))


(defn install-auto-completion
  [rta] 
    (template/install-templates default-auto-completion)
    (config/apply-auto-completion-prefs! default-auto-completion)
    (.install default-auto-completion rta))

(defn install-project-auto-completion
"Adds all project ns completions to a text area. Takes a text-area and a SketchPad project."
  [rsta  completion-provider]
    (config/apply-auto-completion-prefs! completion-provider)
    (.install completion-provider rsta))

(defn install-html-auto-completion
  [rta]
  (let [completion-provider (org.fife.ui.autocomplete.AutoCompletion. (org.fife.rsta.ac.html.HtmlCompletionProvider.))]
    (config/apply-auto-completion-prefs! completion-provider)
    (doto completion-provider
      (.install rta))))

(defn create-provider
  ([]
   (let [cp (org.fife.ui.autocomplete.DefaultCompletionProvider.)]
     (.setParameterizedCompletionParams cp \space " " \))
     (.setAutoActivationRules cp true "")
     cp)))

(defn make-clojar-completion-provider
"Builds a Completion Provider from the available repo on Clojars."
	[]
	(build-clojar-completions (create-provider)))

(def clojar-completion-provider (org.fife.ui.autocomplete.AutoCompletion. (make-clojar-completion-provider)))

(defn install-clojars-auto-completions
"Adds all project ns completions to a text area. Takes a text-area and a SketchPad project."
  [text-area]
    (config/apply-auto-completion-prefs! clojar-completion-provider)
    (wrapper.rsyntaxtextarea/set-input-map! text-area (input.default/default-input-map))
    (doto 
      clojar-completion-provider
      (.setAutoActivationEnabled true)
      (.setDescriptionWindowSize 300 500) 
      (.setShowDescWindow false))
    (.install clojar-completion-provider text-area))

(defn build-project-completion-provider
"Builds a Completion Provider for a project."
  [project-path]
  (build-project-completions (create-provider) project-path))

(defonce fuzzy-provider (org.fife.ui.autocomplete.DefaultCompletionProvider. ))

(defn not-sufix?
  [f suffix-vec]
  (let [suffix (last (clojure.string/split (.getName f) #"\."))]
    (not (nil? (some #(= suffix %) suffix-vec)))))

(defn add-files-to-fuzzy-complete
  [project-path]
  (let [project-name (last (clojure.string/split project-path #"/"))
        completions (java.util.Vector. )
        directory (clojure.java.io/file project-path)
        files (filter 
                (fn [file]
                  (and
                    (.isFile file)
                    (nil? (re-find #"\.\w+/" (.getAbsolutePath file)))
                    (nil? (re-find #"\.pygments-cache/" (.getAbsolutePath file)))))
                (file-seq directory))]
    (doseq [f files]
      (when (and
              (not (= (.getName f) ".DS_Store"))
              (not (not-sufix? f config/fuzzy-file-type-exclusions))
        (let [path-split (clojure.string/split (.getAbsolutePath f) (java.util.regex.Pattern/compile project-name) 2)]
          (.addCompletion fuzzy-provider
            (ShorthandCompletion. fuzzy-provider 
              (str (.getName f))
              (str "{"
                    ":file " "\"" (last path-split) "\""
                    " "
                    ":project " "\"" (first path-split) project-name "\""
                    " "
                    ":absolute-path " "\"" (.getAbsolutePath f) "\""
                    "}")
              (str (last path-split))))))))))

(defn update-fuzzy-completions
  []
  (let [project-keys (keys @(:projects @state/app))]
    (.clear fuzzy-provider)
    (doseq [proj project-keys]
      (add-files-to-fuzzy-complete proj))))

(defonce fuzzy-auto-completetion (org.fife.ui.autocomplete.AutoCompletion. fuzzy-provider))

(defn- init-fuzzy-ac
	[]
	(doto 
	  fuzzy-auto-completetion
  	(.setAutoActivationEnabled true)
	  (.setDescriptionWindowSize 300 500) 
	  (.setShowDescWindow false)))

(do (init-fuzzy-ac))
	

(defn install-fuzzy-provider
  [rsta]
  (.install fuzzy-auto-completetion rsta))




