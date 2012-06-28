(ns sketchpad.tab-ui
	(:use [seesaw core color graphics style])
	(:import (javax.swing.plaf.basic.BasicTabbedPaneUI)
					 (java.lang.reflect.Array)
					 (java.awt.geom.GeneralPath)
					 (javax.swing ImageIcon UIManager)
					 (java.awt BasicStroke RenderingHints Insets)))

(defn arr-get [array n]
	(java.lang.reflect.Array/get array n))

(def selected-fill-color (color 39 40 34))
(def fill-color (color 74 74 71))
(def border-color (color 150 150 150))
(def tab-bg-color (color 0 0 0))

(defonce bg-img (ImageIcon. "img/Clojure-glyph-overlay.png"))

(defn tab-shape [rect]
	(let [ow  (.getWidth rect)
				h  (.getHeight rect)
				step-size (/ h 2)
				qrtr-unit (/ step-size 4)
				unit (/ step-size 6)
				x  (+ (/ step-size 2) (.getX rect))
		 		y  (.getY rect)
		 		w (- (+ ow step-size) step-size step-size)
				left-step-size step-size
				right-step-size (- 0 step-size)

				; x1 (- x step-size)
				; y1 (+ y h)

				; x2 (+ x third-unit)
				; y2 (- (+ y h) qrtr-unit)

				; x3 (- (+ x left-step-size) qrtr-unit)
				; y3 (+ y qrtr-unit)

				; x4 (+ x1 step-size qrtr-unit)
				; y4 y

				; x5 (- (+ x w) (+ step-size qrtr-unit))
				; y5 y4

				; x6 (- (+ x w qrtr-unit) step-size)
				; y6 y3

				; x7 (- (+ x w) qrtr-unit)
				; y7 y2

				; x8 (+ x w step-size)
				; y8 y1


				x1 (- x step-size)
				y1 (+ y h)

				cx1 (+ x1 unit (/ unit 2))
				cy1 (- y1 unit (/ unit 2))

				x2 (+ x1 unit unit)
				y2 (- y1 unit unit)

				x3 (+ x unit)
				y3 (+ y unit unit)

				cx2 (+ x3 unit)
				cy2 (+ y unit)

				x4 (+ x step-size)
				y4 y

				x5 (- (+ x w) step-size)
				y5 y

				cx3 (- (+ x w unit) step-size)
				cy3 y

				x6 (- (+ x w) unit)
				y6 (+ y unit unit unit)

				x7 (+ x w unit unit)
				y7 (- (+ y h) unit unit unit)

				cx4 (+ x w unit unit unit)
				cy4 (- (+ y h) unit unit)

				x8 (+ x w step-size)
				y8 (+ y h)
				]
		(doto (java.awt.geom.GeneralPath.)
			(.moveTo x1 y1)
			(.curveTo x1 y1 cx1 cy1 x2 y2)
			(.lineTo x3 y3)
			(.curveTo x3 y3 cx2 cy2 x4 y4)
			(.lineTo x5 y5)
			(.curveTo x6 y6 cx3 cy3 x7 y7)
			(.lineTo x8 y8)
			(.curveTo x7 y2 cx4 cy1 x8 y1)
			(.moveTo x8 y1)
			(.closePath))))

(def base-color (color 39 40 34))
(def base-color2 (color 59 50 44))

(def base-color3 (color 113 111 89))
(def base-color4 (color 123 121 99))

(defn rect-gradient 
	([x y w h div c1 c2]
		(linear-gradient 
			:start [x ( + y (* h div))]
			:end [x y]
			:colors [c1 c2]))
	([rect div c1 c2]
		(linear-gradient 
			:start [(.getX rect) ( + (.getY rect) (* (.getHeight rect) div))]
			:end [(.getX rect) (.getY rect)]
			:colors [c1 c2])))

(defn sketchpad-tab-ui []
	(let [bg (atom fill-color)
				height-pad 10
				tab-ui  (proxy [javax.swing.plaf.basic.BasicTabbedPaneUI] []

									(paint [gfx comp]
										(proxy-super paint gfx comp)
										; (println comp)
										(if (= (.getTabCount comp) 0)
											(let [w (width comp)
												  	h (height comp)
												  	bg-img-width-offset (/ (.getIconWidth bg-img) 2)
														bg-img-height-offset (/ (.getIconHeight bg-img) 2)
												  	x (- (/ w 2) bg-img-width-offset)
												  	y (- (/ h 2) bg-img-height-offset)]
												(draw gfx 
													(rect 0 0 w h)
													(style :background :black))
												(.paintIcon bg-img comp gfx x y))))

									(paintTab [gfx tab-placement rects tab-index icon-rect text-rect]
										


										(let [current-rect (arr-get rects tab-index)
													orig-color (.getColor gfx)
											 		gfx2 (cast java.awt.Graphics2D gfx)
											 		tab-shape (tab-shape current-rect)
											 		gradient (rect-gradient current-rect 0.3 base-color base-color2)]
											(draw gfx
												tab-shape
												(style :foreground :gray :background base-color))
										; (proxy-super paintTab gfx tab-placement rects tab-index icon-rect text-rect)

											)
									)
									(paintTabBackground [gfx tab-placement tab-index x y w h selected?]
										(if selected? 
												(do 
													(swap! bg (fn [_] selected-fill-color))
													(swap! bg (fn [_] fill-color))
													(push gfx
														(draw gfx
															tab-shape
															(style :background :gray))))
												(do 
													(push gfx	
														(draw gfx
														tab-shape
														(style :background :blue))))
										))
									
									(calculateTabHeight [placement index font-height]
										(+ font-height height-pad))

									; (paintTabBorder [gfx placement index x y w h selected]
									; 	; (println "paintTabBorder")
									; 	; (println gfx placement index x y w h selected)
									; 	; (if (not selected)
									; 	; 	(push gfx	
									; 	; 		(draw gfx 
									; 	; 			(line x (+ y h) (+ x w) (+ y h))
									; 	; 			(style :foreground :white :stroke 1))))
									; 	)
									(getContentBorderInsets [placement]
										(Insets. 2 0 0 0))

									(paintContentBorderTopEdge [gfx placement index x y w h]
										(push gfx	
											(draw gfx 
												(line x y (+ x w) y)
												(style :foreground :white :stroke 1)))
										)
									; (paintContentBorder [gfx placement selected])

									(getTabInsets [placement index]
										(let [insets (proxy-super getTabInsets placement index)
												h-pad 0
												v-pad 4
												t (+ v-pad (.top insets))
												l (+ h-pad (.left insets))
												b (+ v-pad (.bottom insets))
												r (+ h-pad (.right insets))]
										(java.awt.Insets. t l b r)
										))
									)]
									

	

		tab-ui))