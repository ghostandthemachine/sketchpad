(ns sketchpad.tab-builder
	(:import (java.awt.event KeyEvent)
					(java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
					(javax.swing JButton JOptionPane JWindow ImageIcon)
					(javax.swing.event DocumentListener))
	(:use [sketchpad tab-manager main-background option-windows editor-component file-manager button-tab prefs]
				[clojure pprint string]
				[seesaw meta core border])
	)


; (defn new-file-tab! 
; 	([app] (new-file-tab! app (make-editor-component)))
; 	([app comp]
; 	(add-tab! (app :editor-tabbed-panel) " " comp)))

(defn new-file-tab! 
	([app-atom file] (new-file-tab! app-atom file -1))
	([app-atom file i]
	(let [app @app-atom]
		(if (text-file? file)
			(if (open? (app :editor-tabbed-panel) (file-name file))
				;; already open so just show it
				(show-tab! (app :editor-tabbed-panel) (index-of (app :editor-tabbed-panel) (file-name file)))
				;; otherwise create a new tab and set the syntax style
				(do 
					(let [tabbed-panel (app :editor-tabbed-panel)
								container (make-editor-component)
								new-file-name (str (file-name file))
								rsta (select container [:#editor])]
						;; attach the file to the component for easy saving
						(put-meta! rsta :file file)
						;; set the text area text from file
						(let [txt (slurp file)
			            rdr (StringReader. txt)]
			        (.read rsta rdr nil))
						;; set the new text area syntax
						(config! rsta :syntax (file-type file))
						;; check if this is a new tab or loading a tab from prefs
						(if (= i -1)
							(add-tab! (app :editor-tabbed-panel) new-file-name container)
							(insert-tab! (app :editor-tabbed-panel) new-file-name container i))
						
						;; set custom tab
						(let [index-of-new-tab (index-of (app :editor-tabbed-panel) new-file-name)
									tab (button-tab app tabbed-panel index-of-new-tab)
									close-button (select tab [:#close-button])
									tab-label (first (select tab [:.tab-label]))
									tab-state (get-meta rsta :state)
									clean (@tab-state :clean)]
									;; link the tab for this component for updating clean indicator
									(put-meta! rsta :tab tab)
									;; set the tab index in the editor component state map
									(swap! tab-state (fn [state] (assoc state :index index-of-new-tab)))
									; (config! close-button :foreground base-color :background base-color)
							;; add tab listeners here because they use tab-manager functions and we need
							;; to include the button tab ns. This avoids the cyclic loading
							(listen close-button :mouse-entered (fn [e] (swap! current-tab-color (fn [_] mouse-over-color))))
							(listen close-button :mouse-exited (fn [e] (swap! current-tab-color (fn [_] base-color))))
							(listen close-button :mouse-pressed (fn [e]
																	;; CHECK FOR SAVE FIRST!!!!!!!
																	(if (@tab-state :clean)
																		;; otherwise it's clean so just close it up
																		(do
																			(swap! current-tab-color (fn [_] pressed-color))
																			(remove-tab! (app :editor-tabbed-panel) (index-of-component (app :editor-tabbed-panel) container)))
																		(do
																			(swap! current-tab-color (fn [_] pressed-color))
																			(let [idx (index-of-component (app :editor-tabbed-panel) container)
																					answer (close-or-save-current-dialogue (title-at tabbed-panel idx))]
																				;; if the tab which is being close is dirty
																				(cond 
																					;; save and close
																					(= answer 0)
																						(do
																							(save-file app rsta idx) 
																							(swap! (app :current-files) (fn [files] (dissoc files idx)))
																							(remove-tab! (app :editor-tabbed-panel) idx))
																					;; don't save and close
																					(= answer 1)
																						(do 
																							(swap! (app :current-files) (fn [files] (dissoc files idx)))
																							(remove-tab! (app :editor-tabbed-panel) idx))
																					;; cancel
																					; (= answer 2)
																				))))
																	(save-tab-selections app)))
    					;; add state listener to this rta
    					;; this is where a document is marked as dirty
					    (.addDocumentListener (.getDocument rsta)
					    	(proxy [javax.swing.event.DocumentListener] []
					    		(insertUpdate [e] )
						     	(removeUpdate [e]	)
						     	(changedUpdate [e]
					    			(mark-tab-dirty! tabbed-panel (index-of-component (app :editor-tabbed-panel) container))
						        ; (swap! (get-meta rsta :state) (fn [state] (assoc state :clean false)))
						        )))

							;; set the component in the new tab
							(.setTabComponentAt tabbed-panel index-of-new-tab tab)
							;; add file and index to app map
							(swap! (@app-atom :current-files) (fn [files] (assoc files index-of-new-tab file)))
							
							; (println "Added new file: " file)
							(swap! app-atom (fn [app] (assoc app :doc-text-area rsta)))
							(swap! (@app-atom :current-files) (fn [files] (assoc files index-of-new-tab file)))
							;; update the current global focussed file
							(swap! (@app-atom :current-file) (fn [_] file))
							;; bring the new tab to the front
							(show-tab! tabbed-panel index-of-new-tab)
							))))))))	

; (defn new-repl-tab! 
; 	([app] (new-repl-tab! app (make-repl-component app)))
; 	([app comp]
; 	(add-tab! (app :repl-panel) " " comp)))

; (defn new-repl-tab! 
; 	([app-atom project-path] (new-repl-tab! app-atom project-path -1))
; 	([app-atom project-path i]
; 	(let [app @app-atom
; 				tabbed-panel (app :repl-panel)
; 				repl-component (make-repl-component app nil)
; 				rsta (select repl-component [:#repl-editor])
; 				tab-title (str "REPL# " (tab-count tabbed-panel))]
; 		(add-tab! tabbed-panel tab-title repl-component)
; 		;; set custom tab
; 		(let [index-of-new-tab (index-of tabbed-panel tab-title)
; 					tab (button-tab app tabbed-panel index-of-new-tab)
; 					close-button (select tab [:#close-button])
; 					tab-label (first (select tab [:.tab-label]))]
; 					;; link the tab for this component for updating clean indicator
; 					(put-meta! rsta :tab tab)
; 			;; add tab listeners here because they use tab-manager functions and we need
; 			;; to include the button tab ns. This avoids the cyclic loading
; 			(listen close-button :mouse-entered (fn [e] (swap! current-tab-color (fn [_] mouse-over-color))))
; 			(listen close-button :mouse-exited (fn [e] (swap! current-tab-color (fn [_] base-color))))
; 			(listen close-button :mouse-clicked (fn [e]
; 																						(let [idx (index-of-component tabbed-panel repl-component)
; 																									answer (close-or-save-current-dialogue (title-at tabbed-panel idx))]
; 																							;; if the tab which is being close is dirty
; 																							(cond 
; 																								;; save and close
; 																								(= answer 0)
; 																									(do
; 																										; (save-file app rsta idx) 
; 																										(swap! (app :repls) (fn [repls] (dissoc repls rsta)))
; 																										(remove-tab! tabbed-panel idx))
; 																								;; don't save and close
; 																								(= answer 1)
; 																									(do 
; 																										(swap! (app :repls) (fn [repls] (dissoc repls rsta)))
; 																										(remove-tab! tabbed-panel idx))
; 																								;; cancel
; 																								; (= answer 2)
; 																				; (save-tab-selections app)
; 																							))))

; 			;; set the component in the new tab
; 			(.setTabComponentAt tabbed-panel index-of-new-tab tab)
			
; 			(println "Added new repl: " tab-title)

; 			;; bring the new tab to the front
; 			(show-tab! app-atom tabbed-panel index-of-new-tab)
; 			))))
