(ns sketchpad.tab
	(:import 
		(java.awt.event KeyEvent)
    (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
		(javax.swing JButton JOptionPane JWindow ImageIcon)
		(javax.swing.event DocumentListener))
	(:use [sketchpad option-windows file-manager prefs]
				[clojure pprint]
				[seesaw meta core border])
	(:require [clojure.string :as string]
    		[sketchpad.tree.utils :as tree.utils]
			[sketchpad.state :as state]))

(defn chop
  "Removes the last character of string."
  [s]
  (subs s 0 (dec (count s))))

(defn tab-count 
([]
	(tab-count (@state/app :editor-tabbed-panel)))
([tabbed-panel]
	(let [tabbed-panel tabbed-panel]
		(.getTabCount tabbed-panel))))

(defn current-index 
"Index of current tab."
([]
	(.getSelectedIndex (@state/app :editor-tabbed-panel))))

(defn tabs? 
([]
	(tabs? (@state/app :editor-tabbed-panel)))
([tabbed-panel]
	(> (tab-count tabbed-panel) 0)))

(defn component-at [tabbed-panel index]
	(when (tabs? tabbed-panel)
		(try 
			(.getComponentAt tabbed-panel index)
			(catch java.lang.ArrayIndexOutOfBoundsException e))))

(defn component-at! [tabbed-panel index comp]
	(.setComponentAt tabbed-panel index comp))

(defn title-at
([] (title-at (@state/app :editor-tabbed-panel) (current-index)))
([index]
	(title-at (@state/app :editor-tabbed-panel) index))
([tabbed-panel index]
	(.getTitleAt tabbed-panel index)))

(defn title-at!
([index s]
	(title-at! (@state/app :editor-tabbed-panel) index s))
([tabbed-panel index s]
	(.setTitleAt tabbed-panel index s)))

(defn index-of-component 
	([comp]
		(index-of-component (@state/app :editor-tabbed-panel) (get-meta comp :parent)))
	([tabbed-panel comp]
	(.indexOfComponent tabbed-panel comp)))

(defn title 
"Title of the current buffer."
([]
	(let [idx (current-index)]
		(if (>= idx 0)
			(title-at idx)
			"no file open")))
([buffer]
	(title-at (index-of-component buffer))))

(defn index-of [tabbed-panel name]
	(.indexOfTab tabbed-panel name))

(defn set-selected! [tabbed-panel index]
	(.setSelectedIndex tabbed-panel index))

(defn index-of-buffer [buffer]
	(.indexOfComponent (@state/app :editor-tabbed-panel) (get buffer :container)))

(defn index-of-repl [repl]
	(println (.indexOfComponent (@state/app :repl-tabbed-panel) (get-in repl [:component :container])))
	(.indexOfComponent (@state/app :repl-tabbed-panel) (get-in repl [:component :container])))

(defn insert-tab!
([title comp i] (insert-tab! (:editor-tabbed-panel @state/app) title comp i))
([tabbed-panel title comp i] (insert-tab! tabbed-panel title nil comp nil i))
([tabbed-panel title icon comp tip i]
	(.insertTab tabbed-panel title icon comp tip i)))

(defn add-tab! 
([title comp]
	(add-tab! (@state/app :editor-tabbed-panel) title comp))
([tabbed-panel title comp]
	(.addTab tabbed-panel title comp)))

(defn add-repl
[repl]
	(.addTab (@state/app :repl-tabbed-panel) @(:title repl) (:container repl)))

(defn remove-tab! 
	([buffer]
		(remove-tab! 
			(@state/app :editor-tabbed-panel) 
			buffer))
	([tabbed-panel buffer]
	(.removeTabAt tabbed-panel (index-of-buffer buffer))))

(defn remove-repl
[repl]
	(.removeTabAt (@state/app :repl-tabbed-panel) (index-of-repl repl)))

(defn current-tab-index 
([] (current-tab-index (@state/app :editor-tabbed-panel)))
([tabbed-panel]
	(.getSelectedIndex tabbed-panel)))

(defn current-tab 
([]
	(current-tab (@state/app :editor-tabbed-panel)))
([tabbed-panel]
	(component-at tabbed-panel (current-tab-index tabbed-panel))))

(defn open? [tabbed-panel file-name]
	(if (= -1 (.indexOfTab tabbed-panel file-name))
		false
		true))

(defn show-tab! 
([buffer]
	(set-selected! (@state/app :editor-tabbed-panel) (index-of-buffer buffer)))
([tabbed-panel buffer]
	(set-selected! tabbed-panel (index-of-component tabbed-panel (:container buffer)))))

(defn show-buffer
[buffer]
	(set-selected! (@state/app :editor-tabbed-panel) (index-of-component (@state/app :editor-tabbed-panel) (:container buffer))))

(defn show-repl
[repl]
	(set-selected! (@state/app :repl-tabbed-panel) (index-of-repl repl)))

(defn next-tab []
	(let [tabbed-panel (@state/app :editor-tabbed-panel)]
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
	(let [tabbed-panel (@state/app :editor-tabbed-panel)]
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


(defn current-tab-file-name 
([]
	(current-tab-file-name (:editor-tabbed-panel @state/app)))
([tabbed-panel]
	(.getAbsolutePath (get-meta (select (current-tab tabbed-panel) [:#editor]) :file))))

(defn current-text-area 
([]
	(current-text-area (@state/app :editor-tabbed-panel)))
([tabbed-panel]
	(select (current-tab tabbed-panel) [:#editor])))

(defn current-buffers []
	(get @state/app :current-buffers))

(defn current-buffer-uuid
([] (current-buffer-uuid (@state/app :editor-tabbed-panel)))
([tabbed-panel]
	(get-meta (current-text-area tabbed-panel) :uuid)))

(defn current-buffer 
([]
	(current-buffer (@state/app :editor-tabbed-panel)))
([tabbed-panel]
  (if (tabs? tabbed-panel)
	(do 
		(let [uuid (current-buffer-uuid tabbed-panel)
			  buffers @(current-buffers)]
  			(get buffers uuid)))
  	"No buffers are currently open")))

(defn mark-tab-state! 
[buffer kw]
(when-let [file-state (:state buffer)]
	(cond
		(= kw :clean)
			(do
				(swap! file-state assoc :clean true))
		(= kw :dirty)
			(do
				(swap! file-state assoc :clean false)))
	(.repaint (get-in buffer [:tab :container]))))

(defn mark-tab-clean! 
([buffer]
	(mark-tab-state! buffer :clean)))

(defn mark-current-tab-clean! []
	(mark-tab-clean! (current-buffer)))

(defn mark-tab-dirty!
([buffer]
(mark-tab-state! buffer :dirty)))

(defn mark-current-tab-dirty! [tabbed-panel i]
	(mark-tab-dirty! tabbed-panel (.getSelectedIndex tabbed-panel)))

(defn save-tab-selections []
	(let [current-index (current-tab-index)]
	  (write-value-to-prefs sketchpad-prefs "current-files" @(@state/app :current-files))
  	  (write-value-to-prefs sketchpad-prefs "current-tab" current-index)))

(defn close-tab 
([]
	(if (tabs? (@state/app :editor-tabbed-panel))
		(remove-tab! (current-buffer))))
([tabbed-panel]
	(if (tabs? tabbed-panel)
		(remove-tab! tabbed-panel (current-buffer)))))

(defn close-current-tab []
	(remove-tab! (@state/app :editor-tabbed-panel) (current-buffer)))

(defn get-file-from-tab-index [app i]
	(i @(app :current-files)))

(defn get-tab-rsta [tabbed-panel i]
	(select (component-at tabbed-panel i) [:#editor]))

(defn focus-buffer [buffer]
	(when (not (nil? buffer))
	  (.grabFocus (:text-area buffer))))

(defn focus-repl [repl]
	(when (not (nil? repl))
	  (.grabFocus (:text-area repl))))

(defn add-buffer [buffer]
	(add-tab! @(:title buffer) (:container buffer)))

(defn buffer-tab-component! [buffer]
	; (println buffer)
	; (println )
	; (println )
	; (println tab)
	(.setTabComponentAt (:editor-tabbed-panel @state/app) (index-of-buffer buffer) (get-in buffer [:tab :container])))

(defn repl-tab-component! [repl]
	(.setTabComponentAt (:repl-tabbed-panel @state/app) (index-of-repl repl) (get-in repl [:tab :container])))

(defn buffer-title!
([buffer] (buffer-title! buffer @(get-in buffer [:component :title])))
([buffer title]
	(title-at! (index-of-buffer buffer) title)))


(defn repl-title!
([repl] (buffer-title! repl))
([buffer title]
	(buffer-title! buffer title)))

(defn update-tree-selection-from-tab []
	(when (tabs?)
		(let [buffer (current-buffer)]
			(when-not @(:new-file? buffer)
				(let [file @(:file buffer)
					  file-path (.getAbsolutePath file)]
					(tree.utils/set-tree-selection file-path))))))

(defn current-repl-tab []
  (current-tab (@state/app :repl-tabbed-panel)))

(defn current-repl-text-area []
  (select (current-tab (@state/app :repl-tabbed-panel)) [:#editor]))

(defn current-repl-uuid []
  (get-meta (current-repl-text-area) :uuid))

