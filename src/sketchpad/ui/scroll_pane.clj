(ns sketchpad.ui.scroll-pane
	(:use [seesaw core color graphics style])
	(:import (javax.swing.plaf.basic BasicScrollBarUI)
					 (java.lang.reflect.Array)
					 (java.awt.geom.GeneralPath)
					 (javax.swing ImageIcon UIManager JScrollBar)
					 (java.awt Dimension BasicStroke RenderingHints Insets Rectangle)))

(defn empty-button[]
	(let [btn (button :maximum-size [0 :by 0] :size [0 :by 0])]
	btn))

(defn sketchpad-scroll-pane-ui []
	(let [scrollbar-ui 
		(proxy [javax.swing.plaf.basic.BasicScrollPaneUI	] []
			(paintTrack [gfx c bounds]
				(println "paintTrack"))
			(paintThumb [gfx c bounds]
				(println "paintThumb"))

			(getMaximumSize [c]
				(println "getMaximumSize")
				(Dimension. 0 0))

			(getTrackBounds []
				(println "getTrackBounds"))
			(paintIncreaseHighlight [gfx]
				(println "paintIncreaseHighlight"))

			(getCorners [k]))]
				scrollbar-ui))
										
(defn s-scroll-bar []
	(let [scroll-bar 
			(proxy [JScrollBar] []
				(paint [gfx c]))]
		scroll-bar))