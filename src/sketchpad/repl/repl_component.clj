; (ns sketchpad.repl-component
;   (:use [seesaw core border meta color]
;       [sketchpad auto-complete rsyntaxtextarea default-mode repl]
;       )
;   (:require [sketchpad.rsyntax :as rsyntax]
;             [sketchpad.rtextscrollpane :as sp]
;             [sketchpad.config :as config]))



; (defn make-repl-component [app project-path]
; 	(let [state (atom {:tab-index -1
; 										 :parent-tab-index -1
; 										})
; 				rsta (rsyntax/text-area :border nil
; 																					:syntax :clojure
; 																					:id 		:repl-editor
; 																					:class 	:repl)
; 				repl-scroll-pane (sp/scroll-pane rsta)
; 				repl-out-writter (make-repl-writer rsta)
; 				repl (create-outside-repl repl-out-writter project-path)
				
; 				repl-panel (app :repl-panel)
; 				]
; 		;;config prefs
;     (config! repl-scroll-pane :background config/app-color)

; 		(put-meta! rsta :state state)
; 		(put-meta! rsta :repl-history {:items (atom nil) :pos (atom 0) :last-end-pos (atom 0)})
; 		;; add file and index to app map
; 		(swap! (app :repls) (fn [repls] (assoc repls rsta repl)))
; 		(install-auto-completion rsta)
;     ;; set default input map
;     (set-input-map! rsta (default-input-map))
; 		))


