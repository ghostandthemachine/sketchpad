(ns sketchpad.ui.scroll-bar
	(:use [seesaw core color graphics style])
	(:import (javax.swing.plaf.basic BasicScrollPaneUI)
					 (java.lang.reflect.Array)
					 (java.awt.geom.GeneralPath)
					 (javax.swing ImageIcon UIManager)
					 (java.awt Dimension BasicStroke RenderingHints Insets Rectangle)))

(defn zero-button []
	(let [button (button :text "zero button")
		  dim (Dimension. 0 0)]
		(doto button
			(.setPreferredSize dim)
			(.setMinimumSize dim)
			(.setMaximumSize dim))
	button))

(defn sketchpad-scroll-bar-ui []
	(let [scrollbar-ui 
		(proxy [javax.swing.plaf.basic.BasicScrollBarUI	] []
			(createDecreaseButton [orientation]
				(zero-button))
			(createIncreaseButton [orientation]
				(zero-button))
			(paintThumb [g c rect]
				(println "paintThumb"))
			(paintTrack [g c rect]
				(println "paintTrack"))
			(getTrackBounds []
				(println "getTrackBounds")
				(Rectangle. 0 0 0 0))
			; (paint [g c]
			; 	(println "paint"))
			(paintDecreaseHighlight [g c]
				(println "paintDecreaseHighlight"))
			(paintIncreaseHighlight [g c]
				(println "paintIncreaseHighlight"))
			(preferredLayoutSize [c]
				(println "preferredLayoutSize")))]
	scrollbar-ui))
										
