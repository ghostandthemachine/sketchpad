(ns sketchpad.auto-complete.auto-complete
  (:import (org.fife.rsta.ac.html.HtmlCompletionProvider)
    (java.util.Vector)
    (org.fife.ui.autocomplete ShorthandCompletion))
  (:use [sketchpad.auto-complete.completion-builder])
  (:require [sketchpad.config.config :as config]
            [sketchpad.wrapper.rsyntaxtextarea :as wrapper.rsyntaxtextarea]
            [sketchpad.input.default :as input.default]
            [sketchpad.state.state :as state]
            [seesaw.core :as seesaw]
            [sketchpad.fuzzy.list-cell-renderer :as cell-renderer]
            [sketchpad.auto-complete.template :as template]))

(defn make-ac
	[provider]
	(proxy [org.fife.ui.autocomplete.AutoCompletion] [provider]
	(insertCompletion [c]
		(proxy-super insertCompletion c))))

(defn template
"A completion made up of a template with arbitrary parameters that the user
can tab through and fill in.  This completion type is useful for inserting
common boilerplate code, such as for-loops.<p>
The format of a template is similar to those in Eclipse.  The following
example would be the format for a for-loop template:

<pre>
for (int ${i} = 0; ${i} &lt; ${array}.length; ${i}++) {
   ${cursor}
}
</pre>

In the above example, the first <code>${i}</code> is a parameter for the
user to type into; all the other <code>${i}</code> instances are
automatically changed to what the user types in the first one.  The parameter
named <code>${cursor}</code> is the \"ending position\" of the template.  It's
where the caret moves after it cycles through all other parameters.  If the
user types into it, template mode terminates.  If more than one
<code>${cursor}</code> parameter is specified, behavior is undefined.<p>

Two dollar signs in a row (\"<code>$$</code>\") will be evaluated as a single
dollar sign.  Otherwise, the template parsing is pretty straightforward and
fault-tolerant.<p>
Leading whitespace is automatically added to lines if the template spans
more than one line, and if used with a text component using a
<code>PlainDocument</code>, tabs will be converted to spaces if requested.
@author Robert Futrell
@version 1.0"
	[provider input template]
	(org.fife.ui.autocomplete.TemplateCompletion. provider input template))

(defn create-completion-provider
  ([] (create-completion-provider :default))
  ([kw]
   (let [cp (org.fife.ui.autocomplete.ClojureCompletionProvider. )]
     (add-all-ns-completions cp)
     (.setParameterizedCompletionParams cp \space " " \))
;     (.setAutoActivationRules cp true "")
     cp)))

(defonce completion-provider (create-completion-provider))

(defonce default-auto-completion (org.fife.ui.autocomplete.AutoCompletion. completion-provider))
(do
	(template/install-templates default-auto-completion))


(defn install-auto-completion
  [buffer] 
  (let [provider completion-provider
  	    ac (org.fife.ui.autocomplete.AutoCompletion. provider)
        rta (get-in buffer [:component :text-area])]
    (template/install-templates ac)
    (reset! (:auto-complete buffer) ac)
    (config/apply-auto-completion-prefs! ac)
    (.install ac rta)))
;
;(defn install-project-auto-completion
;"Adds all project ns completions to a text area. Takes a text-area and a SketchPad project."
;  [rsta  completion-provider]
;    (config/apply-auto-completion-prefs! completion-provider)
;    (.install completion-provider rsta))

(defn install-html-auto-completion
  [rta]
  (let [provider (org.fife.rsta.ac.html.HtmlCompletionProvider.)
  	  ac (org.fife.ui.autocomplete.AutoCompletion. provider)]
    (config/apply-auto-completion-prefs! ac)
    (doto ac
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

(defonce clojars-provider (future (make-clojar-completion-provider)))
	
(defn install-clojars-auto-completions
"Adds all project ns completions to a text area. Takes a text-area and a SketchPad project."
  [text-area]
    (when (future-done? clojars-provider)
      (seesaw/invoke-later
    		(let [provider @clojars-provider
    			ac (org.fife.ui.autocomplete.AutoCompletion. provider)]
     			(config/apply-auto-completion-prefs! ac)
     			(wrapper.rsyntaxtextarea/set-input-map! text-area (input.default/default-input-map))
     			(doto ac
       			(.setAutoActivationEnabled true)
       			(.setDescriptionWindowSize 300 500) 
       			(.setShowDescWindow false))
       			(doto provider
			         (.setParameterizedCompletionParams \space " " \))
			         (.setAutoActivationRules true ""))
      			(.install ac text-area)))))

(defn build-project-completion-provider
"Builds a Completion Provider for a project."
  [project-path]
  (build-project-completions (create-provider) project-path))

(defonce fuzzy-provider (org.fife.ui.autocomplete.DefaultCompletionProvider. ))
(defonce fuzzy-ac (org.fife.ui.autocomplete.AutoCompletion. fuzzy-provider))

(defn- not-sufix?
  [f suffix-vec]
  (let [suffix (last (clojure.string/split (.getName f) #"\."))]
    (not (nil? (some #(= suffix %) suffix-vec)))))

(defn- in-inclusions?
  [f suffix-vec]
  (let [suffix (last (clojure.string/split (.getName f) #"\."))]
    (not (nil? (some #(= suffix %) suffix-vec)))))

(defn add-file-completion
	[project-path f]
	(let [project-name (last (clojure.string/split project-path #"/"))]
		(when (and
		      (not (= (.getName f) ".DS_Store"))
		      (in-inclusions? f config/fuzzy-file-type-inclusions))
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
  		      (str (last path-split))))))))

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
	(add-file-completion project-path f))))

(defn- init-fuzzy-ac
	[ac provider]
	(doto ac
		(.setAutoActivationEnabled true)
		(.setAutoActivationDelay 0)
		(.setDescriptionWindowSize 800 500) 
		(.setShowDescWindow false))
	  (doto provider
	     (.setParameterizedCompletionParams \space " " \))
	     (.setAutoActivationRules true "")
	     (.setListCellRenderer (cell-renderer/renderer)))
		ac)

(defn install-fuzzy-provider
  [fuzzy-buffer]
  (let [provider fuzzy-provider
        ac (org.fife.ui.autocomplete.AutoCompletion. provider)
        text-area (get-in fuzzy-buffer [:component :text-area])
        fuzzy-ac (:auto-complete fuzzy-buffer)]
      (init-fuzzy-ac ac provider)
      ; (.setListCellRenderer provider (cell-renderer/renderer))
      (swap! fuzzy-ac assoc :auto-complete ac)
    (.install ac text-area)))


(defn update-fuzzy-completions
  []
  (let [project-keys (keys @(:projects @state/app))
  	  ac (get-in @state/app [:fuzzy :auto-complete])
  	  text-area (get-in @state/app [:fuzzy :text-area])]
    (.clear fuzzy-provider)
    (.uninstall ac)
    (doseq [proj project-keys]
      (add-files-to-fuzzy-complete proj))
    (install-fuzzy-provider text-area)))
