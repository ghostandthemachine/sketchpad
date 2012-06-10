(ns sketchpad.toggle-vim-mode-action
	(:use [sketchpad.default-mode]
			[sketchpad.edit-mode]
			[sketchpad.vim-mode])
	(:import (org.fife.ui.rtextarea RecordableTextAction)))
(comment
	(require :reload 'sketchpad.toggle-vim-mode-action)
)
(defn make-text-action
	[action-name handler]
	(proxy [RecordableTextAction] [action-name]
		(actionPerformedImpl [event text-area]
			(handler event text-area))

		(getMacroID [] action-name)))

(def vim-mode (atom false))

(defn toggle-vim-mode!
	[rta]
	(if @vim-mode
  		(do 
			(println "toggle to default edit mode")
  			(edit-mode! :default rta (default-input-map))
  			(swap! vim-mode (fn [_] false)))
  		(do 
			(println "toggle to vim edit mode")
  			(edit-mode! :vim rta (vim-input-map))
  			(swap! vim-mode (fn [_] true)))))

(def actions
	{"toggle-vim-mode" (fn [_ ta] (toggle-vim-mode! ta))})

(defn add-actions-to-action-map
	[action-map]
	(doseq [[aname handler] actions]
		(.put action-map aname (make-text-action aname handler))))

