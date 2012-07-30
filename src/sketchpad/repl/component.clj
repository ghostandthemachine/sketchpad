(ns sketchpad.repl.component
  (:use [seesaw core border meta color]
        [sketchpad repl auto-complete rsyntaxtextarea default-mode])
  (:require [sketchpad.rsyntax :as rsyntax]
            [sketchpad.config :as config]
            [sketchpad.buffer.action :as buffer.action]
            [clojure.string :as string])
  (:import (org.fife.ui.rtextarea RTextScrollPane)
           (java.io BufferedReader BufferedWriter PipedReader PipedWriter PrintWriter Writer
                    StringReader PushbackReader)))

(defn- init-prompt [rta]
  (buffer.action/append-text-update
    rta
"Docs: (doc function-name-here)
       (find-doc \"part-of-name-here\")
Source: (source function-name-here)
Javadoc: (javadoc java-object-or-class-here)
Examples from clojuredocs.org: [clojuredocs or cdoc]\n\n")
  (buffer.action/append-text-update rta "user=> "))

(defn init-repl 
"Init the repl component prefs and handlers."
[text-area]
  (config/apply-repl-prefs! text-area)
  (set-input-map! text-area (default-input-map))
  (install-auto-completion text-area)  
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
    (config! repl-scroll-pane :border (line-border :thickness 1 :color config/app-color))
    (init-repl text-area)
    {:container repl-container
     :text-area text-area
     :title (atom "nREPL")})))


