; (ns sketchpad.button-tab
;   (:use [seesaw core color border graphics meta]
;         [sketchpad option-windows]
;         [clojure.pprint])
;   (:import (javax.swing JPanel JLabel BorderFactory AbstractButton JButton)
;            (javax.swing.plaf.basic.BasicButtonUI)
;            (java.awt.event ActionListener MouseListener)
;            (java.awt.BasicStoke)
;            (java.awt Color Dimension Graphics2D FlowLayout))
;   (:require [sketchpad.state :as state]
;             [sketchpad.sketchpad-prefs :as sketchpad.sketchpad-prefs]))

; (defn text-area-from-index [tabbed-panel i]
;   (select (.getComponentAt tabbed-panel i) [:#editor]))

; (def button-base-color (color 150 150 150))

; (defn paint-tab-button [c g]
;   (println "paint-tab-button button: ")
;     (let [
;           ; clean? (@(:state buffer) :clean)
;           w          (width c)
;           h          (height c)
;           line-style (style :foreground button-base-color :stroke 2 :cap :round)
;           ; project-color (if (:project buffer) (get-in buffer [:project :theme :color]) (color :white))
;           border-style (style :foreground (color :white) :stroke 0.5)
;           ellipse-style (style :foreground button-base-color :background button-base-color :stroke 1 :cap :round)
;           d 3
;           lp 7]
;       ; (cond
;       ;   clean?
;       ;   (do
;           (draw g
;                 (line lp lp (- w lp) (- h lp)) line-style
;                 (line lp (- h lp) (- w lp) lp) line-style
;                 (rounded-rect d d (- w d d) (- h d d) 5 5) border-style))
;         ; (not clean?)
;         ; (do
;         ;   (draw g
;         ;         (ellipse lp lp (- w lp lp) (- h lp lp)) ellipse-style
;         ;         (rounded-rect d d (- w d d) (- h d d) 5 5) border-style))
;         )


; (defn tab-button 
; ([buffer]
;   (let [button (button :focusable? false
;                     :tip "close this tab"
;                     :minimum-size [20 :by 20]
;                     :size [20 :by 20]
;                     :id :close-button
;                     :paint paint-tab-button)]
;     (doto button
;       (.setBorderPainted false)
;       (.setRolloverEnabled false)
;       (.setFocusable false))
;     button)))

; (defn tab-label [buffer]
;   (let [tabbed-panel (:editor-tabbed-panel @state/app)
;         container (:container buffer)]
;         (println @(:title buffer))
;     (proxy [JLabel] []
;               (getText []
;                 (let [index (.indexOfTabComponent tabbed-panel container)]
;                   (if (= (.getSelectedIndex tabbed-panel) index)
;                     (config! this :foreground :white)
;                     (config! this :foreground (color 155 155 155)))
;                   (if (not= -1 index)
;                     @(:title buffer)
;                     nil))))))

; (defn button-tab 
; ([buffer]
;   (println "button-tab buffer: " buffer)
;   (let [btn-tab (flow-panel :align :right)
;         btn (tab-button buffer)
;         label (tab-label buffer)]
;     (config! btn-tab :items[label btn] :class :button-tab)
;     (config! label :foreground (color :white) :focusable? false)
;     (when-not  @sketchpad.sketchpad-prefs/show-tabs?
;       (config! btn-tab :visible? false))
;     (doto btn-tab
;       (.setOpaque false)
;       (.setBorder (javax.swing.BorderFactory/createEmptyBorder 0 0 0 0))
;       (.setMinimumSize (Dimension. 300 20))))))
