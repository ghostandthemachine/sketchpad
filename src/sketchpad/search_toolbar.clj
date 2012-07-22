(ns sketchpad.search-toolbar
	(:use [sketchpad tab]
		  [seesaw core]))

(defn find-field-listener [app])

(defn search-toolbar [app-atom]
	(let [app @app-atom
			find-field (text )
			find-btn (button :listen [:action find-btn-listener])
			find-prev-btn (button )
			match-case-check-box (checkbox)
			info-label (label)
			search-bar (toolbar :floatable?  false :orientation :horizontal)]

		(listen 
			find-btn :action 
			(fn [e]
				(let [text-not-found "Nothing found for search parameters"
							action-command (.getActionCommand e)
							forward (if (= action-command "FindNext") true false)
							text (config find-field :text)
							current-rta (current-buffer app)
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
	search-bar))


(defn show-find-panel [app]
	(let [split-panel (:info-label app)
	(config! split-panel :top (:search-bar app))]))