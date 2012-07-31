(ns sketchpad.util.tab
	(:import 
		(java.awt.event KeyEvent)
    (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
		(javax.swing JButton JOptionPane JWindow ImageIcon)
		(javax.swing.event DocumentListener))
	(:use [sketchpad.util.option-windows]
		[sketchpad.config.prefs]
				[clojure pprint]
				[seesaw meta core border])
	(:require [clojure.string :as string]
    		[sketchpad.tree.utils :as tree.utils]
			[sketchpad.state.state :as state]))

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
	(current-index (@state/app :editor-tabbed-panel)))
([tabbed-panel]
	(.getSelectedIndex tabbed-panel)))

(defn tabs? 
([]
	(tabs? (@state/app :editor-tabbed-panel)))
([tabbed-panel]
	(> (tab-count tabbed-panel) 0)))

(defn component-at [tabbed-panel index]
	(try 
		(.getComponentAt tabbed-panel index)
		(catch java.lang.ArrayIndexOutOfBoundsException e)))

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
([tabbed-panel]
	(let [idx (current-index tabbed-panel)]
		(if (>= idx 0)
			(title-at tabbed-panel idx)
			"no file open"))))

(defn index-of [tabbed-panel name]
	(.indexOfTab tabbed-panel name))

(defn set-selected!
([index] (set-selected! (@state/app :editor-tabbed-panel) index))
([tabbed-panel index]
	(.setSelectedIndex tabbed-panel index)))

(defn set-selected-component
	([c] 
		(set-selected-component (@state/app :editor-tabbed-panel) c))
	([tabbed-panel c]
		(.setSelectedComponent tabbed-panel c)))




(defn index-of-buffer [buffer]
	(.indexOfComponent (@state/app :editor-tabbed-panel) (get buffer :container)))

(defn index-of-repl 
([repl] (index-of-repl (@state/app :repl-tabbed-panel) repl))
([tabbed-panel repl]
	(.indexOfComponent tabbed-panel (get-in repl [:component :container]))))

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
	(.addTab (@state/app :repl-tabbed-panel) @(:title repl) (get-in repl [:component :container])))

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

(defn next-tab
	([] (next-tab (@state/app :editor-tabbed-panel)))
	([tabbed-panel]
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

(defn previous-tab 
	([] (previous-tab (@state/app :editor-tabbed-panel)))
	([tabbed-panel]
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

(defn current-editor-buffer 
([]
	(current-editor-buffer (@state/app :editor-tabbed-panel)))
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
	(mark-tab-clean! (current-editor-buffer)))

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
		(remove-tab! (current-editor-buffer))))
([tabbed-panel]
	(if (tabs? tabbed-panel)
		(remove-tab! tabbed-panel (current-editor-buffer)))))

(defn close-current-tab []
	(remove-tab! (@state/app :editor-tabbed-panel) (current-editor-buffer)))

(defn get-file-from-tab-index [app i]
	(i @(app :current-files)))

(defn get-tab-rsta [tabbed-panel i]
	(select (component-at tabbed-panel i) [:#editor]))

(defn focus [text-area]
	(.grabFocus text-area))

(defn focus-buffer [buffer]
	(when (not (nil? buffer))
	  (focus (:text-area buffer))))

(defn add-buffer [buffer]
	(add-tab! @(:title buffer) (:container buffer)))

(defn buffer-tab-component! [buffer]
	(.setTabComponentAt (:editor-tabbed-panel @state/app) (index-of-buffer buffer) (get-in buffer [:tab :container])))

(defn repl-tab-component! 
([repl] (repl-tab-component! (@state/app :repl-tabbed-panel) repl))
([tabbed-panel repl] (repl-tab-component! tabbed-panel repl (get-in repl [:tab :container])))
([tabbed-panel repl tab]
	(.setTabComponentAt tabbed-panel (index-of-repl tabbed-panel repl) tab)))

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
		(let [buffer (current-editor-buffer)]
			(when-not @(:new-file? buffer)
				(let [file @(:file buffer)
					  file-path (.getAbsolutePath file)]
					(tree.utils/set-tree-selection file-path))))))

(defn current-repl-tab []
  (current-tab (@state/app :repl-tabbed-panel)))

(defn current-repl-text-area []
  (select (current-tab (@state/app :repl-tabbed-panel)) [:#editor]))

(defn get-repl-uuid []
  (get-meta (current-repl-text-area) :uuid))

(defn current-repl []
	(first (filter #(= (get-repl-uuid) (:uuid %)) (mapcat :repls @(:projects @state/app)))))

(defn current-buffer 
([]
	(current-editor-buffer (@state/app :editor-tabbed-panel)))
([tabbed-panel]
  (if (tabs? tabbed-panel)
	(do 
		(let [uuid (current-buffer-uuid tabbed-panel)
			  buffers @(current-buffers)]
  			(get buffers uuid)))
  	"No buffers are currently open")))

(defn uuid-at [idx]
	(let [text-area (select (component-at tabbed-panel idx) [:#editor])]
		(get-meta text-area :uuid)))

(defn focus-editor-text-area
"Focus the current editor text area if a buffer is open."
	[]
	(if (tabs?)
		(focus (current-text-area))))

(defn focus-editor-repl []
	(set-selected-component (@state/app :repl-tabbed-panel) (@state/app :repl-container))
	(focus (@state/app :repl-container))
	(focus (@state/app :editor-repl)))

(defn focus-repl 
"Focus the REPL panel."
	([] (focus (current-repl-text-area)))
	([repl]
	(when (not (nil? repl))
	  (focus (:text-area repl)))))

