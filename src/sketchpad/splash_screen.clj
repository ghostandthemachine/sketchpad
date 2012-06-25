(ns sketchpad.splash-screen
	(:use [seesaw core border color])
	(:import (javax.swing.ImageIcon)
						(java.awt.Dimension)
						[java.awt.Toolkit]
						(javax.swing.JWindow)))


(defn splash-screen []
	(let [
		;tk (java.awt.Toolkit/getDefaultToolKit)
			img (javax.swing.ImageIcon. "img/sketchpad-icon.png")
			lbl (label :icon img)
			window (window :content lbl)
			;	dim (java.awt.Dimension. (.getScreenSize tk))
				; x (- (.getWidth dim) (/ (.getWidth window) 2))
				; y (- (.getHeight dim) (/ (.getHeight window) 2))
				x 10000
				y 500
				]
		(.pack window)
		(.setLocation window x y)
		window))