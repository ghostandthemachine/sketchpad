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

; (defn set-color [gfx c]
; 	(.setColor gfx c))

; (defn move [path x y]
; 	(.moveTo path x y))

; (defn curve [path x1 y1 x2 y2 x3 y3]
; 	(.curveTo path x1 y1 x2 y2 x3 y3))

; (defn line-to [path x y]
; 	(.line-toTo path x y))

; (defn draw [path shape]
; 	(.draw path shape))

; (defn fill [path shape]
; 	(.fill path shape))

; (defn close [path]
; 	(.closePath path))

; (defn paint [path color]
; 	(.setPaint path color))

; (defn stroke [gfx s]
; 	(.setStroke gfx s))

; (defn basic-stroke [size]
; 	(java.awt.BasicStroke. size))

; (defn rendering-hint! [k v]
; 	(.setRenderingHint k v))


; ;;; tab renderer values
; (defn browser-tab [gfx2 rects tab-index]
; 	(let [tab-path (java.awt.geom.GeneralPath.) 
; 				unit 8
; 				w  (- (.getWidth (arr-get rects tab-index)) unit)
; 				h  (.getHeight (arr-get rects tab-index))
; 				x  [(.getX (arr-get rects tab-index)) ;; normal x starting point
; 					  (+ (.getX (arr-get rects tab-index)) unit) ;; norm x + angle offset
; 					  (+ (.getX (arr-get rects tab-index)) w) ;; normal x + w 
; 					  (+ (.getX (arr-get rects tab-index)) w unit)] ;; normal x + w 
; 				y	 [( + (.getY (arr-get rects tab-index)) h)
; 					  (.getY (arr-get rects tab-index))
; 					  (.getY (arr-get rects tab-index))
; 					  ( + (.getY (arr-get rects tab-index)) h)]]
; 		(doto tab-path
; 			(move (get x 0) (get y 0))
; 			(line-to (get x 1) (get y 1))
; 			(line-to (get x 2) (get y 2))
; 			(line-to (get x 3) (get y 3))
; 			(close))
; 			tab-path))

; (defn browser-tab-border [gfx2 rects tab-index]
; 	(let [tab-path (java.awt.geom.GeneralPath.) 
; 				unit 8
; 				w  (- (.getWidth (arr-get rects tab-index)) unit)
; 				h  (.getHeight (arr-get rects tab-index))
; 				x  [(.getX (arr-get rects tab-index)) ;; normal x starting point
; 					  (+ (.getX (arr-get rects tab-index)) unit) ;; norm x + angle offset
; 					  (+ (.getX (arr-get rects tab-index)) w) ;; normal x + w 
; 					  (+ (.getX (arr-get rects tab-index)) w unit)] ;; normal x + w 
; 				y	 [( + (.getY (arr-get rects tab-index)) h)
; 					  (.getY (arr-get rects tab-index))
; 					  (.getY (arr-get rects tab-index))
; 					  ( + (.getY (arr-get rects tab-index)) h)]]
; 		(doto tab-path
; 			(move (get x 0) (get y 0))
; 			(line-to (get x 1) (get y 1))
; 			(line-to (get x 2) (get y 2))
; 			(line-to (get x 3) (get y 3)))
; 			tab-path))

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
											(println selected?)

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