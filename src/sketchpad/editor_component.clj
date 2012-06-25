(ns sketchpad.editor-component
    (:use [seesaw core border meta color]
        [sketchpad.auto-complete]
        [sketchpad.rsyntaxtextarea]
        [sketchpad.default-mode]
        )
    (:require [seesaw.rsyntax :as rsyntax]
              [sketchpad.rtextscrollpane :as sp]
              [sketchpad.config :as config]
              ))

(def bg [39 40 34])

(defn make-editor-component []
	(let [state (atom :clean)
        doc-text-area         	(rsyntax/text-area    :syntax         :clojure
                                                      :id             :editor
											                          :class          [:editor-comp :syntax-editor])
                                                      
        doc-scroll-pane       	(sp/scroll-pane doc-text-area)
        doc-scroller-container  (vertical-panel :items [doc-scroll-pane] :class :rsta-scroller)
        doc-container  			(vertical-panel :items [doc-scroller-container] :class :container)]
    (put-meta! doc-text-area :state state )
    ; (config! doc-scroll-pane :background (apply color bg) :border (empty-border :thickness 0))
    ; (config! doc-scroller-container :background (apply color bg) :border (empty-border :thickness 0))
    ; (config! doc-container :background (apply color bg) :border (empty-border :thickness 0))
    (config/apply-editor-prefs! config/default-editor-prefs doc-text-area)
    (install-auto-completion doc-text-area)
    ;; set default input map
    (set-input-map! doc-text-area (default-input-map))
    doc-container))