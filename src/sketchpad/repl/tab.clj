(ns sketchpad.repl.tab
	(:use [seesaw core color border graphics meta]
				[sketchpad option-windows])
	(:import (javax.swing JPanel JLabel BorderFactory AbstractButton JButton)
					 (javax.swing.plaf.basic.BasicButtonUI)
					 (java.awt.event ActionListener MouseListener)
					 (java.awt.BasicStoke)
					 (java.awt Color Dimension Graphics2D FlowLayout))
	(:require [sketchpad.state :as state]
				[sketchpad.styles :as styles]
				[sketchpad.project.project :as sketchpad.project]
				[seesaw.bind :as bind]))	

(defn paint-tab-button 
"custom renderer for tab x"
[repl c g]
(println)
(println)
(println "paint-tab-button")
(println repl)
  (let [w          (width c)
        h          (height c)
        line-style (style :foreground styles/base-color :stroke 2 :cap :round)
        border-style (style :foreground (color :white) :stroke 0.5)
        d 3
        lp 7]
    (draw g
      (line lp lp (- w lp) (- h lp)) line-style
      (line lp (- h lp) (- w lp) lp) line-style)
	(draw g
      (rounded-rect d d (- w d d) (- h d d) 5 5) border-style)))

(defn tab-button [repl]
	(println)
	(println)
	(println "tab-button")
	(println repl)
	(let [project-color (sketchpad.project/buffer-color repl)
			_ (println project-color)
		  btn (button :focusable? false
		  			  :tip "close this tab"
		  			  :size [20 :by 20]
		  			  :minimum-size [20 :by 20]
		  			  :id :close-button
		  			  :paint (partial paint-tab-button repl))]
		(doto btn
			(.setBorderPainted false)
			(.setRolloverEnabled false)
			(.setFocusable false))
		btn))

; (defn repl-tab-label [repl-tabbed-panel repl-component]
; 	(proxy [JLabel] []
; 						(getText []
; 							(let [index (.indexOfTabComponent repl-tabbed-panel repl-component)]
; 								(if (= (.getSelectedIndex repl-tabbed-panel) index)
; 									(config! this :foreground :white)
; 									(config! this :foreground (color 155 155 155)))
; 								(if (not= -1 index)
; 									(.getTitleAt repl-tabbed-panel index)
; 									nil)))))

(defn add-button-tab [repl]
	(let [text-area (:text-area repl)
		  repl-tabbed-panel (@state/app :repl-tabbed-panel)
		  repl-component (:container repl)
		  label (label :text @(:title repl) :foreground (color :white))
		  button (tab-button repl)
		  container (flow-panel :align :right)]
	    (config! container :items[label button] :class :button-tab)
	    (config! label :foreground (color :white) :focusable? false)
		(bind/bind (:title repl) (bind/transform (fn [s] s)) (bind/property label :text))
		(doto container
			(.setOpaque false)
			(.setBorder (javax.swing.BorderFactory/createEmptyBorder 0 0 0 0))
			(.setMinimumSize (Dimension. 300 20)))
		{:button button}))





