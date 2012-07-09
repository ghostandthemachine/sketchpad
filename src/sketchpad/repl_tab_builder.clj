(ns sketchpad.repl-tab-builder
	(:import (java.awt.event KeyEvent)
					(java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
					(javax.swing JButton JOptionPane JWindow ImageIcon)
					(javax.swing.event DocumentListener))
	(:use [sketchpad project-manager buffer-edit option-windows repl utils tab-manager repl-component repl-button-tab prefs]
				[clojure pprint]
				[seesaw meta core border])
	(:require [clojure.string :as str])
	)
;;
;; create a new repl tab from an outside process
;;
 (defn new-repl-tab! 
 	([app-atom] (new-repl-tab! app-atom -1))
 	([app-atom i]
 	(let [app @app-atom
 				cur-project-path (get-current-project app-atom)
 				project-map (app :project-map)
 				tabbed-panel (app :repl-tabbed-panel)
		 		repl-component (make-repl-component app nil)
		 		rsta (select repl-component [:#editor])
		 		tab-title (str "REPL# " (+ (tab-count tabbed-panel) 1))
		 		cur-buffer (current-text-area (app :editor-tabbed-panel))
		 		cur-proj (get-meta cur-buffer :project)]
 		;; add the tab to the tabbed panel
 		(add-tab! tabbed-panel tab-title repl-component)
 		;; add the new repl to the projects map
 		(add-repl-to-project! app-atom rsta)
 		;; set custom tab
 		(let [project (@project-map cur-project-path)
 					index-of-new-tab (index-of tabbed-panel tab-title)
 					project-color (get-project-theme-color (:id project))
 					tab (repl-button-tab app tabbed-panel index-of-new-tab project-color)
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
																(do 
																	;; remove the tab from the tabbed panel
																	(remove-repl-tab! tabbed-panel idx)
																	;; remove the repl from the projects map
																	(remove-repl-from-project! app-atom rsta (get-meta rsta :project-path)))

																))))

 			;; set the component in the new tab
 			(.setTabComponentAt tabbed-panel index-of-new-tab tab)
			
 			(println "Added new repl: " tab-title)

		    ;; attach handlers
		    (add-repl-rsta-input-handler rsta)

 			;; bring the new tab to the front
 			(show-tab! tabbed-panel index-of-new-tab)))))
