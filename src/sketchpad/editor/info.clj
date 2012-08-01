(ns sketchpad.editor.info
	(:use [seesaw core border color graphics])
	(:require [sketchpad.config.config :as config]
		[seesaw.bind :as bind]
		[sketchpad.state.state :as state]
		[sketchpad.util.tab :as tab])
	(:import (org.fife.ui.rtextarea.RTextAreaBase)))

(defonce doc-title-atom (atom "no file open"))
(defonce doc-position-atom (atom (str "Line " "Column ")))

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

(defn buffer-info []
	(let [doc-position-label (label :text ""
													:border nil
													:foreground (color :white)
													:id :buffer-info-label)
		doc-title-label (label :text ""
													:border nil
													:foreground (color :white)
													:id :buffer-info-label)
		buffer-info-panel (horizontal-panel
										:items [[:fill-h 10] doc-position-label :fill-h doc-title-label [:fill-h 10]]
										:background config/app-color
										:border nil
										:maximum-size [10000 :by 20] ;; HACK. need to figure out the safe way to set max height when no tab is present
										:id :buffer-info-panel
										:paint paint-info-panel)
		buffer-info {:type :buffer-info
					 :component {:container buffer-info-panel 
					             :doc-title-label doc-title-label
					         	 :doc-position-label doc-position-label}
					 :buffer-title-atom doc-title-atom
					 :buffer-position-atom doc-position-atom}]
		(swap! state/app 
			(fn [a] 
				(assoc a :buffer-info buffer-info :doc-position-atom doc-position-atom :doc-title-atom doc-title-atom :doc-title-label doc-title-label)))
	  	(bind/bind doc-title-atom (bind/transform (fn [s] s)) (bind/property doc-title-label :text))
		(bind/bind doc-position-atom (bind/transform (fn [s] s)) (bind/property doc-position-label :text))
		buffer-info))
