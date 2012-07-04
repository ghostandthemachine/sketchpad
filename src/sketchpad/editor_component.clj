(ns sketchpad.editor-component
    (:use [seesaw core border meta color graphics]
          [sketchpad editor-info auto-complete rsyntaxtextarea default-mode scroll-bar-ui scroll-pane-ui])
    (:import (java.awt Adjustable Font)
      (javax.swing JScrollBar UIManager)
      (javax.swing JLayeredPane))
    (:require [sketchpad.rsyntax :as rsyntax]
              [sketchpad.rtextscrollpane :as sp]
              [sketchpad.config :as config]
              ))

(def bg [39 40 34])

(defn sketchpad-scroll-bar []
  (let [scroll-bar (JScrollBar. )]
    (.setUI scroll-bar (sketchpad-scroll-bar-ui))
    scroll-bar))

(defn make-editor-component [app-atom]
	(let [state (atom {:clean true
                     :new false
                     :active nil
                     :index nil})
        doc-text-area         	(rsyntax/text-area    ;:border nil
                                                      :syntax         :clojure
                                                      :id             :editor
											                                :class          [:editor-comp :syntax-editor])
        doc-scroll-pane       	(sp/scroll-pane doc-text-area)
        doc-scroller-container  (vertical-panel :border nil
                                                :items [doc-scroll-pane] :class :rsta-scroller)
        doc-scroller-gutter     (.getGutter doc-scroll-pane)
        doc-container  			    (vertical-panel :border nil
                                                :items [doc-scroller-container] :class :container)
        undo-count (atom 0)]
    (put-meta! doc-text-area :state state )
    (put-meta! doc-text-area :undo-count undo-count)
    (config/apply-editor-prefs! config/default-editor-prefs doc-text-area)
    (install-auto-completion doc-text-area)
    (.setBorderColor doc-scroller-gutter (color 0 0 0 0))
    (.setBorder doc-scroll-pane (empty-border :thickness 0))

    (.setFont doc-text-area (Font. "Menlo" Font/BOLD 14))
    ;; attach caret listener to editor info component
    (attach-caret-handler doc-text-area app-atom)
        ;; set default input map
    (set-input-map! doc-text-area (default-input-map))
    doc-container))

(defn doc-canvas-renderer [c g]
	(let [w (width c)
				h (height c)
				rw (/ w 4)
				rh (/ h 4)
				rx (- (/ w 2) (/ rw 2))
				ry (- (/ h 2) (/ rh 2))]
		(rotate g (/ (System/currentTimeMillis) 1000))
		(draw g 
			(rect rx ry rw rh)
			(style :background (color :red))))
)
	

(defn make-layered-editor-component [app-atom]
	(let [state (atom {:clean true
                     :new false
                     :active nil
                     :index nil})
        doc-canvas (canvas :paint doc-canvas-renderer)
        doc-text-area         	(rsyntax/text-area    ;:border nil
                                                      :syntax         :clojure
                                                      :id             :editor
											                                :class          [:editor-comp :syntax-editor])
        doc-scroll-pane       	(sp/scroll-pane doc-text-area)
        doc-layered-pane (JLayeredPane. )
        doc-scroller-container  (vertical-panel :border nil
                                                :items [doc-layered-pane] :class :rsta-scroller)
        doc-scroller-gutter     (.getGutter doc-scroll-pane)

        doc-container  			    (vertical-panel :border nil
                                                :items [doc-scroller-container] :class :container)
        undo-count (atom 0)]

		(.setLayer doc-layered-pane doc-canvas 0)
		(.setLayer doc-layered-pane doc-scroll-pane 1)
    
    (put-meta! doc-text-area :state state )
    (put-meta! doc-text-area :undo-count undo-count)

    (put-meta! doc-container :editor doc-text-area)

    (config/apply-editor-prefs! config/default-editor-prefs doc-text-area)
    (install-auto-completion doc-text-area)
    (.setBorderColor doc-scroller-gutter (color 0 0 0 0))
    (.setBorder doc-scroll-pane (empty-border :thickness 0))

    (.setFont doc-text-area (Font. "Menlo" Font/BOLD 14))
    ;; attach caret listener to editor info component
    (attach-caret-handler doc-text-area app-atom)
        ;; set default input map
    (set-input-map! doc-text-area (default-input-map))
    doc-container))