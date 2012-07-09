 (ns sketchpad.repl-component
   (:use [seesaw core border meta color]
       [sketchpad buffer-edit repl auto-complete rsyntaxtextarea default-mode]
       [sketchpad.utils :only (attach-child-action-keys attach-action-keys
                            awt-event get-file-ns
                            when-lets get-text-str get-directories)]
       )
   (:require [sketchpad.rsyntax :as rsyntax]
             [sketchpad.rtextscrollpane :as sp]
             [sketchpad.config :as config]
             [clojure.string :as string])
   (:import (org.fife.ui.rtextarea RTextScrollPane)
            (java.io BufferedReader BufferedWriter PipedReader PipedWriter PrintWriter Writer
                            StringReader PushbackReader)))

(defn make-repl-writer [ta-out]
  (->
    (let [buf (agent (StringBuffer.))]
      (proxy [Writer] []
        (write
          ([char-array offset length]
            (awt-event 
              (append-text ta-out (apply str char-array))
              ))
          ([^Integer t]
            (awt-event (append-text ta-out (str (char t))))))
        (flush [] )
        (close [] nil)))
    (PrintWriter. true)))

 (defn make-repl-component [app project-path]
 	(let [state (atom {:tab-index -1
 										 :parent-tab-index -1
 										})
 				rsta (rsyntax/text-area :border nil
 																					:syntax :clojure
 																					:id 		:editor
 																					:class 	:repl)
        repl-history {:items (atom nil) :pos (atom 0) :last-end-pos (atom 0)}
 				repl-scroll-pane (RTextScrollPane. rsta false)
        repl-writer (make-repl-writer rsta)
        repl (create-outside-repl repl-writer project-path)
        repl-container (vertical-panel :items [repl-scroll-pane] :class :repl-container)]
    (put-meta! rsta :state state)
    (put-meta! rsta :repl repl)
 		(put-meta! rsta :repl-history repl-history)
    (put-meta! rsta :project-path project-path)
    ;; apply config prefs
    (config! repl-scroll-pane :background config/app-color)
    (config/apply-editor-prefs! config/default-editor-prefs rsta)
    ;; input map
    (set-input-map! rsta (default-input-map))
    ;; auto completion
    (install-auto-completion rsta)

    repl-container
 		))


