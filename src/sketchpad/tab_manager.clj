(ns sketchpad.tab-manager
	(:use [sketchpad option-windows editor-component]
				[clojure.string]
				[seesaw meta core])
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

(defn add-tab! [tabbed-panel title comp]
	(.addTab tabbed-panel title comp))

(defn remove-tab! [tabbed-panel index]
	(.removeTabAt tabbed-panel index))

(defn tab-count [tabbed-panel]
	(.getTabCount tabbed-panel))

(defn tabs? [tabbed-panel]
	(> (tab-count tabbed-panel) 0))

(defn current-tab-index [app]
	(let [tabbed-panel (app :editor-tabbed-panel)]
		(if (tabs? tabbed-panel)
			(do 
				(.getSelectedIndex tabbed-panel))
				-1)))

(defn current-tab [app]
	(if (tabs? (app :editor-tabbed-panel))
		(.getComponentAt (app :editor-tabbed-panel) (current-tab-index app))
		nil))

(defn open? [app file-name]
	(let [tabbed-panel (app :editor-tabbed-panel)]
		(if (= -1 (.indexOfTab tabbed-panel file-name))
			false
			true)))

(defn index-of [app name]
	(.indexOfTab (app :editor-tabbed-panel) name))

(defn set-selected! [tabbed-panel index]
	(.setSelectedIndex tabbed-panel index))


(defn mark-tab-clean! [tabbed-panel i]
	(let [last-char (str (get (title-at tabbed-panel i) (- (.length (title-at tabbed-panel i)) 1)))]
		(if (= last-char "*")
			(do 
				(title-at! tabbed-panel i (chop (title-at tabbed-panel i)))
				(let [rsta (select tabbed-panel [:#editor])]
					(swap! (get-meta rsta :state) (fn [_] :clean))
					(println (get-meta rsta :state))
				(println (title-at tabbed-panel i))

				)))))

(defn mark-current-tab-clean! [tabbed-panel]
	(mark-tab-clean! tabbed-panel (.getSelectedIndex tabbed-panel)))

(defn show-tab! [app-atom index file]
	(let [comp (component-at (@app-atom :editor-tabbed-panel) index)
				rsta (select comp [:#editor])
				tabbed-panel (@app-atom :editor-tabbed-panel)]
		(swap! app-atom (fn [app] (assoc app :doc-text-area rsta)))
		(swap! (@app-atom :current-files) (fn [files] (assoc files index file)))
		;; update the current global focussed file
		(swap! (@app-atom :current-file) (fn [_] file))
		(set-selected! tabbed-panel index)
		))

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

(defn close-current-tab [app]
	(let [tabbed-panel (app :editor-tabbed-panel) 
				current-index (.getSelectedIndex tabbed-panel)
				num-tabs (.getTabCount tabbed-panel)]
		(if (> num-tabs 0)
			(do
				;; CHECK FOR SAVE FIRST!!!!!!!
				(close-or-save-option app (title-at tabbed-panel current-index))
				(swap! (app :current-files) (fn [files] (dissoc files current-index)))
				(remove-tab! tabbed-panel  current-index)))))
				
