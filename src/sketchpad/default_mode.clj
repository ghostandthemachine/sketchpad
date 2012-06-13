(ns sketchpad.default-mode
	(:use [sketchpad.edit-mode])
	(:import (java.awt Toolkit)
			 (java.awt.event InputEvent KeyEvent)
			 (javax.swing InputMap KeyStroke)
			 (javax.swing.text DefaultEditorKit)
			 (org.fife.ui.rtextarea RTextAreaEditorKit)
			 (org.fife.ui.rsyntaxtextarea RSyntaxTextAreaDefaultInputMap)))

(defn default-input-map
	[]
	"Extend the Swing InputMap class and implement key mappings"
	(println "Create default input map")
	(let [alt (InputEvent/ALT_MASK)
		  shift (InputEvent/SHIFT_MASK)
		  input-map (RSyntaxTextAreaDefaultInputMap. )]
		(doto input-map
			; toggle vim mode
			(.put
				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_ESCAPE
				(int 0))
				 "toggle-vim-mode"))
	input-map))
