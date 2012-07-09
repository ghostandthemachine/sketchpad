(ns sketchpad.repl-tab-builder
	(:import (java.awt.event KeyEvent)
					(java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
					(javax.swing JButton JOptionPane JWindow ImageIcon)
					(javax.swing.event DocumentListener))
	(:use [sketchpad project-manager buffer-edit option-windows repl utils tab-manager repl-component repl-button-tab prefs]
				[clojure pprint]
				[seesaw meta core border color])
	(:require [clojure.string :as str])
	)

(def mouse-over-color (color 200 200 200))
(def base-color (color 150 150 150))
(def pressed-color (color 255 255 255))

(def current-tab-color (atom base-color))

(defn add-mouse-handlers [app-atom panel rsta btn repl-component current-tab-color]
	;; add tab listeners here because they use tab-manager functions and we need
	;; to include the button tab ns. This avoids the cyclic loading
	(listen btn 
		:mouse-entered (fn [e] (swap! current-tab-color (fn [_] mouse-over-color)))
		:mouse-exited (fn [e] (swap! current-tab-color (fn [_] base-color)))
		:mouse-clicked (fn [e] (let [idx (.indexOfComponent panel repl-component)
												  yes-no-option (close-repl-dialogue)]
											(if (= yes-no-option 0)
												(do 
													;; remove the tab from the tabbed panel
													(remove-repl-tab! panel idx)
													;; remove the repl from the projects map
													(remove-repl-from-project! app-atom rsta (get-meta rsta :project-path))))))))
;;
;; create a new repl tab from an outside process
;;
 (defn new-repl-tab! 
 	([app-atom]
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
 					tab-label (first (select tab [:.tab-label]))
 					current-tab-color (atom base-color)]
 					;; link the tab for this component for updating clean indicator
 					(put-meta! rsta :tab tab)

 			(add-mouse-handlers app-atom tabbed-panel rsta close-button repl-component current-tab-color)

 			;; set the component in the new tab
 			(.setTabComponentAt tabbed-panel index-of-new-tab tab)
			
 			(println "Added new repl: " tab-title)

		    ;; attach handlers
		    (add-repl-rsta-input-handler rsta)

 			;; bring the new tab to the front
 			(show-tab! tabbed-panel index-of-new-tab)))))
