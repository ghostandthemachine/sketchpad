(ns sketchpad.tab-manager
	(:import 
		(java.awt.event KeyEvent)
    (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
		(javax.swing JButton JOptionPane JWindow ImageIcon)
		(javax.swing.event DocumentListener))
	(:use [sketchpad main-background option-windows editor-component file-manager button-tab prefs]
				[clojure pprint string]
				[seesaw meta core border])
	)

; (defonce bg-panel (background-comp))

(defn chop
  "Removes the last character of string."
  [s]
  (subs s 0 (dec (count s))))

(defn component-at [tabbed-panel index]
	(.getComponentAt tabbed-panel index))

(defn component-at! [tabbed-panel index comp]
	(.setComponentAt tabbed-panel index comp))

(defn title-at [tabbed-panel index]
	(.getTitleAt tabbed-panel index))

(defn title-at! [tabbed-panel index s]
	(.setTitleAt tabbed-panel index s))

(defn insert-tab! [app title icon comp tip i]
	(.insertTab (app :editor-tabbed-panel) title icon comp tip i))

(defn add-tab! [app title comp]
	(.addTab (app :editor-tabbed-panel) title comp))

(defn remove-tab! [app index]
	(.removeTabAt (app :editor-tabbed-panel) index)
	;; not sure if this is the best way to do this....
	; (if (not (tabs? app))
	; 	(let [container (app :doc-split-pane)]
	; 		(.setRightComponent container bg-panel)))
	)

(defn index-of-component [app comp]
	(.indexOfComponent (app :editor-tabbed-panel) comp))

(defn tab-count [app]
	(let [tabbed-panel (app :editor-tabbed-panel)]
		(.getTabCount tabbed-panel)))

(defn tabs? [app]
	(> (tab-count app) 0))

(defn current-tab-index [app]
	(.getSelectedIndex (app :editor-tabbed-panel)))

(defn current-tab [app]
	(component-at (app :editor-tabbed-panel) (current-tab-index app)))

(defn current-text-area [app]
	(select (current-tab app) [:#editor]))

; (defn text-area-from-index [app i]
; 	(select (component-at (app :editor-tabbed-panel) i) [:#editor]))

(defn open? [app file-name]
	(let [tabbed-panel (app :editor-tabbed-panel)]
		(if (= -1 (.indexOfTab tabbed-panel file-name))
			false
			true)))

(defn index-of [app name]
	(.indexOfTab (app :editor-tabbed-panel) name))

(defn set-selected! [tabbed-panel index]
	(.setSelectedIndex tabbed-panel index))

(defn mark-tab-state! [tabbed-panel i kw]
	(let [rsta (select (component-at tabbed-panel i) [:#editor])
				tab (get-meta rsta :tab)
				; indicator (select tab [:#indicator])
				file-state (get-meta rsta :state)]
		(cond
			(= kw :clean)
				(do
					(swap! file-state (fn [state] (assoc state :clean true)))
					;; update the tab paint so the indicator shows with out needing a mouseover
					(.repaint tabbed-panel))
			(= kw :dirty)
				(do
					(swap! file-state (fn [state] (assoc state :clean false)))
					;; update the tab paint so the indicator shows with out needing a mouseover
					(.repaint tabbed-panel)))))

(defn mark-tab-clean! [tabbed-panel i] (mark-tab-state! tabbed-panel i :clean))

(defn mark-current-tab-clean! [app]
	(mark-tab-clean! (app :editor-tabbed-panel) (.getSelectedIndex (app :editor-tabbed-panel))))

(defn show-tab! [app-atom index file]
	(let [comp (component-at (@app-atom :editor-tabbed-panel) index)
				rsta (select comp [:#editor])
				tabbed-panel (@app-atom :editor-tabbed-panel)]
		(swap! app-atom (fn [app] (assoc app :doc-text-area rsta)))
		(swap! (@app-atom :current-files) (fn [files] (assoc files index file)))
		;; update the current global focussed file
		(swap! (@app-atom :current-file) (fn [_] file))
		(set-selected! tabbed-panel index)))

(defn new-editor-tab! 
	([app] (new-editor-tab! app (make-editor-component)))
	([app comp]
	(add-tab! (app :editor-tabbed-panel) " " comp)))


(defn select-next-tab [app]
	(let [tabbed-panel (app :editor-tabbed-panel) 
				current-index (.getSelectedIndex tabbed-panel)
				num-tabs (.getTabCount tabbed-panel)]
		(cond 
			(< current-index (- num-tabs 1))
				(.setSelectedIndex tabbed-panel (+ current-index 1))
			(= current-index (- num-tabs 1))
				(.setSelectedIndex tabbed-panel 0)
			:else 
				(.setSelectedIndex tabbed-panel 0))))

(defn select-previous-tab [app]
	(let [tabbed-panel (app :editor-tabbed-panel) 
				current-index (.getSelectedIndex tabbed-panel)
				num-tabs (.getTabCount tabbed-panel)]
		(cond 
			(> current-index 0)
				(.setSelectedIndex tabbed-panel (- current-index 1))
			(= current-index 0)
				(.setSelectedIndex tabbed-panel (- num-tabs 1))
			:else 
				(.setSelectedIndex tabbed-panel (- num-tabs 1)))))

(defn mark-tab-dirty! [tabbed-panel i] (mark-tab-state! tabbed-panel i :dirty))

(defn mark-current-tab-dirty! [tabbed-panel i]
	(mark-tab-dirty! tabbed-panel (.getSelectedIndex tabbed-panel)))

(defn save-tab-selections [app]
	; (println "save-tab-selections")
	(let [current-index (current-tab-index app)]
	  (write-value-to-prefs clooj-prefs "current-files" @(app :current-files))
  	  (write-value-to-prefs clooj-prefs "current-tab" current-index)))

(defn new-file-tab! 
	([app-atom file] (new-file-tab! app-atom file -1))
	([app-atom file i]
	(let [app @app-atom]
		(if (text-file? file)
			(if (open? app (file-name file))
				;; already open so just show it
				(show-tab! app-atom (index-of app (file-name file)) file)
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
							(add-tab! app new-file-name container)
							(insert-tab! app new-file-name container i))
						
						;; set custom tab
						(let [index-of-new-tab (index-of app new-file-name)
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
							(listen close-button :mouse-entered (fn [e] (if (@tab-state :clean)
																			(swap! current-tab-color (fn [_] mouse-over-color)))))
							(listen close-button :mouse-exited (fn [e] (if (@tab-state :clean)
																			(swap! current-tab-color (fn [_] base-color)))))
							(listen close-button :mouse-pressed (fn [e]
																	;; CHECK FOR SAVE FIRST!!!!!!!
																	(if (@tab-state :clean)
																		;; otherwise it's clean so just close it up
																		(do
																			(swap! current-tab-color (fn [_] pressed-color))
																			(remove-tab! app (index-of-component app container)))
																		(do
																			(swap! current-tab-color (fn [_] pressed-color))
																			(let [idx (index-of-component app container)
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
					    			(mark-tab-dirty! tabbed-panel (index-of-component app container))
						        ; (swap! (get-meta rsta :state) (fn [state] (assoc state :clean false)))
						        )))

							;; set the component in the new tab
							(.setTabComponentAt tabbed-panel index-of-new-tab tab)
							;; add file and index to app map
							(swap! (@app-atom :current-files) (fn [files] (assoc files index-of-new-tab file)))
							
							; (println "Added new file: " file)

							;; bring the new tab to the front
							(show-tab! app-atom index-of-new-tab file)))))))))	

(defn load-tab-selections [app-atom]
	(let [current-files (read-value-from-prefs clooj-prefs "current-files")
				current-file (read-value-from-prefs clooj-prefs "current-tab")]
		;; update the app map with previously loaded files
		(swap! app-atom (fn [app] (assoc app :current-files (atom current-files))))
		(swap! app-atom (fn [app] (assoc app :current-file current-file)))
		;; now load the prefs
		(println "Reload tabs from prefs")
		(doseq [[id file] current-files]
			(println id file)
			(new-file-tab! app-atom file id))
		)

	)

(defn close-current-tab [app]
	(if (tabs? app)
		(let [tabbed-panel (app :editor-tabbed-panel) 
					idx (.getSelectedIndex tabbed-panel)
					num-tabs (.getTabCount tabbed-panel)
					container (current-tab app)
					rsta (select container [:#editor])
					tab-state (get-meta rsta :state)]
			;; CHECK FOR SAVE FIRST!!!!!!!
			(if (@tab-state :clean)
				(remove-tab! app idx)
				(do 
					(let [answer (close-or-save-current-dialogue (title-at tabbed-panel idx))]
						;; if the tab which is being close is dirty
						(cond 
							;; save and close
							(= answer 0)
								(do
									(save-file app rsta idx) 
									(swap! (app :current-files) (fn [files] (dissoc files idx)))
									(remove-tab! (app :editor-tabbed-panel) idx)
									)
							;; don't save and close
							(= answer 1)
								(do 
									(swap! (app :current-files) (fn [files] (dissoc files idx)))
									(remove-tab! (app :editor-tabbed-panel) idx)
									)
							;; just close
							; (= answer 2)
							)))))))

(defn get-file-from-tab-index [app i]
	(i @(app :current-files)))
