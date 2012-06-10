(ns sketchpad.editor-kit
	(:import (org.fife.ui.rtextarea RTextAreaEditorKit)))

(defn editor-kit
	[rta]
	(.getEditorKit (.getUI rta) rta))

(defn delete-rest-of-line-action
"
        args:  
       flags:  :static :public :final
  interop fn:  .rtaDeleteRestOfLineAction
 return-type:  "
	[rta]
	(let [editor-kit (editor-kit rta)] 
		(org.fife.ui.rtextarea.RTextAreaEditorKit$DeleteRestOfLineAction. )))



