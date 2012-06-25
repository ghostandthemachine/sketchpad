(ns sketchpad.button-tab
	(:use [seesaw core color border graphics]
				[sketchpad option-windows tab-manager])
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
  (let [w          (/ (width c) 2)
        h          (height c)
        line-style (style :foreground base-color :stroke 2 :cap :round)
        d 2]
    (draw g
      (line (+ d w) d (- (+ w w) d) (- h d)) line-style
      (line (- (+ w w) d) d (+ d w) (- h d)) line-style)))

(defn tab-button [tabbed-pane parent-tab app]
	(let [btn (button :focusable? false
										:tip "close this tab"
										:minimum-size [20 :by 10]
										:size [20 :by 10]
										; :border (empty-border :thickness 5)
										:paint paint-tab-button
										:listen [:mouse-entered (fn [e] (config! (.getComponent e) :foreground mouse-over-color
																															:border (empty-border :thickness 5)))
													:mouse-exited (fn [e] (config!  (.getComponent e) :foreground base-color
																															:border (empty-border :thickness 5)))
													:mouse-pressed (fn [e] (do 
																										(config! (.getComponent e) 
																											:foreground pressed-color
																											:border (empty-border :thickness 3))
																										
																										;; CHECK FOR SAVE FIRST!!!!!!!
																										(let [i (.indexOfTabComponent tabbed-pane parent-tab)]
																										(close-or-save-option app (title-at tabbed-pane i))
																										
																										(swap! (app :current-files) (fn [files] (dissoc files i)))
																										(remove-tab! tabbed-pane  i))

													))])]
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
				label (proxy [JLabel] []
								(getText []
									(let [index (.indexOfTabComponent tabbed-pane btn-tab)]
										(if (not= -1 index)
											(do
												(println (.getTitleAt tabbed-pane index))
												(.getTitleAt tabbed-pane index))
											nil))))]
		(config! btn-tab :items[label btn])
		(config! label :foreground (color :white)
									 :class [:tab-label])
		;; constructor updates
		(doto btn-tab
			(.setOpaque false)
			(.setBorder (javax.swing.BorderFactory/createEmptyBorder 2 0 0 0))
			(.setMinimumSize (Dimension. 300 20))
			)
		))