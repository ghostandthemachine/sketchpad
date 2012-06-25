(ns sketchpad.tab-ui
	(:use [seesaw core color graphics style])
	(:import (javax.swing.plaf.basic.BasicTabbedPaneUI)
					 (java.lang.reflect.Array)
					 (java.awt.geom.GeneralPath)
					 (javax.swing.UIManager)
					 (java.awt.BasicStroke)
					 (java.awt.RenderingHints)
					 (java.awt.Insets)))

(defn arr-get [array n]
	(java.lang.reflect.Array/get array n))

(def selected-fill-color (color 39 40 34))
(def fill-color (color 74 74 71))
(def border-color (color 150 150 150))
(def tab-bg-color (color 0 0 0))

(defn sketchpad-tab-ui []
	(let [bg (atom fill-color)
				height-pad 10
				tab-ui  (proxy [javax.swing.plaf.basic.BasicTabbedPaneUI] []
									(paintTab [gfx tab-placement rects tab-index icon-rect text-rect]
										; (let [orig-color (.getColor gfx)
										; 	 		gfx2 (cast java.awt.Graphics2D gfx)
										; 	 		rect (aget rects 0)
										; 	 		x  (.getX (arr-get rects tab-index))
										; 	 		y  (.getY (arr-get rects tab-index))
										; 	 		w  (.getWidth (arr-get rects tab-index))
										; 			h  (.getHeight (arr-get rects tab-index))
										; 	 		]
										; 	 	(doto gfx2
										; 	 		(draw 
										; 	 			(rounded-rect x y w h 8 8)
										; 	 			(style :stroke (stroke :width 1) :background @bg)))
										(proxy-super paintTab gfx tab-placement rects tab-index icon-rect text-rect)

											; )
									)
										(paintTabBackground [gfx tab-placement tab-index x y w h selected?]
											(if selected? 
												(swap! bg (fn [_] selected-fill-color))
												(swap! bg (fn [_] fill-color)))
											(proxy-super paintTabBackground gfx tab-placement tab-index x y w h selected?)
											)

									(calculateTabHeight [placement index font-height]
										(+ font-height height-pad))

									(getTabInsets [placement index]
										(let [insets (proxy-super getTabInsets placement index)
													t (.top insets)
													l (.left insets)
													b (.bottom insets)
													r (.right insets)
													pad 10]
										; (proxy-super getTabInsets placement index)
										(java.awt.Insets. t (- l pad) b (- r pad))
										))
									)]
									

	

		tab-ui))