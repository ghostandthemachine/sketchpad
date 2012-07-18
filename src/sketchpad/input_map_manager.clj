(ns sketchpad.input-map-manager
	(:use [sketchpad rsyntaxtextarea]))


(defn set-repl-input-map [rta]
	)


(defn dumb-completion-action []
	(proxy [org.fife.ui.rtextarea.RTextAreaEditorKit$DumbCompleteWordAction] []
		(actionPerformedImpl [e rta]
			(try 
				(proxy-super actionPerformedImpl e rta)
				(catch javax.swing.text.BadLocationException e)))))

(defn add-sketchpad-dumb-completion-action [rta]
	(let [action-map (.getActionMap rta)
		  action (dumb-completion-action)]
		(.put action-map "sketchpad-dumb-completion" action)))