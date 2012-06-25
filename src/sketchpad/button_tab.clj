(ns sketchpad.button-tab
	(:use [seesaw core color border graphics]
				[sketchpad option-windows])
	(:import (javax.swing JPanel JLabel BorderFactory AbstractButton JButton)
					 (javax.swing.plaf.basic.BasicButtonUI)
					 (java.awt.event ActionListener MouseListener)
					 (java.awt.BasicStoke)
					 (java.awt Color Dimension Graphics2D FlowLayout)
					 ))

(def mouse-over-color (color 200 200 200))
(def base-color (color 150 150 150))
(def pressed-color (color 255 255 255))


(defn paint-tab-button [c g]
	"custom renderer for tab x"
  [c g]
  (let [w          (width c)
        h          (height c)
        line-style (style :foreground base-color :stroke 2 :cap :round)
        d 2]
    (draw g
      (line d d (- w d) (- h d)) line-style
      (line d (- h d) (- w d) d) line-style)))

(defn tab-button [tabbed-pane parent-tab app]
	(let [btn (button :focusable? false
										:tip "close this tab"
										:minimum-size [20 :by 10]
										:size [10 :by 10]
										:id :close-button
										; :border (empty-border :thickness 5)
										:paint paint-tab-button)]
		(doto btn
			(.setBorderPainted false)
			(.setContentAreaFilled false)
			(.setRolloverEnabled false)
			(.setFocusable false))
		btn))

(defn button-tab [app tabbed-pane]
	(let [btn-tab (flow-panel :align :right
														:border (empty-border :thickness 0))
				btn (tab-button tabbed-pane btn-tab app)
				indicator (label :text "" 
												 :size [10 :by 10]
												 :class :tab-comp
												 :id :indicator)
				label (proxy [JLabel] []
								(getText []
									(let [index (.indexOfTabComponent tabbed-pane btn-tab)]
										(if (not= -1 index)
											(.getTitleAt tabbed-pane index)
											nil))))]
		(config! btn-tab :items[label indicator btn])
		(config! label :foreground (color :white)
									 :class [:tab-label])
		;; constructor updates
		(doto btn-tab
			(.setOpaque false)
			(.setBorder (javax.swing.BorderFactory/createEmptyBorder 2 0 0 0))
			(.setMinimumSize (Dimension. 300 20))
			)
		))