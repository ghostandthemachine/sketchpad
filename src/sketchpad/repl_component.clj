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
            [clojure.string :as string])
  (:import (org.fife.ui.rtextarea RTextScrollPane)
           (java.io BufferedReader BufferedWriter PipedReader PipedWriter PrintWriter Writer
                    StringReader PushbackReader)))

(defn- init-prompt [rta]
  (append-text rta "user=> "))

(defn make-repl-component
([app] 
(let [current-rta (tab/current-text-area (app :editor-tabbed-panel))
      current-project-path (get-meta current-rta :project-path)
      lein-project (lein/get-lein-project current-project-path)]
  (make-repl-component app current-project-path lein-project)))
([app path project]
	(let [state (atom {:tab-index -1
										 :parent-tab-index -1
										})
				rsta (rsyntax/text-area :border nil
																					:syntax :clojure
																					:id 		:editor
																					:class 	:repl)
      repl-history {:items (atom nil) :pos (atom 0) :last-end-pos (atom 0)}
				repl-scroll-pane (RTextScrollPane. rsta false)
			repl-server (lein/project-repl-server project)
      repl-container (vertical-panel :items [repl-scroll-pane] :class :repl-container)]
  
  (put-meta! rsta :state state)
  (put-meta! rsta :repl-server repl-server)
	(put-meta! rsta :repl-history repl-history)
  (put-meta! rsta :project-path path)

  (config! repl-scroll-pane :background config/app-color)
  (config/apply-editor-prefs! config/default-editor-prefs rsta)
  
  (set-input-map! rsta (default-input-map))
  
  (install-auto-completion rsta)
  
  (attach-lein-repl-handler rsta)
  
  (init-prompt rsta)
  
  repl-container)))
