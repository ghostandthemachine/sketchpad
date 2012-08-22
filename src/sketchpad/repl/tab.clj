(ns sketchpad.repl.tab
	(:use [seesaw core color border graphics meta]
				[sketchpad.util.option-windows])
	(:import (javax.swing JPanel JLabel BorderFactory AbstractButton JButton)
					 (javax.swing.plaf.basic.BasicButtonUI)
					 (java.awt.event ActionListener MouseListener)
					 (java.awt.BasicStoke)
					 (java.awt Color Dimension Graphics2D FlowLayout))
	(:require [sketchpad.state.state :as state]
				[sketchpad.config.styles :as styles]
				[sketchpad.project.project :as sketchpad.project]
				[seesaw.bind :as bind]
				[seesaw.font :as font]
				[sketchpad.config.prefs :as sketchpad.config.prefs]))	

(defn paint-button 
"custom renderer for tab x"
[repl c g]
	(when sketchpad.config.prefs/show-tabs?
		(let [project-color @(sketchpad.project/repl-color repl)
				w          (width c)
		    h          (height c)
		    line-style (style :foreground styles/base-color :stroke 2 :cap :round)
		    border-style (style :foreground project-color :stroke 0.5)
		    d 3
		    lp 7]
		(draw g
		  (line lp lp (- w lp) (- h lp)) line-style
		  (line lp (- h lp) (- w lp) lp) line-style)
		(draw g
		  (rounded-rect d d (- w d d) (- h d d) 5 5) border-style))))

(defn tab-button [repl]
	(let [button (button :focusable? false
		  			  :tip "close this tab"
		  			  :minimum-size [20 :by 20]
		  			  :size [20 :by 20]
		  			  :id :close-button
		  			  :paint (partial paint-button repl))]
		(doto button
			(.setBorderPainted false)
			(.setRolloverEnabled false)
			(.setFocusable false))
		button))

(defn button-tab [repl]
	(let [label (label :text @(:title repl) 
		  				:foreground (color :white)
		  				:focusable? false
              :font (font/font "MENLO-10"))
		  button (tab-button repl)
		  container (flow-panel :align :right :items [label button])]
		(bind/bind (:title repl) (bind/transform (fn [s] s)) (bind/property label :text))
		(doto container
			(.setOpaque false)
			(.setBorder (javax.swing.BorderFactory/createEmptyBorder 0 0 0 0))
			(.setMinimumSize (Dimension. 300 20)))
		{:type :tab
		 :button button
		 :label label
		 :container container
		 :label-color (atom (color :white))}))


(defn label-tab [repl]
	(let [label (label :text @(:title repl) 
		  				:foreground (color :white)
		  				:focusable? false)
		  container (flow-panel :align :right :items [label])]
		(bind/bind (:title repl) (bind/transform (fn [s] s)) (bind/property label :text))
		(doto container
			(.setOpaque false)
			(.setBorder (javax.swing.BorderFactory/createEmptyBorder 0 0 0 0))
			(.setMinimumSize (Dimension. 300 20)))
		{:type :tab
		 :label label
		 :container container
		 :label-color (atom (color :white))}))


