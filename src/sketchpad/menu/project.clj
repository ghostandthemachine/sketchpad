(ns sketchpad.menu.project
	(:require [seesaw.keystroke :as keystroke]
			[seesaw.core :as seesaw]
			[sketchpad.tree.utils :as tree.utils]
      [sketchpad.util.utils :as utils]
      [sketchpad.project.project :as project]
      [sketchpad.repl.project-repl :as repl]
      [sketchpad.state.state :as state]
      [sketchpad.system.desktop :as desktop]
      [sketchpad.util.tab :as tab]
      [sketchpad.menu.view :as menu.view]
			[sketchpad.project.form :as project.form]))

(defn create-project
"Create a new Leiningen project."
  []
  (project.form/create-new-project))

(defn open-project
"Open a project."
  []
  (tree.utils/open-project @state/app))

(defn rename-project
"Rename a project."
  []
  (tree.utils/rename-project @state/app))

(defn remove-project
"Remove a project from the session."
  []
  (let [project-path (first (tree.utils/get-selected-projects))
        confirmed? (utils/confirmed? (str "Are you sure you want to remove " project-path " from the current workspace?") "Remove Project")]
    (when confirmed?
      (project/remove-project project-path))))

(defn delete-project
"Delete a project. !!(This really deletes the proejct)!!"
  ([] (delete-project (first (tree.utils/get-selected-projects))))
  ([project-path]
  (seesaw/invoke-later
	  	(project/delete-project project-path)
 		 (tree.utils/update-tree))))

(defn clear-projects
"Clear all projects in the current workspace."
  []
  (let [confirmed? (utils/confirmed? "Are you sure you want to clear all projects in the current workspace?" "Clear Projects")]
    (when confirmed?
      (doseq [buffer (tab/current-buffers)]
        (menu.view/close-tab buffer))
      (project/clear-projects))))

(defn new-folder
"Append a folder creation prompt to the SketchPad REPL."
  ([] (new-folder ""))
  ([dir-path]
    (seesaw/invoke-later
      (sketchpad.repl.print/append-command (str "(new-folder " \" dir-path "/" \" ")") -2)
      (tab/focus-application-repl))))

(defn delete-folder
"Delete a given directory."
  ([dir-path]
    (seesaw/invoke-later
      (desktop/delete-folder dir-path))))


(defn create-repl [selection-path]
"Create a new project REPL at the given path."
(seesaw/invoke-later
  (let [project-path (first (tree.utils/get-selected-projects))]
    (repl/repl (project/project-from-path project-path)))))

(defn make-project-menu
  []
  (seesaw/menu 
    :id :project-menu
    :class :menu
    :text "Project"
    :items [(seesaw/menu-item :text "New Project"
    							:key (keystroke/keystroke "meta shift N")
                            :listen [:action (fn [_] (create-project))])
            (seesaw/menu-item :text "Open Project"
    							:key (keystroke/keystroke "meta shift O")
                              :listen [:action (fn [_] (open-project))])
            (seesaw/separator)
            (seesaw.core/menu-item :text "Clear All Projects" 
                        :mnemonic "C" 
                        :listen [:action (fn [_] (clear-projects))])]))

