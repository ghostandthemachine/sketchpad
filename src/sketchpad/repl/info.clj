(ns sketchpad.repl.info
	(:use [seesaw core border color graphics])
	(:require [sketchpad.config.config :as config]
		[seesaw.bind :as bind]
		[sketchpad.state.state :as state]
		[sketchpad.util.tab :as tab]))

(defonce repl-title-atom (atom ""))
(defonce repl-position-atom (atom (str "Line " "Column ")))

(defn format-position-str [line column]
  (str "Line " line ", Column " column))

(defn get-coords [text-comp offset]
  (let [row (.getLineOfOffset text-comp offset)
        col (- offset (.getLineStartOffset text-comp row))]
    [row col]))

(defn get-caret-coords [text-comp]
  (get-coords text-comp (.getCaretPosition text-comp)))

(defn update-repl-position-label!
"Update the repl info position label."
[e]
	(if (tab/tabs? (@state/app :repl-tabbed-panel))
		(do
			(let [current-text-area (tab/current-text-area (@state/app :repl-tabbed-panel))
			      coords (get-caret-coords current-text-area)]
				(swap! (@state/app :repl-position-atom) (fn [_] (format-position-str (first coords) (second coords))))))
		(swap! (@state/app :repl-position-atom) (fn [_] ""))))

(defn update-repl-title-label!
"Update the currently displayed repl title in the info panel"
[e]
	(if (tab/tabs?)
		(config! (:repl-title-label @state/app) :text (tab/title (:repl-tabbed-panel @state/app)))
		(config! (:repl-title-label @state/app) :text "")))

(defn attach-caret-handler [text-area]
	(listen text-area :caret-update update-repl-position-label!))

(defn attach-repl-info-handler [tabbed-panel]
  (listen tabbed-panel :selection update-repl-title-label!)
  (listen tabbed-panel :selection update-repl-position-label!))

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
	(let [repl-position-label (label :text ""
													:border nil
													:foreground (color :white)
													:id :repl-info-label)
		repl-title-label (label :text ""
													:border nil
													:foreground (color :white)
													:id :repl-info-label)
		repl-info (horizontal-panel
										:items [[:fill-h 10] repl-position-label :fill-h repl-title-label [:fill-h 10]]
										:background config/app-color
										:border nil
										:maximum-size [10000 :by 20] ;; HACK. need to figure out the safe way to set max height when no tab is present
										:id :repl-info
										:paint paint-info-panel)]
		(swap! state/app (fn [a] (assoc a :repl-info repl-info :repl-position-atom repl-position-atom :repl-title-atom repl-title-atom :repl-title-label repl-title-label)))
	  	(bind/bind repl-title-atom (bind/transform (fn [s] s)) (bind/property repl-title-label :text))
		(bind/bind repl-position-atom (bind/transform (fn [s] s)) (bind/property repl-position-label :text))
		repl-info))
