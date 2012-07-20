(ns sketchpad.tab
	(:import 
		(java.awt.event KeyEvent)
    (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
		(javax.swing JButton JOptionPane JWindow ImageIcon)
		(javax.swing.event DocumentListener))
	(:use [sketchpad option-windows file-manager button-tab prefs]
				[clojure pprint]
				[seesaw meta core border])
	(:require [clojure.string :as string]
			[sketchpad.file :as file]
			[sketchpad.state :as sketchpad.state])
	)

(def tab-app sketchpad.state/app)

(defn chop
  "Removes the last character of string."
  [s]
  (subs s 0 (dec (count s))))

(defn tab-count [tabbed-panel]
	(let [tabbed-panel tabbed-panel]
		(.getTabCount tabbed-panel)))

(defn tabs? 
([]
	(tabs? (@tab-app :editor-tabbed-panel)))
([tabbed-panel]
	(> (tab-count tabbed-panel) 0)))

(defn component-at [tabbed-panel index]
	(when (tabs? tabbed-panel)
		(.getComponentAt tabbed-panel index)))

(defn component-at! [tabbed-panel index comp]
	(.setComponentAt tabbed-panel index comp))

(defn title-at 
([index]
	(.getTitleAt (@tab-app :editor-tabbed-panel) index))
([tabbed-panel index]
	(.getTitleAt tabbed-panel index)))


(defn title-at!
([index s]
	(title-at! (@tab-app :editor-tabbed-panel) index s))
([tabbed-panel index s]
	(.setTitleAt tabbed-panel index s)))

(defn index-of-component 
	([comp]
		(index-of-component (@tab-app :editor-tabbed-panel) (get-meta comp :parent)))
	([tabbed-panel comp]
	(.indexOfComponent tabbed-panel comp)))

(defn insert-tab! [tabbed-panel title icon comp tip i]
	(.insertTab tabbed-panel title icon comp tip i))

(defn add-tab! 
([title comp]
	(add-tab! (@tab-app :editor-tabbed-panel) title comp))
([tabbed-panel title comp]
	(.addTab tabbed-panel title comp)))

(defn remove-tab! 
	([comp]
		(remove-tab! 
			(@tab-app :editor-tabbed-panel) 
			(index-of-component comp)))
	([tabbed-panel index]
	(.removeTabAt tabbed-panel index)))

(defn remove-repl-tab! [tabbed-panel index]
	(let [rsta (select (component-at tabbed-panel index) [:#editor])]
		(if-let [repl (get-meta rsta :repl)]
			(.destroy (repl :proc)))
	(.removeTabAt tabbed-panel index)))

(defn current-tab-index 
([] (current-tab-index (@tab-app :editor-tabbed-panel)))
([tabbed-panel]
	(.getSelectedIndex tabbed-panel)))

(defn current-tab-index 
([]
	(.getSelectedIndex (@tab-app :editor-tabbed-panel)))
([tabbed-panel]
	(.getSelectedIndex tabbed-panel)))

(defn current-tab 
([]
	(current-tab (@tab-app :editor-tabbed-panel)))
([tabbed-panel]
	(component-at tabbed-panel (current-tab-index tabbed-panel))))

(defn current-text-area 
([]
	(current-text-area (@tab-app :editor-tabbed-panel)))
([tabbed-panel]
	(try 
		(select (current-tab tabbed-panel) [:#editor])
		(catch java.lang.IllegalArgumentException e))))

(defn open? [tabbed-panel file-name]
	(if (= -1 (.indexOfTab tabbed-panel file-name))
		false
		true))

(defn index-of [tabbed-panel name]
	(.indexOfTab tabbed-panel name))

(defn set-selected! [tabbed-panel index]
	(.setSelectedIndex tabbed-panel index))

(defn show-tab! 
([buffer]
	(set-selected! (@tab-app :editor-tabbed-panel) (index-of-component buffer)))
([tabbed-panel index]
	(set-selected! tabbed-panel index)))

(defn next-tab []
	(let [tabbed-panel (@tab-app :editor-tabbed-panel)]
		(when (tabs? tabbed-panel)
			(let [current-index (.getSelectedIndex tabbed-panel)
						num-tabs (.getTabCount tabbed-panel)]
				(cond 
					(< current-index (- num-tabs 1))
						(.setSelectedIndex tabbed-panel (+ current-index 1))
					(= current-index (- num-tabs 1))
						(.setSelectedIndex tabbed-panel 0)
					:else 
						(.setSelectedIndex tabbed-panel 0))))))

(defn previous-tab []
	(let [tabbed-panel (@tab-app :editor-tabbed-panel)]
		(when (tabs? tabbed-panel)
			(let [current-index (.getSelectedIndex tabbed-panel)
						num-tabs (.getTabCount tabbed-panel)]
				(cond 
					(> current-index 0)
						(.setSelectedIndex tabbed-panel (- current-index 1))
					(= current-index 0)
						(.setSelectedIndex tabbed-panel (- num-tabs 1))
					:else 
						(.setSelectedIndex tabbed-panel (- num-tabs 1)))))))

(defn mark-tab-state! 
([tabbed-panel i kw]
	(let [rsta (select (component-at tabbed-panel i) [:#editor])
				file-state (get-meta rsta :state)]
		(cond
			(= kw :clean)
				(do
					(swap! file-state (fn [state] (assoc state :clean true)))
					(.repaint tabbed-panel))
			(= kw :dirty)
				(do
					(swap! file-state (fn [state] (assoc state :clean false)))
					(.repaint tabbed-panel)))))
([buffer kw]
(let [file-state (get-meta buffer :state)]
	(cond
		(= kw :clean)
			(do
				(swap! file-state (fn [state] (assoc state :clean true)))
				(.repaint (get-meta buffer :tab)))
		(= kw :dirty)
			(do
				(swap! file-state (fn [state] (assoc state :clean false)))
				(.repaint (get-meta buffer :tab)))))))

(defn mark-tab-clean! 
([buffer]
	(mark-tab-state! buffer :clean))
([tabbed-panel i] 
	(mark-tab-state! tabbed-panel i :clean)))

(defn mark-current-tab-clean! [tabbed-panel]
	(mark-tab-clean! tabbed-panel (.getSelectedIndex tabbed-panel)))

(defn mark-tab-dirty!
([buffer]
(mark-tab-state! buffer :dirty))
([tabbed-panel i] 
(mark-tab-state! tabbed-panel i :dirty)))

(defn mark-current-tab-dirty! [tabbed-panel i]
	(mark-tab-dirty! tabbed-panel (.getSelectedIndex tabbed-panel)))

(defn save-tab-selections []
	(let [current-index (current-tab-index)]
	  (write-value-to-prefs sketchpad-prefs "current-files" @(@tab-app :current-files))
  	  (write-value-to-prefs sketchpad-prefs "current-tab" current-index)))

(defn close-tab 
([]
	(if (tabs? (@tab-app :editor-tabbed-panel))
		(remove-tab! (@tab-app :editor-tabbed-panel) (current-tab-index))))
([tabbed-panel]
	(if (tabs? tabbed-panel)
		(remove-tab! tabbed-panel (current-tab-index tabbed-panel)))))

(defn close-current-tab []
	(remove-tab! (@tab-app :editor-tabbed-panel) (current-tab-index)))

(defn get-file-from-tab-index [app i]
	(i @(app :current-files)))

(defn get-tab-rsta [tabbed-panel i]
	(select (component-at tabbed-panel i) [:#editor]))
