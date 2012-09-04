(ns sketchpad.repl.info
	(:use [seesaw core border color graphics])
	(:require [sketchpad.config.config :as config]
		[seesaw.bind :as bind]
		[sketchpad.state.state :as state]
		[sketchpad.util.tab :as tab]))

(defonce repl-title-atom (atom ""))
(defonce repl-info-atom (atom (str "Line " "Column ")))

(defn format-position-str [line column]
  (str "Line " line ", Column " column))

(defn get-coords [text-comp offset]
	(invoke-later
		(let [row (.getLineOfOffset text-comp offset)
		    col (- offset (.getLineStartOffset text-comp row))]
		[row col])))

(defn get-caret-coords [text-comp]
	(invoke-later
		(get-coords text-comp (.getCaretPosition text-comp))))

(defn update-repl-info-label!
"Update the repl info position label."
[tabbed-panel e]
	(let [current-text-area (tab/current-text-area tabbed-panel)
	      coords (get-caret-coords current-text-area)]
			(swap! (@state/app :repl-info-atom) (fn [_] (format-position-str (first coords) (second coords))))
	(swap! (@state/app :repl-info-atom) (fn [_] ""))))

(defn update-repl-title-label!
"Update the currently displayed repl title in the info panel"
[tabbed-panel e]
	(if (tab/tabs? tabbed-panel)
		(config! (:repl-title-label @state/app) :text (tab/title tabbed-panel))
		(config! (:repl-title-label @state/app) :text "")))

(defn attach-caret-handler [text-area]
	(listen text-area :caret-update (partial update-repl-info-label! (get-in (:repl-tabbed-panel @state/app) [:component :container]))))

(defn attach-repl-info-handler [app-atom]
	(let [tabbed-panel (get-in (:repl-tabbed-panel @app-atom) [:component :container])]
		(listen tabbed-panel :selection (partial update-repl-title-label! tabbed-panel))
  		(listen tabbed-panel :selection (partial update-repl-info-label! tabbed-panel))))

(defn info-panel-bg []
	(seesaw.graphics/linear-gradient 
		:colors [(seesaw.color/color 75 75 75) (seesaw.color/color 90 90 90)] 
		:fractions [0 0.9]
		:start [0 12]
		:end [0 0]))

(defn paint-info-panel [c g]
	(draw g
		(rect 0 0 (width c) (height c))
		(style :foreground (color 20 20 20) :background (info-panel-bg))))

(defn repl-info []
	(let [repl-info-label (label :text ""
									:font config/info-font
									:border nil
									:foreground (color :white)
									:id :repl-info-label)
		repl-title-label (label :text ""
							    :font config/info-font
							    :border nil
							    :foreground (color :white)
							    :id :repl-info-label)
		repl-info {:type :repl-info
			       :component {:container (horizontal-panel :items [[:fill-h 10] repl-info-label :fill-h repl-title-label [:fill-h 10]]
															:background config/app-color
															; :border (seesaw.border/empty-border :thickness 5)
															:maximum-size [10000 :by 20] ;; HACK. need to figure out the safe way to set max height when no tab is present
															:id :repl-info
															:paint paint-info-panel)}}]
		(swap! state/app (fn [a] (assoc a :repl-info repl-info :repl-info-atom repl-info-atom :repl-title-atom repl-title-atom :repl-title-label repl-title-label)))
	  	(bind/bind repl-title-atom (bind/transform (fn [s] s)) (bind/property repl-title-label :text))
		(bind/bind repl-info-atom (bind/transform (fn [s] s)) (bind/property repl-info-label :text))
		repl-info))
