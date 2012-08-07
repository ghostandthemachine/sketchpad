(ns sketchpad.tree.popup
  (:use [sketchpad.system.desktop])
	(:require [sketchpad.state.state :as state]
			[seesaw.core :as seesaw]
			[seesaw.keystroke :as keystroke]
      [sketchpad.menu.file :as menu.file]
      [sketchpad.menu.project :as menu.project]
      [sketchpad.project.form :as project.form]
      [sketchpad.system.desktop :as desktop]
      [sketchpad.project.project :as project]
			[sketchpad.tree.utils :as tree.utils]))

(defn directory-popup 
"Creates a popup menu after a file directory is selected."
  [selection-path & opts]
  (seesaw/popup 
    :id :directory-popup
    :class :popup
    :items [(seesaw/menu-item :text "New Project"
                            :listen [:action (fn [_] (menu.project/create-project))])
            (seesaw/menu-item :text "Open Project"
                              :listen [:action (fn [_] (menu.project/open-project))])
            (seesaw/separator)
            (seesaw/menu-item :text "New File"
                              :listen [:action (fn [_] (menu.file/new-file selection-path))])
            (seesaw/separator)
            (seesaw/menu-item :text "New Folder"
                              :listen [:action (fn [_] (menu.project/new-folder selection-path))])
            (seesaw/separator)
            (seesaw/menu-item :text "Delete Folder"
                              :listen [:action (fn [_] (menu.project/delete-folder selection-path))])
            (seesaw/separator)
            (seesaw/menu-item :text "Reveal in Finder"
                              :listen [:action (fn [_] (desktop/reveal-in-finder selection-path))])
            (seesaw/separator)
            (seesaw/menu-item :text "Create REPL"
                              :listen [:action (fn [_] (menu.project/create-repl selection-path))])]))

(defn file-popup 
"Creates a popup menu after a file directory is selected."
  [selection-path & opts]
  (seesaw/popup 
    :id :file-popup
    :class :popup
    :items [(seesaw/menu-item :text "Create REPL"
                              :listen [:action (fn [_] (menu.project/create-repl selection-path))])
            (seesaw/separator)
            (seesaw/menu-item :text "Reveal in Finder"
                              :listen [:action (fn [_] (desktop/reveal-in-finder selection-path))])
            (seesaw/separator)
            (seesaw/menu-item :text "Delete file" 
                              :listen [:action (fn [_] (tree.utils/delete-file @state/app))])]))

(defn project-popup 
"Creates a popup menu after a project's root directory is selected."
  [selection-path & opts]
  (seesaw/popup 
    :id :project-popup
    :class :popup
    :items [(seesaw/menu-item :text "New Project"
                            :listen [:action (fn [_] (menu.project/create-project))])
            (seesaw/menu-item :text "Open Project"
                              :listen [:action (fn [_] (menu.project/open-project))])
            (seesaw/separator)
            (seesaw/menu-item :text "New Folder" 
                              :mnemonic "M" 
                              :listen [:action (fn [_] (menu.project/new-folder selection-path))])
            (seesaw/separator)
            (seesaw/menu-item :text "Remove Project" 
                              :mnemonic "M" 
                              :listen [:action (fn [_] (tree.utils/remove-project @state/app))])
            (seesaw/menu-item :text "Delete Project" 
                              :listen [:action (fn [_] (tree.utils/delete-project @state/app))]) 
            (seesaw/separator)
            (seesaw/menu-item :text "Create REPL"
                              :listen [:action (fn [_] (menu.project/create-repl selection-path))])
            (seesaw/separator)
            (seesaw/menu-item :text "Reveal in Finder"
                              :listen [:action (fn [_] (desktop/reveal-in-finder selection-path))])]))

(defn no-selection-popup
  [& opts]
  (seesaw/popup 
    :id :file-popup
    :class :popup
    :items [(seesaw/menu-item :text "New Project"
                            :listen [:action (fn [_] (menu.project/create-project))])
            (seesaw/menu-item :text "Open Project"
                              :listen [:action (fn [_] (menu.project/open-project))])
            (seesaw/separator)
            (seesaw/menu-item :text "Clear Projects"
                              :listen [:action (fn [_] (menu.project/clear-projects))])]))

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
                        :listen [:action (fn [_] (menu.project/create-repl))])
              (seesaw/separator)
              (seesaw/menu-item :text "Delete file" 
                        :listen [:action (fn [_] (tree.utils/delete-file app))])])))
