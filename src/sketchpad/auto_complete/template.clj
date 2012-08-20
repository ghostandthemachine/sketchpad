(ns sketchpad.auto-complete.template
	(:import (org.fife.ui.autocomplete TemplateCompletion))
	(:require [sketchpad.auto-complete.auto-complete :as auto-complete]))

(defn add-template
  [input-str template]
  (.addCompletion auto-complete/completion-provider (TemplateCompletion. auto-complete/completion-provider input-str template)))

(do
	(add-template "fr" "(search-replace \"${search}\" \"${replace}\""))