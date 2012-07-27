(ns sketchpad.editor.tab
  (:use [seesaw core color border graphics meta]
        [sketchpad option-windows]
        [clojure.pprint])
  (:import (javax.swing JPanel JLabel BorderFactory AbstractButton JButton)
           (javax.swing.plaf.basic.BasicButtonUI)
           (java.awt.event ActionListener MouseListener)
           (java.awt.BasicStoke)
           (java.awt Color Dimension Graphics2D FlowLayout))
  (:require [sketchpad.state :as state]
            [sketchpad.sketchpad-prefs :as sketchpad.sketchpad-prefs]))

(defn text-area-from-index [tabbed-panel i]
  (select (.getComponentAt tabbed-panel i) [:#editor]))

(def button-base-color (color 150 150 150))

(defn paint-tab-button [buffer-component c g]
    (let [
          clean? (@(:state buffer-component) :clean)
          w          (width c)
          h          (height c)
          line-style (style :foreground button-base-color :stroke 2 :cap :round)
          project-color (if (:project buffer-component) (get-in buffer-component [:project :theme :color]) (color :white))
          border-style (style :foreground project-color :stroke 0.5)
          ellipse-style (style :foreground button-base-color :background button-base-color :stroke 1 :cap :round)
          d 3
          lp 7]
      (cond
        clean?
        (do
          (draw g
                (line lp lp (- w lp) (- h lp)) line-style
                (line lp (- h lp) (- w lp) lp) line-style
                (rounded-rect d d (- w d d) (- h d d) 5 5) border-style))
        (not clean?)
        (do
          (draw g
                (ellipse lp lp (- w lp lp) (- h lp lp)) ellipse-style
                (rounded-rect d d (- w d d) (- h d d) 5 5) border-style)))))

(defn tab-button 
([buffer-component]
  (let [button (button :focusable? false
                    :tip "close this tab"
                    :minimum-size [20 :by 20]
                    :size [20 :by 20]
                    :id :close-button
                    :paint (partial paint-tab-button buffer-component))]
    (doto button
      (.setBorderPainted false)
      (.setRolloverEnabled false)
      (.setFocusable false))
    button)))

(defn button-tab 
[buffer-component]
  (let [container (flow-panel :align :right)
        button (tab-button buffer-component)
        label (label "untitled")]
    (config! container :items[label button] :class :button-tab)
    (config! label :foreground (color :white) :focusable? false)
    (when-not  @sketchpad.sketchpad-prefs/show-tabs?
      (config! container :visible? false))
    ; (bind/bind (:title repl) (bind/transform (fn [s] s)) (bind/property label :text))
    (doto container
      (.setOpaque false)
      (.setBorder (javax.swing.BorderFactory/createEmptyBorder 0 0 0 0))
      (.setMinimumSize (Dimension. 300 20)))
    {:type :tab
     :container container
     :button button
     :label label
     :label-color (atom (color :white))}))
