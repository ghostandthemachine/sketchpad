(ns sketchpad.tab-manager
	(:import 
		(java.awt.event KeyEvent)
    (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
		(javax.swing JButton JOptionPane JWindow ImageIcon)
		(javax.swing.event DocumentListener))
	(:use [sketchpad option-windows file-manager button-tab prefs]
				[clojure pprint]
				[seesaw meta core border])
	(:require [clojure.string :as string])
	)


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

(defn insert-tab! [tabbed-panel title icon comp tip i]
	(.insertTab tabbed-panel title icon comp tip i))

(defn add-tab! [tabbed-panel title comp]
	(.addTab tabbed-panel title comp))

(defn remove-tab! [tabbed-panel index]
	(.removeTabAt tabbed-panel index)
	;; not sure if this is the best way to do this....
	; (if (not (tabs? (app :editor-tabbed-panel)))
	; 	(let [container (app :doc-split-pane)]
	; 		(.setRightComponent container bg-panel)))
	)

(defn index-of-component [tabbed-panel comp]
	(.indexOfComponent tabbed-panel comp))

(defn tab-count [tabbed-panel]
	(let [tabbed-panel tabbed-panel]
		(.getTabCount tabbed-panel)))

(defn tabs? [tabbed-panel]
	(> (tab-count tabbed-panel) 0))

(defn current-tab-index [tabbed-panel]
	(.getSelectedIndex tabbed-panel))

(defn current-tab [tabbed-panel]
	(component-at tabbed-panel (current-tab-index tabbed-panel)))

(defn current-text-area [tabbed-panel]
	(select (current-tab tabbed-panel) [:#editor]))

(defn open? [tabbed-panel file-name]
	(if (= -1 (.indexOfTab tabbed-panel file-name))
		false
		true))

(defn index-of [tabbed-panel name]
	(.indexOfTab tabbed-panel name))

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

(defn mark-current-tab-clean! [tabbed-panel]
	(mark-tab-clean! tabbed-panel (.getSelectedIndex tabbed-panel)))

(defn show-tab! [tabbed-panel index]
	(set-selected! tabbed-panel index))

(defn select-next-tab [tabbed-panel]
	(let [current-index (.getSelectedIndex tabbed-panel)
				num-tabs (.getTabCount tabbed-panel)]
		(cond 
			(< current-index (- num-tabs 1))
				(.setSelectedIndex tabbed-panel (+ current-index 1))
			(= current-index (- num-tabs 1))
				(.setSelectedIndex tabbed-panel 0)
			:else 
				(.setSelectedIndex tabbed-panel 0))))

(defn select-previous-tab [tabbed-panel]
	(let [current-index (.getSelectedIndex tabbed-panel)
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
	(let [current-index (current-tab-index (app :editor-tabbed-panel))]
	  (write-value-to-prefs sketchpad-prefs "current-files" @(app :current-files))
  	  (write-value-to-prefs sketchpad-prefs "current-tab" current-index)))

(defn close-current-tab [app]
	(if (tabs? (app :editor-tabbed-panel))
		(let [tabbed-panel (app :editor-tabbed-panel) 
					idx (.getSelectedIndex tabbed-panel)
					num-tabs (.getTabCount tabbed-panel)
					container (current-tab (app :editor-tabbed-panel))
					rsta (select container [:#editor])
					tab-state (get-meta rsta :state)]
			;; CHECK FOR SAVE FIRST!!!!!!!
			(if (@tab-state :clean)
				(remove-tab! (app :editor-tabbed-panel) idx)
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

(defn get-tab-rsta [tabbed-panel i]
	(select (component-at tabbed-panel i) [:#editor]))
