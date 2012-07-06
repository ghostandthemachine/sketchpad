(ns sketchpad.repl-tab-builder
	(:import (java.awt.event KeyEvent)
					(java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
					(javax.swing JButton JOptionPane JWindow ImageIcon)
					(javax.swing.event DocumentListener))
	(:use [sketchpad option-windows repl utils tab-manager repl-component repl-button-tab prefs]
				[clojure pprint]
				[seesaw meta core border])
	(:require [clojure.string :as str])
	)
;;
;; create a new repl tab from an outside process
;;
 (defn new-repl-tab! 
 	([app] (new-repl-tab! app -1))
 	([app i]
 	(let [tabbed-panel (app :repl-tabbed-panel)
		 		repl-component (make-repl-component app nil)
		 		rsta (select repl-component [:#editor])
		 		tab-title (str "REPL# " (+ (tab-count tabbed-panel) 1))]
 		(add-tab! tabbed-panel tab-title repl-component)
 		;; set custom tab
 		(let [index-of-new-tab (index-of tabbed-panel tab-title)
 					tab (repl-button-tab app tabbed-panel index-of-new-tab)
 					close-button (select tab [:#close-button])
 					tab-label (first (select tab [:.tab-label]))]
 					;; link the tab for this component for updating clean indicator
 					(put-meta! rsta :tab tab)
 			;; add tab listeners here because they use tab-manager functions and we need
 			;; to include the button tab ns. This avoids the cyclic loading
 			(listen close-button :mouse-entered (fn [e] (swap! current-tab-color (fn [_] mouse-over-color))))
 			(listen close-button :mouse-exited (fn [e] (swap! current-tab-color (fn [_] base-color))))
 			(listen close-button :mouse-clicked (fn [e] (let [idx (index-of-component tabbed-panel repl-component)
 															  yes-no-option (close-repl-dialogue)]
															(if (= yes-no-option 0)
																(remove-repl-tab! tabbed-panel idx)

																))))

 			;; set the component in the new tab
 			(.setTabComponentAt tabbed-panel index-of-new-tab tab)
			
 			(println "Added new repl: " tab-title)

		    ;; attach handlers
		    (add-repl-rsta-input-handler rsta)

 			;; bring the new tab to the front
 			(show-tab! tabbed-panel index-of-new-tab)))))
