(ns sketchpad.tab-builder
	(:import (java.awt.event KeyEvent)
					(java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
					(javax.swing JButton JOptionPane JWindow ImageIcon)
					(javax.swing.event DocumentListener))
	(:use [sketchpad shortcuts styles option-windows editor-component file-manager prefs]
				[clojure pprint]
				[seesaw meta core border])
	(:require [clojure.string :as str]
						[sketchpad.state :as sketchpad.state]
						[sketchpad.button-tab :as button-tab]
						[sketchpad.tab :as tab]
						[sketchpad.file :as file]))

(def app sketchpad.state/app)

(defn app-tabbed-panel []
	(@app :editor-tabbed-panel))

(defn save-file! [rsta]
"Save the current buffer."
(let [buffer (tab/current-text-area)
      new-file? (get-meta buffer :new-file)]
  (if new-file?
    (do
      (let [new-file (file/save-file-as)]
        (when (file/save-file buffer new-file)
          (put-meta! buffer :file new-file)
          (tab/mark-current-tab-clean! (@app :editor-tabbed-panel)))))
    (do
      (when (file/save-file buffer (get-meta buffer :file))
             (tab/mark-current-tab-clean! (@app :editor-tabbed-panel)))))))

(defn add-mouse-handlers [rsta]
	(let [tab-color (get-meta rsta :tab-color)
		btn (get-meta rsta :btn)]
	(listen btn :mouse-entered (fn [e] (swap! tab-color (fn [_] mouse-over-color))))
	(listen btn :mouse-exited (fn [e] (swap! tab-color (fn [_] base-color))))
	(listen btn :mouse-pressed (fn [e]
							;; CHECK FOR SAVE FIRST!!!!!!!
							(if (@(get-meta rsta :state) :clean)
								(do
									(swap! tab-color (fn [_] pressed-color))
									(try 
										(catch java.lang.NullPointerException e))
									(tab/remove-tab! rsta))
								(do
									(swap! tab-color (fn [_] pressed-color))
									(let [idx (tab/index-of-component rsta)
											answer (close-or-save-current-dialogue (tab/title-at idx))]
										(cond 
											(= answer 0)
												(do
													(save-file! rsta)
													(tab/remove-tab! rsta))
											(= answer 1)
												(do 
													(tab/remove-tab! rsta))))))
							(tab/save-tab-selections )))))

(defn add-doc-handler [rsta]
	(.addDocumentListener 
		(.getDocument rsta)
	    	(proxy [javax.swing.event.DocumentListener] []
	    		(insertUpdate [e])
		     	(removeUpdate [e])
		     	(changedUpdate [e]
    			(tab/mark-tab-dirty! rsta)))))


(defn attach-tab-handlers [rsta]
	(add-mouse-handlers rsta)
	(add-doc-handler rsta))

(defn init-new-tab [container rsta tab label btn tab-state tab-color]
	(put-meta! rsta :label label)
	(put-meta! rsta :btn btn)
	(put-meta! rsta :tab tab)
	(put-meta! rsta :parent container) ;; used for (tab/index-of-comopnent rsta)
	(put-meta! rsta :tab-color tab-color)
	(attach-tab-handlers rsta)
	(swap! tab-state (fn [state] (assoc state :index (tab/index-of-component rsta))))
	(.setTabComponentAt (app-tabbed-panel) (tab/index-of-component rsta) tab)
	(.setDot (.getCaret rsta) 0)
	(.discardAllEdits rsta)
	(tab/show-tab! rsta))

(defn new-file! [rsta b]
	(if b
		(put-meta! rsta :new-file true)
		(put-meta! rsta :new-file false)))

(defn new-tab!
([]
	(let [tabbed-panel (app-tabbed-panel)
		  container (make-editor-component app)
		  rsta (select container [:#editor])
		  tab-state (get-meta rsta :state)]
		(tab/add-tab! "untitled" container)
		(tab/show-tab! rsta)
		(let [index-of-new-tab (tab/index-of-component rsta)
				tab (button-tab/button-tab rsta)
				close-button (select tab [:#close-button])
				tab-label (first (select tab [:.tab-label]))
				clean (@tab-state :clean)
				tab-color (atom base-color)]
		(init-new-tab container rsta tab tab-label close-button tab-state tab-color)
		(new-file! rsta true)
		rsta))) 
([proj]
	(let [tabbed-panel (app-tabbed-panel)
		  container (make-editor-component app)
		  rsta (select container [:#editor])
		  tab-state (get-meta rsta :state)]
		(tab/add-tab! "untitled" container)
		(tab/show-tab! rsta)
		(let [index-of-new-tab (tab/index-of-component rsta)
				tab (button-tab/button-tab rsta)
				close-button (select tab [:#close-button])
				tab-label (first (select tab [:.tab-label]))
				clean (@tab-state :clean)
				tab-color (atom base-color)]
		(init-new-tab container rsta tab tab-label close-button tab-state tab-color)
		(new-file! rsta false)
		rsta))))



