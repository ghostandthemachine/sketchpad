(ns sketchpad.scroll-bar-ui
	(:use [seesaw core color graphics style])
	(:import (javax.swing.plaf.basic BasicScrollPaneUI)
					 (java.lang.reflect.Array)
					 (java.awt.geom.GeneralPath)
					 (javax.swing ImageIcon UIManager)
					 (java.awt Dimension BasicStroke RenderingHints Insets Rectangle)))

(defn sketchpad-scroll-bar-ui []
	(let [scrollbar-ui 
		(proxy [javax.swing.plaf.basic.BasicScrollBarUI	] []
			(paintThumb [gfx c bounds])

			(createUI []
				(sketchpad-scroll-bar-ui))

			(getMaximumSize [c]
				(println "getMaximumSize")
				(Dimension. 0 0))

			(getTrackBounds []
				(Rectangle. 0 0 0 0))
				
			(paintIncreaseHighlight [gfx]
				(println "paintIncreaseHighlight")))]
	scrollbar-ui))
										
