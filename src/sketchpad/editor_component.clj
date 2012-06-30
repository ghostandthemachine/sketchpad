(ns sketchpad.editor-component
    (:use [seesaw core border meta color]
          [sketchpad auto-complete rsyntaxtextarea default-mode scroll-bar-ui scroll-pane-ui])
    (:import (java.awt Adjustable Font)
      (javax.swing JScrollBar UIManager))
    (:require [sketchpad.rsyntax :as rsyntax]
              [sketchpad.rtextscrollpane :as sp]
              [sketchpad.config :as config]
              ))

(def bg [39 40 34])

(defn sketchpad-scroll-bar []
  (let [scroll-bar (JScrollBar. )]
    (.setUI scroll-bar (sketchpad-scroll-bar-ui))
    scroll-bar))

(defn make-editor-component []
	(let [state (atom {:clean true
                     :new false
                     :active nil
                     :index nil})
        doc-text-area         	(rsyntax/text-area    :border nil
                                                      :syntax         :clojure
                                                      :id             :editor
											                          :class          [:editor-comp :syntax-editor])
        doc-scroll-pane       	(sp/scroll-pane doc-text-area)
        doc-scroller-container  (vertical-panel :border nil
                                                :items [doc-scroll-pane] :class :rsta-scroller)
        doc-scroller-gutter     (.getGutter doc-scroll-pane)
        doc-container  			    (vertical-panel :border nil
                                                :items [doc-scroller-container] :class :container)]
    (put-meta! doc-text-area :state state )
    (config/apply-editor-prefs! config/default-editor-prefs doc-text-area)
    (install-auto-completion doc-text-area)
    (.setBorderColor doc-scroller-gutter (color 0 0 0 0))
    (.setBorder doc-scroll-pane (empty-border :thickness 0))

    (.setFont doc-text-area (Font. "Menlo" Font/BOLD 14))

    ;; setup sketchpad scrollbar
    ; (.setVerticalScrollBar doc-scroll-pane (sketchpad-scroll-bar))


    ; (.setUI doc-scroll-pane (sketchpad-scroll-pane-ui))

        ;; set default input map
    (set-input-map! doc-text-area (default-input-map))
    doc-container))