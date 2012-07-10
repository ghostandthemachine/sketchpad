(ns sketchpad.tab-builder
	(:import (java.awt.event KeyEvent)
					(java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
					(javax.swing JButton JOptionPane JWindow ImageIcon)
					(javax.swing.event DocumentListener))
	(:use [sketchpad styles tab-manager project-manager option-windows editor-component file-manager button-tab prefs]
				[clojure pprint]
				[seesaw meta core border])
	(:require [clojure.string :as str]
			[sketchpad.project-manager :as project])
	)

(defn new-file-tab! 
	([app-atom file proj] (new-file-tab! app-atom file proj -1))
	([app-atom file proj i]
	(let [app @app-atom
				project (@(app :project-map) proj)
				project-id (:id project)
				project-color (:project-color project)]
		(if (text-file? file)
			(if (open? (app :editor-tabbed-panel) (file-name file))
				;; already open so just show it
				(show-tab! (app :editor-tabbed-panel) (index-of (app :editor-tabbed-panel) (file-name file)))
				;; otherwise create a new tab and set the syntax style
				(do 
					(let [tabbed-panel (app :editor-tabbed-panel)
								container (make-editor-component app-atom)
								; container (make-quil-editor-component app-atom)
								new-file-name (str (file-name file))
								rsta (select container [:#editor])
								]
						;; attach the file to the component for easy saving
						(put-meta! rsta :file file)
						(put-meta! rsta :project proj)
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
									tab (button-tab app tabbed-panel index-of-new-tab project-color)
									close-button (select tab [:#close-button])
									tab-label (first (select tab [:.tab-label]))
									tab-state (get-meta rsta :state)
									clean (@tab-state :clean)
									current-tab-color (atom base-color)]
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
																			(project/remove-buffer-from-project! app-atom proj new-file-name)
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
																							(project/remove-buffer-from-project! app-atom proj new-file-name)
																							(remove-tab! (app :editor-tabbed-panel) idx))
																					;; don't save and close
																					(= answer 1)
																						(do 
																							(swap! (app :current-files) (fn [files] (dissoc files idx)))
																							(project/remove-buffer-from-project! app-atom proj new-file-name)
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
;							(swap! app-atom (fn [app] (assoc app :doc-text-area rsta)))
							(swap! (@app-atom :current-files) (fn [files] (assoc files index-of-new-tab file)))
							;; update the current global focussed file
							(swap! (@app-atom :current-file) (fn [_] file))

							;; update the project map
							(project/add-buffer-to-project! app-atom proj (.getTitleAt tabbed-panel index-of-new-tab) rsta)


							;; set caret to start
							(.setDot (.getCaret rsta) 0)
							;; finally discard all undo supported edits (which include loading the text!)
							;; this prevents the undo action from undoing the initial load
							(.discardAllEdits rsta)
							;; bring the new tab to the front
							(show-tab! tabbed-panel index-of-new-tab)))))))))
