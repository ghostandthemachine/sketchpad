(ns sketchpad.editor.build
	(:import (java.util UUID)
			 (java.awt.event KeyEvent)
					(java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
					(javax.swing JButton JOptionPane JWindow ImageIcon)
					(javax.swing.event DocumentListener))
	(:use [sketchpad.config.styles]
		[sketchpad.config.prefs]
				[clojure pprint]
				[seesaw meta core border color])
	(:require [clojure.string :as str]
						[sketchpad.state.state :as state]
						[sketchpad.editor.tab :as button-tab]
						[sketchpad.util.tab :as tab]
						[sketchpad.util.utils :as utils]
						[sketchpad.project.project :as sketchpad.project]
						[sketchpad.editor.component :as editor.component]
						[sketchpad.editor.info-utils :as editor.info-utils]
						[sketchpad.menu.view :as sketchpad.menu.view]
						[sketchpad.file.file :as file]))

(defn app-tabbed-panel []
	(get-in (:buffer-tabbed-panel @state/app) [:component :container]))

(defn add-mouse-handlers [buffer]
	(let [label-color (get-in buffer [:tab :label-color])
		  button (get-in buffer [:tab :button])]
	(listen button :mouse-entered (fn [e] (reset! label-color mouse-over-color)))
	(listen button :mouse-exited  (fn [e] (reset! label-color base-color)))
	(listen button :mouse-pressed (fn [e] (sketchpad.menu.view/close-tab buffer)))))

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
    (tab/buffer-title! buffer)
    (tab/buffer-tab-component! buffer)
	buffer)

(defn custom-tab [buffer-component]
	(button-tab/button-tab buffer-component))

(defn scratch-buffer-tab
[project-path]
	(let [tabbed-panel (app-tabbed-panel)
		  buffer-component (editor.component/buffer-component)
		  container (:container buffer-component)
		  text-area (:text-area buffer-component)
		  tab-state (:state buffer-component)
		  uuid (.. UUID randomUUID toString)
		  buffer { :type :buffer
					 :text-area text-area
					 :container container
					 :title (:title buffer-component)
					 :label (:label buffer-component)
					 :file (atom nil)
					 :component buffer-component
					 :state tab-state
					 :new-file? (atom true)
					 :uuid uuid
					 :project project-path}]
		(tab/add-tab! "untitled" container)
		(let [tab (custom-tab buffer)]
	(init-new-tab (assoc buffer :tab tab)))))

(defn project-buffer-tab
[project-path]
	(let [tabbed-panel (app-tabbed-panel)
		  buffer-component (editor.component/buffer-component)
		  container (get buffer-component :container)
		  text-area (get buffer-component :text-area)
		  tab-state (get buffer-component :state)
		  uuid (.. UUID randomUUID toString)
		  buffer { :type :buffer
				     :text-area text-area
					 :title (:title buffer-component)
					 :label (:label buffer-component)
					 :file (atom nil)
					 :container container
					 :state tab-state
					 :new-file? (atom false)
					 :component buffer-component
					 :project project-path
					 :uuid uuid}]
		(tab/add-tab! "untitled" container)
		(let [tab (custom-tab buffer)]
	(init-new-tab (assoc buffer :tab tab)))))



