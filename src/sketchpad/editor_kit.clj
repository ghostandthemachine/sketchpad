(ns sketchpad.editor-kit
	(:import (org.fife.ui.rtextarea RTextAreaEditorKit)
					 (org.fife.ui.rsyntaxtextarea RSyntaxUtilities RSyntaxTextArea)))

(defn editor-kit
	[rta]
	(.getEditorKit (.getUI rta) rta))

(defn delete-rest-of-line-action
"        args:
       flags:  :static :public :final
  interop fn:  .rtaDeleteRestOfLineAction
 return-type:  "
	[rta]
	(let [editor-kit (editor-kit rta)]
		(org.fife.ui.rtextarea.RTextAreaEditorKit$DeleteRestOfLineAction. )))

(defn text
	"Fetches a portion of the text represented by the component. Returns an empty string if length is 0."
	([rta] (.getText rta))
	([rta start len]
		(.getText rta start len)))

(defn last-visible-offset
  [rta]
  "Returns the position of the last visible token in the RTextArea"
  (.getLastVisibleOffset rta))

(defn matching-bracket-position
  [rta]
  "Returns the location of the bracket paired with the one at the current caret position."
  (.getMatchingBracketPosition rta))
