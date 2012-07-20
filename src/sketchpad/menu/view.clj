(ns sketchpad.menu.view
	(:require [seesaw.core :as seesaw.core]
		[seesaw.keystroke :as keystroke]
		[sketchpad.layout-config :as layout-config]
		[sketchpad.tab-manager :as tab-manager]))

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

(defn focus-repl [app-atom]
"Focus the REPL tabbed panel. This will focus the current REPL tab and ready it for text input."
	(.requestFocusInWindow (:repl-tabbed-panel @app-atom)))

(defn focus-editor [app-atom]
"Focus the editor tabbed panel. This will focus the current editor tab and ready it for text input."
	(.requestFocusInWindow (:editor-tabbed-panel @app-atom)))

(defn focus-file-tree [app-atom]
"Focus the file tree tabbed panel. This will focus the current file tree tab and ready it for text input."
	(.requestFocusInWindow (:docs-tree @app-atom)))

(defn show-file-tree [app-atom]
"Toggle displaying the file tree component."
	(layout-config/toggle-file-tree-panel @app-atom))

(defn show-repl [app-atom]
"Toggle displaying the repl component."
	(layout-config/toggle-repl @app-atom))

(defn next-tab [app-atom]
"Display the next available tab in the editor tabbed panel."
	(tab-manager/select-next-tab (@app-atom :editor-tabbed-panel)))

(defn previous-tab [app-atom]
"Display the previous available tab in the editor tabbed panel."
	(tab-manager/select-previous-tab (@app-atom :editor-tabbed-panel)))

(defn close-tab [app-atom]
"Close the current tab."
	(tab-manager/close-current-tab @app-atom))

(defn make-view-menu-items [app-atom]
	{:goto-repl 		(seesaw.core/menu-item 	:text "Go to REPL input" 
                           						:mnemonic "R" 
                           						:key (keystroke/keystroke "meta alt 3") 
                           						:listen [:action (fn [_] (focus-repl app-atom))])
     :goto-editor  		(seesaw.core/menu-item 	:text "Go to Editor" 
                           						:mnemonic "E" 
                           						:key (keystroke/keystroke "meta alt 2") 
                           						:listen [:action (fn [_] (focus-editor app-atom))])
     :goto-file-tree   	(seesaw.core/menu-item 	:text "Go to Project Tree" 
                           						:mnemonic "P" 
                           						:key (keystroke/keystroke "meta alt 1") 
                           						:listen [:action (fn [_] (focus-file-tree app-atom))])
     :show-file-tree 	(seesaw.core/menu-item 	:text "Show File Tree" 
                           						:key (keystroke/keystroke "meta 1") 
                           						:listen [:action (fn [_] (show-file-tree app-atom))])
     :show-repl        	(seesaw.core/menu-item 	:text "Show REPL" 
                           						:key (keystroke/keystroke "meta 2") 
                           						:listen [:action (fn [_] (show-repl app-atom))])
     :next-tab 			(seesaw.core/menu-item 	:text "Next tab"
		                					 	:key (keystroke/keystroke "meta alt RIGHT")
		                					 	:listen [:action (fn [_] (next-tab app-atom))])
     :previous-tab    	(seesaw.core/menu-item 	:text "Previous tab"
                					 			:key (keystroke/keystroke "meta alt LEFT")
                					 			:listen [:action (fn [_] (previous-tab app-atom))])
     :close-tab 		(seesaw.core/menu-item 	:text "Close tab"
                           						:key (keystroke/keystroke "meta W")
                           						:listen [:action (fn [_] (close-tab app-atom))])})

(defn make-view-menu [app-atom]
"Make view menu"
  (let [menu-items (make-view-menu-items app-atom)]
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






