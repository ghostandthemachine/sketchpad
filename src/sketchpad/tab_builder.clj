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

(defn add-mouse-handlers [buffer]
	(let [tab-color (get-meta buffer :tab-color)
		btn (get-meta buffer :btn)]
	(listen btn :mouse-entered (fn [e] (swap! tab-color (fn [_] mouse-over-color))))
	(listen btn :mouse-exited (fn [e] (swap! tab-color (fn [_] base-color))))
	(listen btn :mouse-pressed (fn [e]
							;; CHECK FOR SAVE FIRST!!!!!!!
							(if (@(get-meta buffer :state) :clean)
								(do
									(swap! tab-color (fn [_] pressed-color))
									(try 
										(catch java.lang.NullPointerException e))
									(tab/remove-tab! buffer))
								(do
									(swap! tab-color (fn [_] pressed-color))
									(let [idx (tab/index-of-component buffer)
											answer (close-or-save-current-dialogue (tab/title-at idx))]
										(cond 
											(= answer 0)
												(do
													(save-file! buffer)
													(tab/remove-tab! buffer))
											(= answer 1)
												(do 
													(tab/remove-tab! buffer))))))
							(tab/save-tab-selections )))))

(defn dirty-doc-handler [e buffer]
	(tab/mark-tab-dirty! buffer))

(defn add-doc-handler [buffer]
	(.addDocumentListener 
		(.getDocument buffer)
	    	(proxy [javax.swing.event.DocumentListener] []
	    		(insertUpdate [e])
		     	(removeUpdate [e])
		     	(changedUpdate [e]
    				(dirty-doc-handler e buffer)))))


(defn attach-tab-handlers [buffer]
	(add-mouse-handlers buffer)
	(add-doc-handler buffer))

(defn init-new-tab [container buffer tab label btn tab-state tab-color]
	(put-meta! buffer :label label)
	(put-meta! buffer :btn btn)
	(put-meta! buffer :tab tab)
	(put-meta! buffer :parent container) ;; used for (tab/index-of-comopnent buffer)
	(put-meta! buffer :tab-color tab-color)
	(attach-tab-handlers buffer)
	(swap! tab-state (fn [state] (assoc state :index (tab/index-of-component buffer))))
	(.setTabComponentAt (app-tabbed-panel) (tab/index-of-component buffer) tab)
	(.setDot (.getCaret buffer) 0)
	(.discardAllEdits buffer)
	(tab/mark-tab-clean! buffer)
	(tab/show-tab! buffer))

(defn new-file! [buffer b]
	(if b
		(put-meta! buffer :new-file true)
		(put-meta! buffer :new-file false)))

(defn new-tab!
([]
	(let [tabbed-panel (app-tabbed-panel)
		  container (make-editor-component app)
		  buffer (select container [:#editor])
		  tab-state (get-meta buffer :state)]
		(tab/add-tab! "untitled" container)
		(tab/show-tab! buffer)
		(let [index-of-new-tab (tab/index-of-component buffer)
				tab (button-tab/button-tab buffer)
				close-button (select tab [:#close-button])
				tab-label (first (select tab [:.tab-label]))
				clean (@tab-state :clean)
				tab-color (atom base-color)]
		(init-new-tab container buffer tab tab-label close-button tab-state tab-color)
		(new-file! buffer true)
		(tab/focus-buffer buffer)
		buffer))) 
([proj]
	(let [tabbed-panel (app-tabbed-panel)
		  container (make-editor-component app)
		  buffer (select container [:#editor])
		  tab-state (get-meta buffer :state)]
		(tab/add-tab! "untitled" container)
		(tab/show-tab! buffer)
		(let [index-of-new-tab (tab/index-of-component buffer)
				tab (button-tab/button-tab buffer)
				close-button (select tab [:#close-button])
				tab-label (first (select tab [:.tab-label]))
				clean (@tab-state :clean)
				tab-color (atom base-color)]
		(init-new-tab container buffer tab tab-label close-button tab-state tab-color)
		(new-file! buffer false)
		(tab/focus-buffer buffer)
		buffer))))



