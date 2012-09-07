(ns sketchpad.repl.app.component
	(:require [seesaw.core :as seesaw]
            [seesaw.border :as border]
            [seesaw.meta :as meta]
            [sketchpad.rsyntax :as rsyntax]
            [sketchpad.state.state :as state]
		        [sketchpad.repl.tab :as repl.tab]
            [sketchpad.config.config :as config.config]
            [sketchpad.buffer.action :as buffer.action]
            [sketchpad.repl.app.sketchpad-repl :as app.sketchpad-repl])
  (:import (java.util UUID)
            (java.util.concurrent LinkedBlockingDeque)
           (org.fife.ui.rtextarea RTextScrollPane)))

(defn- sketchpad-reader [q prompt exit]
  (read-string (.take q)))

(defn- sketchpad-prompt [rsta]
  (let [prompt-ns (ns-name *ns*)]
    (seesaw/invoke-later
      (buffer.action/append-text rsta (str \newline prompt-ns "=> ")))))

(defn- sketchpad-printer [rsta value]
  (seesaw/invoke-later
    (buffer.action/append-text rsta (str value))))

(defn- create-application-repl [repl-rsta]
  (let [application-repl-q (LinkedBlockingDeque. )
        reader (partial sketchpad-reader application-repl-q)
        printer (partial sketchpad-printer repl-rsta)
        prompt (partial sketchpad-prompt repl-rsta)]
    (future 
      (app.sketchpad-repl/sketchpad-repl repl-rsta
        :read reader
        :print printer
        :prompt prompt))
    application-repl-q))

(defn application-repl-component []
	(let [text-area (rsyntax/text-area :syntax :clojure    
                                     :border nil
                                     :id :editor
                                     :class :repl)
				repl-title (atom "SketchPad")
				repl-history {:items (atom nil) :pos (atom 0) :last-end-pos (atom 0)}
				repl-in-scroll-pane (RTextScrollPane. text-area false) ;; default to no linenumbers
				repl-container (seesaw/vertical-panel :items [repl-in-scroll-pane]                                      
												                      :id :repl-container
												                      :class :repl)
				repl-undo-count (atom 0)
				repl-que (create-application-repl text-area)
				tab (repl.tab/label-tab {:container repl-container
				                  :title repl-title})
				uuid (.. UUID randomUUID toString)
				application-repl {:type :application-repl
                  				:component {:container repl-container :text-area text-area :scroller repl-in-scroll-pane}
                  				:title repl-title
                  				:history repl-history
                          :uuid uuid
                          :project ".sketchpad-tmp"
                          :auto-complete (atom nil)
                  				:que repl-que
                  				:tab tab}]
    (meta/put-meta! text-area :repl-history repl-history)
    (meta/put-meta! text-area :repl-que repl-que)
    (meta/put-meta! text-area :uuid uuid)
    (swap! state/app assoc :application-repl application-repl)
    application-repl))