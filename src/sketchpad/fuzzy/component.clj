(ns sketchpad.fuzzy.component
	(:use [seesaw.core])
	(:require [sketchpad.rsyntax :as rsyntax]
						[sketchpad.config.config :as config]
						[sketchpad.editor.buffer :as editor.buffer]
						[sketchpad.util.utils :as utils]
						[seesaw.color :as color]
						[seesaw.border :as border]
						[sketchpad.auto-complete.auto-complete :as auto-complete]))

(defonce fuzzy-visible (atom false))

(defn focus-lost-handler
	[panel text-area e]
	(reset! fuzzy-visible false)
	(invoke-later
		(config! panel :visible? false)
		(config! text-area :visible? false)))

(defn- focus-gained-handler
	[panel text-area e]
	(reset! fuzzy-visible true)
	(invoke-later
		(config! panel :visible? true)
		(config! text-area :visible? true :text "")))

(defn- enter-handler
	[panel text-area]
	(invoke-later
		(let [text-area (get-in (:fuzzy @sketchpad.state.state/app) [:component :text-area])
					cmd (config text-area :text)]
			(try
				(let [file-map (load-string cmd)]
					(when (.exists (clojure.java.io/file (:absolute-path file-map)))
						(editor.buffer/open-buffer (:absolute-path file-map) (:project file-map)))
						(reset! fuzzy-visible false)
						(config! panel :visible? false)
						(config! text-area :visible? false :text ""))
				(catch Throwable e
					(config! text-area :text ""))))))

(defn- esc-handler
	[panel text-area]
	(invoke-later
		(reset! fuzzy-visible false)
		(config! panel :visible? false)
		(config! text-area :visible? false :text "")))

(defn- attach-handlers
	[panel text-area]
	(listen text-area :focus-gained (partial focus-gained-handler panel text-area))
	(listen text-area :focus-lost (partial focus-lost-handler panel text-area))
	(utils/attach-action-keys text-area ["ENTER" (partial enter-handler panel text-area)])
	(utils/attach-action-keys text-area ["ESCAPE" (partial esc-handler panel text-area)]))

(defn component
	[]
	(let [text-area (rsyntax/text-area :id :fuzzy-text-area 
										:columns 200
										:rows 1
										:border (border/empty-border :thickness 5))
			panel (border-panel :center text-area
														:visible? false
														:maximum-size [1000 :by 15])
			fuzzy {:type :fuzzy-panel
					 :auto-complete (atom nil)
					 :component {:container panel
					 			  :text-area text-area}}]
		(config/apply-fuzzy-buffer-prefs! text-area)
		(attach-handlers panel text-area)
    	(auto-complete/install-fuzzy-provider fuzzy)
		fuzzy))