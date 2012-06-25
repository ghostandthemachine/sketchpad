(ns sketchpad.option-windows
	(:import (javax.swing.JOptionPane)
		(javax.swing.JDialog)))


(def alert-options 
	(into-array String ["Save Changes"
 											"Discard Changes"
 											"Cancel"]))

(defn close-or-save-option [app file-to-close-name ]
	(let [close-alert (javax.swing.JOptionPane. (str "Do you want to save the changes you made to " file-to-close-name) javax.swing.JOptionPane/QUESTION_MESSAGE)
				dialog (javax.swing.JDialog. (app :frame) "Unsaved Changes")]
		(doto close-alert 
			(.setOptions alert-options))

		(doto dialog
			(.setVisible true))

		; (let [selected-value (.getValue close-alert)]
		; 	(if (= selected-value "Discard Changes")))
		))
