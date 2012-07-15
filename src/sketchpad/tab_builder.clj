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

(defn add-rsta-mouse-handlers [app-atom proj rsta tab-state btn container tab-color file-name]
	(let [app @app-atom]
		(listen btn :mouse-entered (fn [e] (swap! tab-color (fn [_] mouse-over-color))))
		(listen btn :mouse-exited (fn [e] (swap! tab-color (fn [_] base-color))))
		(listen btn :mouse-pressed (fn [e]
								;; CHECK FOR SAVE FIRST!!!!!!!
								(if (@tab-state :clean)
									(do
										(swap! tab-color (fn [_] pressed-color))
										(project/remove-buffer-from-project! app-atom proj file-name)
										(remove-tab! (app :editor-tabbed-panel) (index-of-component (app :editor-tabbed-panel) container)))
									(do
										(swap! tab-color (fn [_] pressed-color))
										(let [idx (index-of-component (app :editor-tabbed-panel) container)
												answer (close-or-save-current-dialogue (title-at tabbed-panel idx))]
											(cond 
												(= answer 0)
													(do
														(save-file app rsta idx) 
														(swap! (app :current-files) (fn [files] (dissoc files idx)))
														(project/remove-buffer-from-project! app-atom proj file-name)
														(remove-tab! (app :editor-tabbed-panel) idx))
												(= answer 1)
													(do 
														(swap! (app :current-files) (fn [files] (dissoc files idx)))
														(project/remove-buffer-from-project! app-atom proj file-name)
														(remove-tab! (app :editor-tabbed-panel) idx))))))
								(save-tab-selections app)))))

(defn add-rsta-doc-listener [app rsta container]
	(.addDocumentListener 
		(.getDocument rsta)
	    	(proxy [javax.swing.event.DocumentListener] []
	    		(insertUpdate [e] )
		     	(removeUpdate [e]	)
		     	(changedUpdate [e]
    			(mark-tab-dirty! (app :editor-tabbed-panel) (index-of-component (app :editor-tabbed-panel) container))))))

(defn new-file-tab! 
	([app-atom file proj] (new-file-tab! app-atom file proj -1))
	([app-atom file proj i]
	(let [app @app-atom
		project (@(app :project-map) proj)
		project-id (:id project)
		project-color (:project-color project)]
		(if (text-file? file)
			(if (open? (app :editor-tabbed-panel) (file-name file))
				(show-tab! (app :editor-tabbed-panel) (index-of (app :editor-tabbed-panel) (file-name file)))
				(do 
					(let [tabbed-panel (app :editor-tabbed-panel)
								container (make-editor-component app-atom)
								new-file-name (str (file-name file))
								rsta (select container [:#editor])]
						(put-meta! rsta :file file)
						(put-meta! rsta :project proj)
						(put-meta! rsta :project-path proj)
						(let [txt (slurp file)
				              rdr (StringReader. txt)]
				        	  (.read rsta rdr nil))
						(config! rsta :syntax (file-type file))
						(if (= i -1)
							(add-tab! (app :editor-tabbed-panel) new-file-name container)
							(insert-tab! (app :editor-tabbed-panel) new-file-name container i))
						
						(let [index-of-new-tab (index-of (app :editor-tabbed-panel) new-file-name)
									tab (button-tab app tabbed-panel index-of-new-tab project-color)
									close-button (select tab [:#close-button])
									tab-label (first (select tab [:.tab-label]))
									tab-state (get-meta rsta :state)
									clean (@tab-state :clean)
									tab-color (atom base-color)]
							(put-meta! rsta :tab tab)
							(swap! tab-state (fn [state] (assoc state :index index-of-new-tab)))
							(add-rsta-mouse-handlers app-atom proj rsta tab-state close-button container tab-color new-file-name)
					    	(add-rsta-doc-listener app rsta container)
							(.setTabComponentAt tabbed-panel index-of-new-tab tab)
							(swap! (@app-atom :current-files) (fn [files] (assoc files index-of-new-tab file)))
							(swap! (@app-atom :current-file) (fn [_] file))
							(project/add-buffer-to-project! app-atom proj (.getTitleAt tabbed-panel index-of-new-tab) rsta)
							(.setDot (.getCaret rsta) 0)
							(.discardAllEdits rsta)
							(show-tab! tabbed-panel index-of-new-tab)))))))))



