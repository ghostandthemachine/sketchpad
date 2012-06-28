(ns sketchpad.editor-component
    (:use [seesaw core border meta color]
        [sketchpad.auto-complete]
        [sketchpad.rsyntaxtextarea]
        [sketchpad.default-mode]
        )
    (:require [sketchpad.rsyntax :as rsyntax]
              [sketchpad.rtextscrollpane :as sp]
              [sketchpad.config :as config]
              ))

(def bg [39 40 34])

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
        docc-scroller-gutter    (.getGutter doc-scroll-pane)
        doc-container  			    (vertical-panel :border nil
                                                :items [doc-scroller-container] :class :container)]
    (put-meta! doc-text-area :state state )
    (config/apply-editor-prefs! config/default-editor-prefs doc-text-area)
    (install-auto-completion doc-text-area)
    (.setBorderColor docc-scroller-gutter (color 0 0 0 0))
    (.setBorder doc-scroll-pane (empty-border :thickness 0))
    ;; set default input map
    (set-input-map! doc-text-area (default-input-map))
    doc-container))