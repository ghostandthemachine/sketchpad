(ns sketchpad.menu.view
  (:use [seesaw meta])
	(:require [seesaw.core :as seesaw.core]
		[seesaw.keystroke :as keystroke]
		[sketchpad.config.layout :as config.layout]
		[sketchpad.util.tab :as tab]
    [sketchpad.config.app :as app]
    [sketchpad.state.state :as state]
    [sketchpad.file.file :as file]
    [sketchpad.buffer.io :as buffer.io]
    [sketchpad.project.project :as sketchpad.project]
    [sketchpad.util.option-windows :as option-windows]))

(defn focus-repl
"Focus the REPL tabbed panel. This will focus the current REPL tab and ready it for text input."
[]
	(tab/focus-repl))

(defn focus-editor
"Focus the editor tabbed panel. This will focus the current editor tab and ready it for text input."
[]
	(tab/focus-editor-text-area))

(defn focus-file-tree
"Focus the file tree tabbed panel. This will focus the current file tree tab and ready it for text input."
[]
	(app/focus-file-tree))

(defn show-file-tree
"Toggle displaying the file tree component."
[]
	(config.layout/toggle-file-tree-panel))

(defn show-repl
"Toggle displaying the repl component."
[]
	(config.layout/toggle-repl))

(defn next-tab
"Display the next available tab in the editor tabbed panel."
  []
	(tab/next-tab)
  (tab/update-tree-selection-from-tab))

(defn previous-tab
"Display the previous available tab in the editor tabbed panel."
  []
	(tab/previous-tab)
  (tab/update-tree-selection-from-tab))

(defn close-tab [app-atom]
"Close the current tab."
	(tab/close-current-tab @app-atom))

(defn next-repl
"Display the next available tab in the editor tabbed panel."
  []
  (tab/next-tab (get-in (@state/app :repl-tabbed-panel) [:component :container])))

(defn previous-repl
"Display the previous available tab in the editor tabbed panel."
  []
  (tab/previous-tab (get-in (@state/app :repl-tabbed-panel) [:component :container])))

(defn close-tab
"Close the current tab. If the current buffer is dirty this will ask if you are sure you want to close the tab."
([] (close-tab (tab/current-buffer)))
([buffer]
(when (tab/tabs?)
      (let [current-tab-state (:state buffer)]
        (if (@current-tab-state :clean)
          (tab/close-tab) ;; nothing has changed, just close.
          (do 
            (let [answer (option-windows/close-or-save-current-dialogue @(:title buffer))]
              (cond 
                (= answer 0) ;; answered yes to save
                  (do
                    (if @(:new-file? buffer) ;; is it a new buffer?
                      (do 
                        (buffer.io/save-new-buffer! buffer)
                        (tab/close-tab)
                        (sketchpad.project/remove-buffer-from-app buffer)
                        (sketchpad.project/remove-buffer-from-project buffer)

                      (do
                        (buffer.io/save-buffer! buffer)
                        (tab/close-tab)
                        (sketchpad.project/remove-buffer-from-app buffer)
                        (sketchpad.project/remove-buffer-from-project buffer)))))
                (= answer 1) ;; don't save just close
                  (do 
                    (tab/close-tab)
                    (sketchpad.project/remove-buffer-from-app buffer)))))))
                    (tab/save-tab-selections))))

(defn make-view-menu-items []
	{:goto-repl 		(seesaw.core/menu-item 	:text "Go to REPL input" 
                           						:mnemonic "R" 
                           						:key (keystroke/keystroke "meta alt 3") 
                           						:listen [:action (fn [_] (focus-repl))])
     :goto-editor  		(seesaw.core/menu-item 	:text "Go to Editor" 
                           						:mnemonic "E" 
                           						:key (keystroke/keystroke "meta alt 2") 
                           						:listen [:action (fn [_] (focus-editor))])
     :goto-file-tree   	(seesaw.core/menu-item 	:text "Go to Project Tree" 
                           						:mnemonic "P" 
                           						:key (keystroke/keystroke "meta alt 1") 
                           						:listen [:action (fn [_] (focus-file-tree))])
     :show-file-tree 	(seesaw.core/menu-item 	:text "Show File Tree" 
                           						:key (keystroke/keystroke "meta 1") 
                           						:listen [:action (fn [_] (show-file-tree))])
     :show-repl        	(seesaw.core/menu-item 	:text "Show REPL" 
                           						:key (keystroke/keystroke "meta 2") 
                           						:listen [:action (fn [_] (show-repl))])
     :next-tab 			(seesaw.core/menu-item 	:text "Next tab"
		                					 	:key (keystroke/keystroke "meta alt RIGHT")
		                					 	:listen [:action (fn [_] (next-tab))])
     :previous-tab    	(seesaw.core/menu-item 	:text "Previous tab"
                					 			:key (keystroke/keystroke "meta alt LEFT")
                					 			:listen [:action (fn [_] (previous-tab))])
     :next-repl      (seesaw.core/menu-item  :text "Next REPL tab"
                                :key (keystroke/keystroke "meta alt DOWN")
                                :listen [:action (fn [_] (next-repl))])
     :previous-repl      (seesaw.core/menu-item  :text "Previous REPL tab"
                                :key (keystroke/keystroke "meta alt UP")
                                :listen [:action (fn [_] (previous-repl))])
     :close-tab 		(seesaw.core/menu-item 	:text "Close tab"
                           						:key (keystroke/keystroke "meta W")
                           						:listen [:action (fn [_] (close-tab))])})

; (def make-view-menu-items
;   [[:goto-repl "Go to REPL input" "meta alt 3" focus-repl]
;    [:goto-editor "Go to Editor" "meta alt 2" focus-editor]
;    [:goto-file-tree "Go to File Tree" "meta alt 1"]
;    [:show-file-tree "Show File Tree" "meta 1"]
;    [:show-repl "Show REPL component" "meta 2"]
;    [:next-tab "Show next tab" "meta alt RIGHT" next-tab]
;    [:previous-tab "Show next tab" "meta alt LEFT" previous-tab]
;    [:close-tab "Close the current tab" "meta W" close-tab]])

(defn make-view-menu []
"Make view menu"
  (let [menu-items (make-view-menu-items)]
  		(seesaw.core/menu :text "View"
        :mnemonic "V"
        :items [(menu-items :goto-repl)
        		    (menu-items :goto-editor)
        		    (menu-items :goto-file-tree)
                (seesaw.core/separator)
        		    (menu-items :show-repl)
        		    (menu-items :show-file-tree)
                (seesaw.core/separator)
        		    (menu-items :next-tab)
        		    (menu-items :previous-tab)
                (seesaw.core/separator)
                (menu-items :next-repl)
                (menu-items :previous-repl)
                (seesaw.core/separator)
                (menu-items :close-tab)])))






