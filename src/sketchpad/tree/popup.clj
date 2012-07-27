(ns sketchpad.tree.popup
	(:require [sketchpad.state :as state]
			[seesaw.core :as seesaw]
			[seesaw.keystroke :as keystroke]
      [sketchpad.menu.file :as menu.file]
      [sketchpad.repl.repl :as repl]
      [sketchpad.project.project :as project]
			[sketchpad.tree.utils :as utils]))

(defn create-repl []
  (let [project-path (first (utils/get-selected-projects))]
    (repl/repl (project/project-from-path project-path))))

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
              ; (menu-item :text "New Project" 
              ;           :mnemonic "N" 
              ;           :key (keystroke "meta shift N") 
              ;           :listen [:action (fn [_] (new-project app))])
              (seesaw/menu-item :text "Open Project" 
                        :mnemonic "O" 
                        :listen [:action (fn [_] (utils/open-project app))])
              (seesaw/separator)
              (seesaw/menu-item :text "Remove Project" 
                        :mnemonic "M" 
                        :listen [:action (fn [_] (utils/remove-project app))])              
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
              (seesaw/menu-item :text "Delete" 
                        :listen [:action (fn [_] (utils/delete-file app))])
              ])))