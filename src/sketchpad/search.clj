(ns sketchpad.search
	(:use [seesaw core border color]))

(defn search-toolbar [app-atom]
	(let [app @app-atom
			find-field (text )
			find-btn (button )
			find-prev-btn (button )
			match-case-check-box (checkbox)
			info-label (label)
			search-bar (toolbar :floatable?  false :orientation :horizontal)]

		;; create the find button listener here so we can acces other
		;; search panel options
		(listen 
			find-btn :action 
			(fn [e]
				(let [text-not-found "Nothing found for search parameters"
							action-command (.getActionCommand e)
							forward (if (= action-command "FindNext") true false)
							text (config find-field :text)
							current-rta (current-text-area app)
							found (SearchEngine/find 
											current-rta 
											text 
											forward	
											(.isSelected match-case-check-box)
											false
											false)]
					(if found
						(config! info-label :text "")
						(do 
							(config! info-label 
								:foreground (color :red)
								:text text-not-found)
							(.provideErrorFeedback (UIManager/getLookAndFeel ) find-field))))))
	(swap! app-atom (fn [app] (assoc app :search-bar search-bar)))
	search-bar
))

(defnonce toggle-search-panel (atom false))

(defn show-find-panel [app]
	(let [split-panel (:info-panel app)
	(swap! toggle-search-panel (fn [_] true))
	(add! split-panel (:search-bar app))]))

(defn hide-find-panel [app]
	(let [split-panel (:info-panel app)
	(swap! toggle-search-panel (fn [_] false))
	(remove! split-panel (:search-bar app))]))

(defn toggle-search [app] 
	(if @toggle-search-panel
		(show-find-panel app)
		(hide-find-panel)))