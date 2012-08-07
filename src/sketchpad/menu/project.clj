(ns sketchpad.menu.project
	(:require [seesaw.keystroke :as keystroke]
			[seesaw.core :as seesaw]
			[sketchpad.tree.utils :as tree.utils]
			[sketchpad.project.form :as project.form]))

(defn create-projet
"Create a new Leiningen project."
  []
  (project.form/create-new-project))

(defn open-project
"Open a project."
  []
  (tree.utils/open-project))

(defn rename-project
"Rename a project."
  []
  (open-project))

(defn remove-project
"Remove a from the session."
  []
  (remove-project))

; (defn new-folder

;   ([path]
;     ))

; (defn make-project-menu-items [app-atom]
; 	{:new-project 	(seesaw.core/menu-item :text "New Project..." 
; 				                           :mnemonic "N" 
; 				                           :key (keystroke/keystroke "meta shift N") 
;                         					:listen [:action (fn [_] (create-projet))])
; 	 :open-project  (seesaw.core/menu-item :text "Open Project..." 
; 				                           :mnemonic "O" 
; 				                           :key (keystroke/keystroke "meta shift O") 
; 				                           :listen [:action (fn [_] (open-project))])
; 	 :rename-project (seesaw.core/menu-item :text "Rename project" 
; 				                           :mnemonic "M" 
; 				                           :listen [:action (fn [_] (rename-project))])
; 	 :remove-project (seesaw.core/menu-item :text "Remove project" 
; 				                           :listen [:action (fn [_] (remove-project))])})

; (defn make-project-menu
; []
;   (let [menu-items (make-project-menu-items app-atom)]
; 	  (seesaw.core/menu :text "Project" 
; 	            		:mnemonic "P"
; 				        :items [(menu-items :new-project)
; 				        		(menu-items :open-project)	
; 				                (separator)
; 				                (menu-items :rename-project)
; 				                (menu-items :remove-project)])))



; (defn create-repl []
;   (let [project-path (first (tree.utils/get-selected-projects))]
;     (seesaw/invoke-later 
;       (repl/repl (project/project-from-path project-path)))))

; (defn make-filetree-popup
;   []
;   (seesaw/popup 
;     :id :filetree-popup
;     :class :popup
;     :items [
;             ; (menu-item :text "New File" 
;             ;           :listen [:action (fn [_] (new-file app-atom (first (get-selected-projects app)) ""))])
;             ; (menu-item :text "New Folder" )
;             ; (separator)
;             (seesaw/menu-item :text "New Project" 
;                       :mnemonic "N" 
;                       :listen [:action (fn [_] (project.form/create-new-project))])
;             (seesaw/menu-item :text "Open Project" 
;                       :mnemonic "O" 
;                       :listen [:action (fn [_] (tree.utils/open-project app))])
;             (seesaw/separator)
;             (seesaw/menu-item :text "Remove Project" 
;                       :mnemonic "M" 
;                       :listen [:action (fn [_] (tree.utils/remove-project app))])  
;             (seesaw/menu-item :text "Clear All Projects" 
;                       :mnemonic "M" 
;                       :listen [:action (fn [_] (tree.utils/clear-projects))])
;             ; (menu-item :text "Rename Project" 
;             ;           :listen [:action (fn [_] (rename-project app))])
;             ; (separator)
;             ; (menu-item :text "Move/Rename" 
;             ;           :listen [:action (fn [_] (rename-file app))])
;             (seesaw/separator)
;             (seesaw/menu-item :text "Create REPL"
;                       :mnemonic "R" 
;                       :listen [:action (fn [_] (create-repl))])
;             (seesaw/separator)
;             (seesaw/menu-item :text "Delete file" 
;                       :listen [:action (fn [_] (tree.utils/delete-file app))])]))