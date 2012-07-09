(ns sketchpad.repl-button-tab
	(:use [seesaw core color border graphics meta]
				[sketchpad option-windows])
	(:import (javax.swing JPanel JLabel BorderFactory AbstractButton JButton)
					 (javax.swing.plaf.basic.BasicButtonUI)
					 (java.awt.event ActionListener MouseListener)
					 (java.awt.BasicStoke)
					 (java.awt Color Dimension Graphics2D FlowLayout)
					 ))

(defn text-area-from-index [tabbed-panel i]
	(select (.getComponentAt tabbed-panel i) [:#editor]))

(def mouse-over-color (color 200 200 200))
(def base-color (color 150 150 150))
(def pressed-color (color 255 255 255))

(def current-tab-color (atom base-color))


(defn paint-tab-button [proj-color c g]
	"custom renderer for tab x"
  (let [w          (width c)
        h          (height c)
        line-style (style :foreground base-color :stroke 2 :cap :round)
        border-style (style :foreground proj-color :stroke 0.5)
        d 3
        lp 7]
	    ;; in clean state draw an X to close the tab
	    (draw g
	      (line lp lp (- w lp) (- h lp)) line-style
	      (line lp (- h lp) (- w lp) lp) line-style)
    	(draw g
	      (rounded-rect d d (- w d d) (- h d d) 5 5) border-style)))

(defn tab-button [tabbed-pane parent-tab app state c]
	(let [btn (button :focusable? false
										:tip "close this tab"
										:minimum-size [20 :by 20]
										:size [20 :by 20]
										:id :close-button
										:paint (partial paint-tab-button c))]
		(doto btn
			(.setBorderPainted false)
			; (.setContentAreaFilled false)
			(.setRolloverEnabled false)
			(.setFocusable false))
		(put-meta! btn :state state)
		btn))

(defn repl-button-tab [app tabbed-panel i project-color]
	(let [rta (text-area-from-index tabbed-panel i)
				state (get-meta rta :state)
				btn-tab (flow-panel :align :right
														; :border (empty-border :thickness 5)
														)
				btn (tab-button tabbed-panel btn-tab app state project-color)
				label (proxy [JLabel] []
								(getText []
									(let [index (.indexOfTabComponent tabbed-panel btn-tab)]
										(if (= (.getSelectedIndex tabbed-panel) i)
											(config! this :foreground :white)
											(config! this :foreground (color 155 155 155)))
										(if (not= -1 index)
											(.getTitleAt tabbed-panel index)
											nil))))]
		(config! btn-tab :items[label btn])
		(config! label :foreground (color :white) :focusable? false
									 :id (symbol (str "[:tab-label-" i "]")))
		;; constructor updates
		(doto btn-tab
			(.setOpaque false)
			(.setBorder (javax.swing.BorderFactory/createEmptyBorder 0 0 0 0))
			(.setMinimumSize (Dimension. 300 20))
			)
		))