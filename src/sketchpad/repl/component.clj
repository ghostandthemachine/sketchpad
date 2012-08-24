(ns sketchpad.repl.component
  (:use [seesaw core border meta color]
        [sketchpad.repl.app.repl]
        [sketchpad.input.default])
  (:require [sketchpad.rsyntax :as rsyntax]
            [sketchpad.config.config :as config]
            [sketchpad.buffer.action :as buffer.action]
            [sketchpad.wrapper.rsyntaxtextarea :as wrapper.rsyntaxtextarea]
            [sketchpad.auto-complete.auto-complete :as auto-complete]
            [clojure.string :as string])
  (:import (java.util UUID) 
  		(org.fife.ui.rtextarea RTextScrollPane)
           	(java.io BufferedReader BufferedWriter PipedReader PipedWriter PrintWriter Writer
                    StringReader PushbackReader)))

(defn- init-prompt [rta]
  (buffer.action/append-text
    rta
"Docs: (doc function-name-here)
       (find-doc \"part-of-name-here\")
Source: (source function-name-here)
Javadoc: (javadoc java-object-or-class-here)
Examples from clojuredocs.org: [clojuredocs or cdoc]\n\n")
  (buffer.action/append-text rta "user=> "))

(defn init-repl 
"Init the repl component prefs and handlers."
[text-area]
  (config/apply-repl-prefs! text-area)
  (wrapper.rsyntaxtextarea/set-input-map! text-area (default-input-map))
  (auto-complete/install-auto-completion text-area)  
  (init-prompt text-area))

(defn repl-component 
([]
  (let [text-area (rsyntax/text-area 
                                :border (line-border :thickness 1 :color config/app-color)                          
                                :syntax :clojure
                                :id     :editor
                                :class  :repl)
        repl-scroll-pane (RTextScrollPane. text-area false)
        repl-container (vertical-panel :items [repl-scroll-pane] :class :repl-container)]
    (seesaw.meta/put-meta! text-area :uuid  (.. UUID randomUUID toString))
    (config! repl-scroll-pane :border nil)
    (init-repl text-area)
    {:container repl-container
     :text-area text-area
     :title (atom "nREPL")})))


