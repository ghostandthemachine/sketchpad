(ns sketchpad.option-windows
	(:import (javax.swing JButton JOptionPane JWindow)
		(javax.swing.JDialog)))

(def alert-options 
	(into-array String ["Save Changes"
 											"Discard Changes"
 											"Cancel"]))

(defn close-or-save-current-dialogue [title]
 (when-let [answer (JOptionPane/showConfirmDialog 
 					 nil
 					 (str "Do you want to save the changes you made to " title)
           "Unsaved Changes"
           JOptionPane/YES_NO_CANCEL_OPTION)]
 	answer))

(defn close-repl-dialogue []
 (when-let [answer (JOptionPane/showConfirmDialog 
 					 nil
 					 (str 
 					 	"Are you sure you want to close this RELP?\n" 
 					 	"This will destroy this REPL's process.")
           "Close REPL"
           JOptionPane/YES_NO_OPTION)]
 	answer))
