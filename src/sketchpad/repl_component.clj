(ns sketchpad.repl-component
  (:use [seesaw core border meta color]
        [sketchpad buffer-edit repl auto-complete rsyntaxtextarea default-mode]
        [sketchpad.utils :only (attach-child-action-keys attach-action-keys
                                                         awt-event get-file-ns
                                                         when-lets get-text-str get-directories)])
  (:require [sketchpad.rsyntax :as rsyntax]
            [sketchpad.rtextscrollpane :as sp]
            [sketchpad.config :as config]
            [sketchpad.lein-manager :as lein]
            [sketchpad.tab :as tab]
            [sketchpad.state :as state]
            [clojure.string :as string])
  (:import (org.fife.ui.rtextarea RTextScrollPane)
           (java.io BufferedReader BufferedWriter PipedReader PipedWriter PrintWriter Writer
                    StringReader PushbackReader)))

(defn- init-prompt [rta]
  (append-text rta "user=> "))

(defn init-repl-component 
"Init the repl component prefs and handlers."
[rsta repl-history]
  ; (put-meta! rsta :repl-server repl-server)
  (put-meta! rsta :repl-history repl-history)
  (config/apply-editor-prefs! config/default-editor-prefs rsta)
  (set-input-map! rsta (default-input-map))
  (install-auto-completion rsta)  
  (init-prompt rsta))

(defn make-repl-component
([project]
	(let [state (atom {:tab-index -1
										 :parent-tab-index -1
										})
				rsta (rsyntax/text-area :border nil
																					:syntax :clojure
																					:id 		:editor
    																				:class 	:repl)
        repl-history {:items (atom nil) :pos (atom 0) :last-end-pos (atom 0)}
    		repl-scroll-pane (RTextScrollPane. rsta false)
        repl-container (vertical-panel :items [repl-scroll-pane] :class :repl-container)]
  (config! repl-scroll-pane :background config/app-color)
  (init-repl-component rsta repl-history)
  repl-container)))

(defn init-repl 
"Init the repl component prefs and handlers."
[rsta]
  (config/apply-editor-prefs! config/default-editor-prefs rsta)
  (set-input-map! rsta (default-input-map))
  (install-auto-completion rsta)  
  (init-prompt rsta))

(defn repl-panel 
([]
  (let [rsta (rsyntax/text-area :border nil
                                :syntax :clojure
                                :id     :editor
                                :class  :repl)
        repl-scroll-pane (RTextScrollPane. rsta false)
        repl-container (vertical-panel :items [repl-scroll-pane] :class :repl-container)]
    (init-repl rsta)
    repl-container)))

; (defn make-repl-component
; ([project]
;   (let [state (atom {:tab-index -1
;                      :parent-tab-index -1
;                     })
;         rsta (rsyntax/text-area :border nil
;                                           :syntax :clojure
;                                           :id     :editor
;                                           :class  :repl)
;       repl-history {:items (atom nil) :pos (atom 0) :last-end-pos (atom 0)}
;       repl-scroll-pane (RTextScrollPane. rsta false)
;       repl-server (lein/project-repl-server project)
;       repl-container (vertical-panel :items [repl-scroll-pane] :class :repl-container)]
  
;   (put-meta! rsta :state state)
;   (put-meta! rsta :repl-server repl-server)
;   (put-meta! rsta :repl-history repl-history)
;   (put-meta! rsta :project project)

;   (config! repl-scroll-pane :background config/app-color)
;   (config/apply-editor-prefs! config/default-editor-prefs rsta)
  
;   (set-input-map! rsta (default-input-map))
  
;   (install-auto-completion rsta)
  
;   (attach-lein-repl-handler rsta)
  
;   (init-prompt rsta)
  
;   repl-container)))
