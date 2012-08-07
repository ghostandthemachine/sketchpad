(ns sketchpad.tree.popup
  (:use [sketchpad.system.desktop])
	(:require [sketchpad.state.state :as state]
			[seesaw.core :as seesaw]
			[seesaw.keystroke :as keystroke]
      [sketchpad.menu.file :as menu.file]
      [sketchpad.menu.project :as menu.project]
      [sketchpad.project.form :as project.form]
      [sketchpad.repl.project-repl :as repl]
      [sketchpad.system.desktop :as desktop]
      [sketchpad.project.project :as project]
			[sketchpad.tree.utils :as tree.utils]))

(defn create-repl []
  (let [project-path (first (tree.utils/get-selected-projects))]
    (seesaw/invoke-later 
      (repl/repl (project/project-from-path project-path)))))

(defn directory-popup 
"Creates a popup menu after a file directory is selected."
  []
    [(seesaw/menu-item :text "New File"
                              :listen [:action menu.file/new-file])
            ; (seesaw/menu-item :text "New Folder"
            ;                   :listen [:action menu.project/new-folder])
            ; (seesaw/menu-item :text "Rename"
            ;                   :listen [:action menu.project/delete-folder])
            ; (seesaw/menu-item :text "Delete Folder"
            ;                   :listen [:action menu.project/delete-folder])
            (seesaw/menu-item :text "Reveal in Finder"
                              :listen [:action desktop/reveal-in-finder])
            (seesaw/menu-item :text "Create REPL"
                              :listen [:action create-repl])])

(defn file-popup 
"Creates a popup menu after a file directory is selected."
  []
[
            ; (seesaw/menu-item :text "Delete File"
            ;                   :listen [:action menu.project/delete-folder])
            ; (seesaw/menu-item :text "Rename File"
            ;                   :listen [:action menu.project/delete-folder])
            (seesaw/menu-item :text "Create REPL"
                              :listen [:action create-repl])
            (seesaw/menu-item :text "Reveal in Finder"
                              :listen [:action desktop/reveal-in-finder])])

(defn make-filetree-popup
  []
  (let [app @state/app]
    (seesaw/popup 
      :id :filetree-popup
      :class :popup
      :items [
              ; (menu-item :text "New File" 
              ;           :listen [:action (fn [_] (new-file app-atom (first (get-selected-projects app)) ""))])
              ; (menu-item :text "New Folder" )
              ; (separator)
              (seesaw/menu-item :text "New Project" 
                        :mnemonic "N" 
                        :listen [:action (fn [_] (project.form/create-new-project))])
              (seesaw/menu-item :text "Open Project" 
                        :mnemonic "O" 
                        :listen [:action (fn [_] (tree.utils/open-project app))])
              (seesaw/separator)
              (seesaw/menu-item :text "Remove Project" 
                        :mnemonic "M" 
                        :listen [:action (fn [_] (tree.utils/remove-project app))])  
              (seesaw/menu-item :text "Clear All Projects" 
                        :mnemonic "M" 
                        :listen [:action (fn [_] (tree.utils/clear-projects))])
              ; (menu-item :text "Rename Project" 
              ;           :listen [:action (fn [_] (rename-project app))])
              ; (separator)
              ; (menu-item :text "Move/Rename" 
              ;           :listen [:action (fn [_] (rename-file app))])
              (seesaw/separator)
              (seesaw/menu-item :text "Create REPL"
                        :mnemonic "R" 
                        :listen [:action (fn [_] (create-repl))])
              (seesaw/separator)
              (seesaw/menu-item :text "Delete file" 
                        :listen [:action (fn [_] (tree.utils/delete-file app))])])))
