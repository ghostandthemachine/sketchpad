(ns sketchpad.editor.tab
  (:use [seesaw core color border graphics meta]
        [sketchpad.util.option-windows]
        [clojure.pprint])
  (:import (javax.swing JPanel JLabel BorderFactory AbstractButton JButton)
           (javax.swing.plaf.basic.BasicButtonUI)
           (java.awt.event ActionListener MouseListener)
           (java.awt.BasicStoke)
           (java.awt Color Dimension Graphics2D FlowLayout))
  (:require [seesaw.bind :as bind]
            [sketchpad.state.state :as state]
            [seesaw.font :as font]
            [sketchpad.project.project :as project]
            [sketchpad.config.prefs :as sketchpad.config.prefs]))

(defn text-area-from-index [tabbed-panel i]
  (select (.getComponentAt tabbed-panel i) [:#editor]))

(defn paint-tab-button [buffer c g]
    (let [clean? (@(:state buffer) :clean)
          w          (width c)
          h          (height c)
          button-base-color (color 170 170 170)
          line-style (style :foreground button-base-color :stroke 2 :cap :round)
          project-color (project/buffer-color buffer)
          background-style (style :background (color 39 40 34))
          border-style (if project-color 
          					(style :foreground @project-color :stroke 0.5)
          					(style :foreground (color 255 255 255) :stroke 0.5))
          ellipse-style (style :foreground button-base-color :background button-base-color :stroke 1 :cap :round)
          d 3
          lp 7]
      (when @sketchpad.config.prefs/show-tabs?
        (cond
          clean?
          (do
            (draw g
                  (rounded-rect d d (- w d d) (- h d d) 5 5) background-style
                  (rounded-rect d d (- w d d) (- h d d) 5 5) border-style
                  (line lp lp (- w lp) (- h lp)) line-style
                  (line lp (- h lp) (- w lp) lp) line-style))
          (not clean?)
          (do
            (draw g
                  (rounded-rect d d (- w d d) (- h d d) 5 5) background-style
                  (rounded-rect d d (- w d d) (- h d d) 5 5) border-style
                  (ellipse lp lp (- w lp lp) (- h lp lp)) ellipse-style))))))

(defn tab-button 
([buffer]
  (let [button (button :focusable? false
                    :tip "close this tab"
                    :minimum-size [20 :by 20]
                    :size [20 :by 20]
                    :id :close-button
                    :paint (partial paint-tab-button buffer))]
    (doto button
      (.setBorderPainted false)
      (.setRolloverEnabled false)
      (.setFocusable false))
    button)))

(defn button-tab 
[buffer]
  (let [button (tab-button buffer)
        label (label :text @(get-in buffer [:component :title])
                      :foreground (color :white)
                      :focusable?  false
                      :class :tab-label
                      :font (font/font "MENLO-12"))
        container (flow-panel :align :right
                              :items [label button]
                              :maximum-size [300 :by 100]
                              :class :button-tab)]
    (bind/bind sketchpad.config.prefs/show-tabs? (bind/transform (fn [s] s)) (bind/property container :visible?))
    (bind/bind sketchpad.config.prefs/show-tabs? (bind/transform (fn [s] s)) (bind/property label :visible?))
    
    (config! container :visible? sketchpad.config.prefs/show-tabs?)
    (config! label :visible? sketchpad.config.prefs/show-tabs?)
    
    (bind/bind (:title buffer) (bind/transform (fn [s] s)) (bind/property label :text))
    (doto container
      (.setOpaque false)
      (.setBorder (javax.swing.BorderFactory/createEmptyBorder 0 0 0 0))
      (.setMinimumSize (Dimension. 300 20)))
    {:type :tab
     :container container
     :button button
     :label label
     :label-color (atom (color :white))}))
