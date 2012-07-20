(ns sketchpad.menu.view
  (:use [seesaw meta])
	(:require [seesaw.core :as seesaw.core]
		[seesaw.keystroke :as keystroke]
		[sketchpad.layout-config :as layout-config]
		[sketchpad.tab :as tab]
    [sketchpad.app :as app]
    [sketchpad.file :as file]
    [sketchpad.buffer-new :as buffer-new]
    [sketchpad.option-windows :as option-windows]))

(defonce edit-menu-item-state 
  { :focus-repl (atom true)
    :focus-editor (atom true)
    :focus-file-tree (atom true)
    :show-repl (atom true)
    :show-file-tree (atom true)
    :next-tab (atom false)
    :previous-tab (atom false)
    :close-tab (atom false)
    })

(defn focus-repl
"Focus the REPL tabbed panel. This will focus the current REPL tab and ready it for text input."
[]
	(app/focus-repl))

(defn focus-editor
"Focus the editor tabbed panel. This will focus the current editor tab and ready it for text input."
[]
	(app/focus-editor))

(defn focus-file-tree
"Focus the file tree tabbed panel. This will focus the current file tree tab and ready it for text input."
[]
	(app/focus-file-tree))

(defn show-file-tree
"Toggle displaying the file tree component."
[]
	(layout-config/toggle-file-tree-panel))

(defn show-repl
"Toggle displaying the repl component."
[]
	(layout-config/toggle-repl))

(defn next-tab
"Display the next available tab in the editor tabbed panel."
  []
	(tab/next-tab))

(defn previous-tab
"Display the previous available tab in the editor tabbed panel."
  []
	(tab/previous-tab))

(defn close-tab
"Close the current tab. If the current buffer is dirty this will ask if you are sure you want to close the tab."
[]
(if (tab/tabs?)
      (let [buffer (app/buffer)
            current-tab-state (get-meta buffer :state)]
        (if (@current-tab-state :clean)
          (tab/close-tab) ;; nothing has changed, just close.
          (do 
            (let [answer (option-windows/close-or-save-current-dialogue (app/buffer-title))]
              (cond 
                (= answer 0) ;; answered yes to save
                  (do
                    (if (get-meta buffer :new-file) ;; is it a new buffer?
                      (do 
                        (buffer-new/save-new-buffer! buffer)
                        (tab/close-tab))
                      (do
                        (buffer-new/save-buffer! buffer)
                        (tab/close-tab))))
                (= answer 1) ;; don't save just close
                  (do 
                    (tab/close-tab)))))))))

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
                (menu-items :close-tab)])))






