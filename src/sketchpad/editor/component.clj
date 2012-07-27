(ns sketchpad.editor.component
  (:use [seesaw core border meta color graphics]
        [sketchpad auto-complete rsyntaxtextarea default-mode scroll-bar-ui scroll-pane-ui])
  (:import (java.awt Adjustable Font)
           (javax.swing JScrollBar UIManager)
           (javax.swing JLayeredPane))
  (:require [sketchpad.rsyntax :as rsyntax]
            [sketchpad.rtextscrollpane :as sp]
            [sketchpad.config :as config]
            [sketchpad.editor.info :as editor.info]
            [sketchpad.editor.info-utils :as editor.info-utils]))

(defn buffer-component
  []
  (let [text-area         	(rsyntax/text-area    ;:border nil
                                                  :syntax         :clojure
                                                  :id             :editor
                                                  :class          [:editor-comp :syntax-editor])
        doc-scroll-pane       	(sp/scroll-pane text-area)
        doc-scroller-container  (vertical-panel :border nil
                                                :items [doc-scroll-pane] :class :rsta-scroller)
        doc-scroller-gutter     (.getGutter doc-scroll-pane)
        doc-container  			    (vertical-panel :border nil
                                              :items [doc-scroller-container] :class :container)]
    (set-input-map! text-area (default-input-map))

    (config/apply-editor-prefs! config/default-editor-prefs text-area)
    (config/apply-buffer-scroller-prefs! config/default-buffer-scroller-prefs doc-scroll-pane)
    (config/apply-gutter-prefs! config/default-gutter-prefs (.getGutter doc-scroll-pane))
    (install-auto-completion text-area)

    {:type :buffer-component
     :text-area text-area
     :container doc-container
     :title (atom "untitled")
     :state (atom {:clean true :new false :active nil :index nil})
     :scroller doc-scroll-pane}))


