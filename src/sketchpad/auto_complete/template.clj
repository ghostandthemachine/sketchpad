(ns sketchpad.auto-complete.template
	(:import (org.fife.ui.autocomplete TemplateCompletion)
						(org.fife.ui.rsyntaxtextarea CodeTemplateManager)
						(org.fife.ui.rsyntaxtextarea RSyntaxTextArea)
						(org.fife.ui.rsyntaxtextarea.templates StaticCodeTemplate)))

(defn install-templates
	[ac]
	(.addCompletion (.getCompletionProvider ac) (TemplateCompletion. (.getCompletionProvider ac) "sr" "(search-replace \"${search}\" \"${replace}\"")))

  