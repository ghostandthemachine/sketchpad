(ns sketchpad.editor.build
	(:import (java.util UUID)
			 (java.awt.event KeyEvent)
					(java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
					(javax.swing JButton JOptionPane JWindow ImageIcon)
					(javax.swing.event DocumentListener))
	(:use [sketchpad shortcuts styles option-windows file-manager prefs]
				[clojure pprint]
				[seesaw meta core border color])
	(:require [clojure.string :as str]
						[sketchpad.state :as state]
						[sketchpad.editor.tab :as button-tab]
						[sketchpad.tab :as tab]
						[sketchpad.utils :as utils]
						[sketchpad.editor.component :as editor.component]
						[sketchpad.editor.info-utils :as editor.info-utils]
						[sketchpad.file.file :as file]))

(defn app-tabbed-panel []
	(@state/app :editor-tabbed-panel))

(defn add-mouse-handlers [buffer]
	(let [tab-color (atom (color :white))
		  button (get-in buffer [:tab :button])]
	(listen button :mouse-entered (fn [e] (reset! tab-color mouse-over-color)))
	(listen button :mouse-exited  (fn [e] (reset! tab-color base-color)))
	(listen button :mouse-pressed (fn [e]
							;; CHECK FOR SAVE FIRST!!!!!!!
							(if (@(:state buffer) :clean)
								(do
									(swap! tab-color (fn [_] pressed-color))
									(try 
										(catch java.lang.NullPointerException e))
									(tab/remove-tab! buffer))
								(do
									(swap! tab-color (fn [_] pressed-color))
									(let [idx (tab/index-of-buffer buffer)
											answer (close-or-save-current-dialogue (tab/title-at idx))]
										(cond 
											(= answer 0)
												(do
													(file/save-file! buffer)
													(tab/remove-tab! buffer))
											(= answer 1)
												(do 
													(tab/remove-tab! buffer))))))
							(tab/save-tab-selections )))))

(defn dirty-doc-handler [e buffer]
	(tab/mark-tab-dirty! buffer))

(defn add-doc-handler [buffer]
	(utils/awt-event
		(.addDocumentListener
			(.getDocument (:text-area buffer))
		    	(proxy [javax.swing.event.DocumentListener] []
		    		(insertUpdate [e])
			     	(removeUpdate [e])
			     	(changedUpdate [e]
						(dirty-doc-handler e buffer))))))

(defn attach-tab-handlers [buffer]
	(add-mouse-handlers buffer)
	(add-doc-handler buffer))

(defn init-new-tab [buffer]
	(attach-tab-handlers buffer)
	(swap! (:state buffer) (fn [state] (assoc state :index (tab/index-of-buffer buffer))))
	(.setDot (.getCaret (:text-area buffer)) 0)
	(put-meta! (:text-area buffer) :uuid (:uuid buffer))
    (editor.info-utils/attach-caret-handler (:text-area buffer))
	buffer)

(defn custom-tab [buffer-component]
	(let [tab (button-tab/button-tab buffer-component)]
        (tab/buffer-tab-component! buffer-component tab)
        tab))

(defn scratch-buffer-tab
[]
	(let [tabbed-panel (@state/app :editor-tabbed-panel)
		  buffer-component (editor.component/buffer-component)
		  container (:container buffer-component)
		  text-area (:text-area buffer-component)
		  tab-state (:state buffer-component)
		  uuid (.. UUID randomUUID toString)]
		(tab/add-tab! "untitled" container)
		(let [tab (custom-tab buffer-component)
			buffer { :type :buffer
					 :text-area text-area
					 :container container
					 :tab tab
					 :title (atom "new tab")
					 :label (:label buffer-component)
					 :file (atom nil)
					 :state tab-state
					 :new-file? true
					 :uuid uuid
					 :project nil}]
			(init-new-tab buffer))))

(defn project-buffer-tab
[project]
	(let [tabbed-panel (@state/app :editor-tabbed-panel)
		  buffer-component (editor.component/buffer-component)
		  container (get buffer-component :container)
		  text-area (get buffer-component :text-area)
		  tab-state (get buffer-component :state)
		  uuid (.. UUID randomUUID toString)]
		(tab/add-tab! "untitled" container)
		(let [tab (custom-tab buffer-component)
			buffer { :type :buffer
				     :text-area text-area
					 :tab tab
					 :title (atom "new tab")
					 :label (:label buffer-component)
					 :file (atom nil)
					 :container container
					 :state tab-state
					 :new-file? (atom false)
					 :project project
					 :uuid uuid}]
			(init-new-tab buffer))))



