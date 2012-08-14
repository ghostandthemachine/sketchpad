(ns sketchpad.editor.component
  (:use [seesaw core border meta color graphics])
  (:import (java.awt Dimension))
  (:require [sketchpad.rsyntax :as rsyntax]
            [sketchpad.wrapper.rtextscrollpane :as sp]
            [sketchpad.wrapper.rsyntaxtextarea :as wrapper.rsyntaxtextarea]
            [sketchpad.config.config :as config]
            [sketchpad.input.default :as input.default]))

(defn buffer-component
  []
  (let [text-area         	(rsyntax/text-area :syntax   :clojure
                                               :id       :editor
                                               :class    [:editor-comp :syntax-editor])
      doc-scroll-pane       	(sp/scroll-pane text-area)
        doc-scroller-container  (vertical-panel :border nil
                                                :items [doc-scroll-pane] :class :rsta-scroller)
        doc-scroller-gutter     (.getGutter doc-scroll-pane)
        doc-container  			    (vertical-panel :border nil
                                              :items [doc-scroller-container] :class :container)]
    (wrapper.rsyntaxtextarea/set-input-map! text-area (input.default/default-input-map))

    (config/apply-editor-prefs! text-area)
    (config/apply-buffer-scroller-prefs! doc-scroll-pane)
    (config/apply-gutter-prefs! (.getGutter doc-scroll-pane))

    (.setPreferredSize (.getVerticalScrollBar doc-scroll-pane) (Dimension. 0 0))
    (.setPreferredSize (.getHorizontalScrollBar doc-scroll-pane) (Dimension. 0 0))

    {:type :buffer-component
     :text-area text-area
     :container doc-container
     :title (atom "untitled")
     :state (atom {:clean true :new false :active nil :index nil})
     :scroller doc-scroll-pane}))