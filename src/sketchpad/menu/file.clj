(ns sketchpad.menu.file 
	(:use [seesaw meta bind])
	(:require [sketchpad.menu.menu-utils :as menu-utils]
        [sketchpad.filetree :as file-tree]
			  [sketchpad.tab-builder :as tab-builder]
			  [sketchpad.tab-manager :as tab-manager]
			  [sketchpad.file-manager :as file-manager]
			  [sketchpad.rsyntaxtextarea :as rsyntaxtextarea]
        [seesaw.core :as seesaw.core]
        [seesaw.keystroke :as keystroke]))

(defonce file-menu-item-state 
  { :new-file (atom true)
    :save (atom false)
    :save-as (atom false)
    :open (atom true)})

(defn set-edit-menu-item-state-bindings [item-state-map item-map]
  (doseq [[k v] item-state-map]
   (menu-utils/set-menu-binding (k item-state-map) (k item-map))))

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

(defn make-file-menu-items [app-atom]
 {:new-file (seesaw.core/menu-item :text "New File" 
                              :mnemonic "N" 
                              :key (keystroke/keystroke "meta N") 
                              :listen [:action (fn [_] (new-file! app-atom (file-tree/get-selected-file-path @app-atom)))])
  :save     (seesaw.core/menu-item :text "Save" 
                              :mnemonic "S" 
                              :key (keystroke/keystroke "meta S") 
                              :listen [:action (fn [_] (save-file! app-atom))])
  :save-as  (seesaw.core/menu-item :text "Save as..." 
                              :mnemonic "M" 
                              :key (keystroke/keystroke "meta shift S")
                              :listen [:action (fn [_] (file-tree/save-file-as @app-atom))])})



(defn make-file-menu
  [app-atom]
  (let [menu-items (make-file-menu-items app-atom)]
    (seesaw.core/menu :text "File"
          :mnemonic "F"
          :items [
                  (menu-items :new-file)
                  (menu-items :save)
                  (menu-items :save-as)])))

