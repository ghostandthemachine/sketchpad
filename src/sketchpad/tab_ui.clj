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

(defn unselected-tab-graient [x1 y1 x2 y2] (linear-gradient :start [x1 y1] :end [x2 y2] :fractions [0 0.3 1] :colors [(color 46 47 44) (color 57 57 55) (color 77 77 75)]))
(defn selected-tab-graient [x1 y1 x2 y2] (linear-gradient :start [x1 y1] :end [x2 y2] :fractions [0.8 1] :colors [(color 39 40 34) (color 54 55 50)]))

(defonce bg-img (ImageIcon. "img/Clojure-glyph-overlay.png"))

(defn tab-shape [rect kw]
	(let [ow  (.getWidth rect)
				h  (.getHeight rect)
				step-size (/ h 2)
				unit (/ step-size 6)
				x  (+ (/ step-size 2) (.getX rect))
		 		y  (.getY rect)
		 		w (- (+ ow step-size) step-size step-size)

				x0 (- x step-size)
				y0 (+ y h)
				cx1 (+ x step-size)
				cy1 (+ y h)
				cx2 x
				cy2 (+ y unit)
				x3 (+ x step-size)
				y3 y

				x4 (- (+ x w) step-size)
				y4 y

				cx5 (+ x w)
				cy5 (+ y unit)
				cx6 (- (+ x w) step-size)
				cy6 (+ y h)
				x7 (+ (+ x w) step-size)
				y7 (+ y h)

				]
	(cond 
		(= kw :closed)
		(doto (java.awt.geom.GeneralPath.)
			(.moveTo x0 y0)
			(.curveTo cx1 cy1 cx2 cy2 x3 y3)
			(.lineTo x4 y4)
			(.curveTo cx5 cy5 cx6 cy6 x7 y7)
			(.closePath))
		(= kw :open)
		(doto (java.awt.geom.GeneralPath.)			
			(.moveTo x0 y0)
			(.curveTo cx1 cy1 cx2 cy2 x3 y3)
			(.lineTo x4 y4)
			(.curveTo cx5 cy5 cx6 cy6 x7 y7))
		)))

(def base-color (color 39 40 34))
(def base-color2 (color 57 57 55))

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
										(.setRenderingHint gfx RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON)
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
										
										(proxy-super paintTab gfx tab-placement rects tab-index icon-rect text-rect)


										; (let [current-rect (arr-get rects tab-index)
										; 			orig-color (.getColor gfx)
										; 	 		gfx2 (cast java.awt.Graphics2D gfx)
										; 	 		tab-shape (tab-shape current-rect)
										; 	 		gradient (rect-gradient current-rect 0.3 base-color base-color2)]
										; 	(draw gfx
										; 		tab-shape
										; 		(style :foreground :gray :background base-color))

										; 	)
									)
									(paintTabBackground [gfx tab-placement tab-index x y w h selected?]
										(if selected? 
												(do 
													(swap! bg (fn [_] selected-fill-color))
													(swap! bg (fn [_] fill-color))
													(push gfx
														(draw gfx
															(tab-shape (java.awt.Rectangle. x y w h) :closed)
															(style :background (selected-tab-graient x (+ y h) x y)))))
												(do 
													(push gfx	
														(draw gfx
														(tab-shape (java.awt.Rectangle. x y w h) :closed)
														(style :background (unselected-tab-graient x (+ y h) x y)))))
										))
									
									(calculateTabHeight [placement index font-height]
										(+ font-height height-pad))

									(paintTabBorder [gfx placement index x y w h selected]
										(.setRenderingHint gfx RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON)
										(if selected
											(do
												(push gfx	
													(draw gfx
													(line x (+ y h) (+ x w) (+ y h))
													(style :foreground base-color :thickness 1)))
												(push gfx	
													(draw gfx
													(tab-shape (java.awt.Rectangle. x y w h) :open)
													(style :foreground :gray :thickness 0.5))))
											(do
												(push gfx	
													(draw gfx
													(line x (+ y h) (+ x w) (+ y h))
													(style :foreground :gray :thickness 1)))
												(push gfx	
													(draw gfx
													(tab-shape (java.awt.Rectangle. x y w h) :open)
													(style :foreground :black :thickness 0.5))))

											)
										)
									(getContentBorderInsets [placement]
										(Insets. 2 0 0 0))

									(paintContentBorderTopEdge [gfx placement index x y w h]
										(push gfx	
											(draw gfx 
												(line x y (+ x w) y)
												(style :foreground :gray :stroke 1)))
										)
									; (paintContentBorder [gfx placement selected])

									(getTabInsets [placement index]
										(let [insets (proxy-super getTabInsets placement index)
												h-pad 6
												v-pad 0
												t (+ v-pad (.top insets))
												l (+ h-pad (.left insets))
												b (+ v-pad (.bottom insets))
												r (+ h-pad (.right insets))]
										(java.awt.Insets. t l b r)
										))
									)]
		tab-ui))