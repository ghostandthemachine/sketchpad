(ns sketchpad.repl.tab
	(:use [seesaw core color border graphics meta]
				[sketchpad option-windows])
	(:import (javax.swing JPanel JLabel BorderFactory AbstractButton JButton)
					 (javax.swing.plaf.basic.BasicButtonUI)
					 (java.awt.event ActionListener MouseListener)
					 (java.awt.BasicStoke)
					 (java.awt Color Dimension Graphics2D FlowLayout))
	(:require [sketchpad.state :as state]
				[sketchpad.styles :as styles]))	

; (defn text-area-from-index [tabbed-panel i]
; 	(select (.getComponentAt tabbed-panel i) [:#editor]))

(defn paint-tab-button [project-color c g]
	"custom renderer for tab x"
  (let [w          (width c)
        h          (height c)
        line-style (style :foreground styles/base-color :stroke 2 :cap :round)
        border-style (style :foreground project-color :stroke 0.5)
        d 3
        lp 7]
	    (draw g
	      (line lp lp (- w lp) (- h lp)) line-style
	      (line lp (- h lp) (- w lp) lp) line-style)
    	(draw g
	      (rounded-rect d d (- w d d) (- h d d) 5 5) border-style)))

(defn tab-button [project-color]
	(let [btn (button :focusable? false
										:tip "close this tab"
										:minimum-size [20 :by 20]
										:size [20 :by 20]
										:id :close-button
										:paint (partial project-color))]
		(doto btn
			(.setBorderPainted false)
			(.setRolloverEnabled false)
			(.setFocusable false))
		btn))

(defn repl-tab-label [repl-tabbed-panel repl-component]
	(proxy [JLabel] []
						(getText []
							(let [index (.indexOfTabComponent repl-tabbed-panel repl-component)]
								(if (= (.getSelectedIndex repl-tabbed-panel) index)
									(config! this :foreground :white)
									(config! this :foreground (color 155 155 155)))
								(if (not= -1 index)
									(.getTitleAt repl-tabbed-panel index)
									nil)))))

(defn add-button-tab [repl]
	(let [text-area (:text-area repl)
		  repl-tabbed-panel (@state/app :repl-tabbed-panel)
		  repl-component (:container repl)
		  project-color (get-in repl [:project :theme :color] (color :white))
		  label (repl-tab-label repl-tabbed-panel repl-component)
		  button (tab-button repl)
		  button-tab (flow-panel :align :right)]
		(config! button-tab :items[label button])
		(config! label :foreground (color :white) :focusable? false
									 :id (symbol (str "[:tab-label-" (count @(get-in repl [:project :repls])) "]")))
		(doto button-tab
			(.setOpaque false)
			(.setBorder (javax.swing.BorderFactory/createEmptyBorder 0 0 0 0))
			(.setMinimumSize (Dimension. 300 20)))
		{:button button}))





