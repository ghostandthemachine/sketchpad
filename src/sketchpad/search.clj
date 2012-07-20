(ns sketchpad.search
	(:use [seesaw core color]
		  [sketchpad tab layout-config])
	(:require [sketchpad.search-engine :as search])
	(:import (javax.swing UIManager)))

; (defn search-toolbar [app-atom]
; 	(let [app @app-atom
; 			find-field (text :text "")
; 			find-btn (button :text "Find")
; 			find-prev-btn (button :text "Prev")
; 			find-next-btn (button :text "Next")
; 			match-case-check-box (checkbox)
; 			info-label (label :text "")
; 			search-toolbar (horizontal-panel 
; 									; :orientation :horizontal
; 									:items [find-field find-btn find-prev-btn find-next-btn match-case-check-box info-label]
; 									)]

; 		;; create the find button listener here so we can acces other
; 		;; search panel options
; 		 (listen 
; 		 	find-btn :action 
; 		 	(fn [e]
; 		 		(let [text-not-found "Nothing found for search parameters"
; 		 					action-command (.getActionCommand e)
; 		 					forward (if (= action-command "FindNext") true false)
; 		 					text (config find-field :text)
; 		 					current-rta (current-text-area (app :editor-tabbed-panel))
; 		 					found (search/find 
; 		 									current-rta 
; 		 									text 
; 		 									forward	
; 		 									(.isSelected match-case-check-box)
; 		 									false
; 		 									false)]
; 		 			(if found
; 		 				(config! info-label :text "")
; 		 				(do 
; 		 					(config! info-label 
; 		 						:foreground (color :red)
; 		 						:text text-not-found)
; 		 					(.provideErrorFeedback (UIManager/getLookAndFeel ) find-field))))))
; 	(swap! app-atom (fn [app] (assoc app :search-toolbar search-toolbar)))
; 	search-toolbar))
