(ns sketchpad.menu.file 
	(:use [seesaw core keystroke meta])
	(:require [sketchpad.filetree :as file-tree]
			  [sketchpad.tab-builder :as tab-builder]
			  [sketchpad.tab-manager :as tab-manager]
			  [sketchpad.file-manager :as file-manager]
			  [sketchpad.rsyntaxtextarea :as rsyntaxtextarea]))

(defn lein-project-path [lein-project]
"Returns the src path of a Leiningen project."
(first (lein-project :source-paths)))

(defn new-file!
"Create a new file"
[app-atom file-path]
(when-let [new-file (file-tree/new-file app-atom file-path)]
	(println new-file)
	(tab-builder/new-file-tab! app-atom new-file)))

(defn save-file! [app-atom]
"Save the current buffer."
(let [app @app-atom
	  rsta (tab-manager/current-text-area (:editor-tabbed-panel app))]
	(if (file-manager/save-file rsta)
	(tab-manager/mark-current-tab-clean! (app :editor-tabbed-panel)))))

(defn save-file-as! [app-atom]
"Open the save as dialog for the current buffer."
(let [app @app-atom
	  rsta (tab-manager/current-text-area (:editor-tabbed-panel app))
	  file (get-meta rsta :file)]
	(when-let[new-file (file-tree/save-file-as rsta file)]
		(println new-file)
		(tab-builder/new-file-tab! app-atom new-file))))

(defn make-file-menu
  [app-atom]
  (let [app @app-atom]
    (menu :text "File"
        :mnemonic "F"
        :items [
                (menu-item :text "New" 
                           :mnemonic "N" 
                           :key (keystroke "meta N") 
                           :listen [:action (fn [_] (new-file! app-atom (file-tree/get-selected-file-path app)))])
                (menu-item :text "Save" 
                           :mnemonic "S" 
                           :key (keystroke "meta S") 
                           :listen [:action (fn [_] (save-file! app-atom))])
                (separator)
                (menu-item :text "Move/Rename" 
                           :mnemonic "M" 
                           :listen [:action (fn [_] (file-tree/save-file-as app))])
                (if (rsyntaxtextarea/is-osx?)
                  (do 
                  	(separator)
                    (menu-item :text "Quit"
                               :mnemonic "Q"
                               :key (keystroke "meta Q")
                               :listen [:action (fn [_] (System/exit 0))])))])))

