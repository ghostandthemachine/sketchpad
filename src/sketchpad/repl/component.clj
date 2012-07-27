(ns sketchpad.repl.component
  (:use [seesaw core border meta color]
        [sketchpad buffer-edit repl auto-complete rsyntaxtextarea default-mode])
  (:require [sketchpad.rsyntax :as rsyntax]
            [sketchpad.config :as config]
            [sketchpad.repl-button-tab :as button-tab]
            [clojure.string :as string])
  (:import (org.fife.ui.rtextarea RTextScrollPane)
           (java.io BufferedReader BufferedWriter PipedReader PipedWriter PrintWriter Writer
                    StringReader PushbackReader)))

(defn- init-prompt [rta]
  (append-text rta "user=> "))

(defn init-repl 
"Init the repl component prefs and handlers."
[text-area]
  (config/apply-editor-prefs! config/default-editor-prefs text-area)
  (set-input-map! text-area (default-input-map))
  (install-auto-completion text-area)  
  (init-prompt text-area))

(defn repl-component 
([]
  (let [text-area (rsyntax/text-area :border nil
                                :syntax :clojure
                                :id     :editor
                                :class  :repl)
        repl-scroll-pane (RTextScrollPane. text-area false)
        repl-container (vertical-panel :items [repl-scroll-pane] :class :repl-container)]
    (init-repl text-area)
    {:container repl-container
     :text-area text-area
     :title (atom "nREPL")})))