(ns sketchpad.option-windows
	(:import (javax.swing JButton JOptionPane JWindow)
		(javax.swing.JDialog)))


(def alert-options 
	(into-array String ["Save Changes"
 											"Discard Changes"
 											"Cancel"]))


;; display a save, discard, or cancel dialogue if the window being closed is dirty
(defn close-or-save-current-dialogue [title]
 (when-let [answer (JOptionPane/showConfirmDialog 
 					 nil
           ; (label :text (str "Do you want to save the changes you made to " title)
           ; 				:icon (ImageIcon. "img/sketchpad-icon.png")) 
 					 (str "Do you want to save the changes you made to " title)
           "Unsaved Changes"
           JOptionPane/YES_NO_CANCEL_OPTION
           )]
 	answer))
