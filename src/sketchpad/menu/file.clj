(ns sketchpad.menu.file 
	(:use [seesaw meta])
	(:require [sketchpad.menu.menu-utils :as menu-utils]
        [sketchpad.tree.utils :as tree.utils]
			  [sketchpad.util.tab :as tab]
        [sketchpad.project.project :as project]
        [sketchpad.editor.buffer :as editor.buffer]
        [sketchpad.file.file :as file]
			  [sketchpad.wrapper.rsyntaxtextarea :as rsyntaxtextarea]
        [sketchpad.state.state :as state]
        [sketchpad.project.form :as project.form]
        [seesaw.core :as seesaw.core]
        [seesaw.keystroke :as keystroke]
        [sketchpad.tree.utils :as tree.utils]))

(defn lein-project-path [lein-project]
"Returns the src path of a Leiningen project."
(first (lein-project :source-paths)))

(defn new-file
"Create a new file"
  ([] 
  (new-file nil))
  ([selection-path]
    (if-let [current-project-path (first (tree.utils/get-selected-projects))]
      (seesaw.core/invoke-later
        (editor.buffer/new-project-buffer! current-project-path selection-path)
        (tree.utils/update-tree))
      (seesaw.core/invoke-later
        (editor.buffer/new-project-buffer! "tmp" selection-path)
        (tree.utils/update-tree)))))

(defn save
"Save the current buffer."
([] (save (tab/current-buffer)))
([buffer]
(when (tab/tabs?)
	(let [new-file? @(buffer :new-file?)]
	  (if new-file?
	    (do
	      (when-let [new-file (file/save-file-as! (:selection-path buffer))]
	        (let[new-file-title (.getName new-file)] 
            (seesaw.core/invoke-later
  	          (reset! (:file buffer) new-file)
  	          (reset! (:new-file? buffer) false) 
  	          (when (file/save-file! buffer)
  	            (tab/title-at! (tab/index-of-buffer buffer) new-file-title)
  	            (reset! (:title buffer) new-file-title)
  	            (tab/mark-current-tab-clean!))))))
	    (do
        (seesaw.core/invoke-later
          (when (file/save-file! buffer)
	          (tab/mark-current-tab-clean!)))))
    (seesaw.core/invoke-later
      (when (= @(get-in buffer [:component :title]) "project.clj")
        (project/update-lein-project! (project/project-from-buffer buffer)))
      (tree.utils/update-tree))))))

(defn save-as
"Open the save as dialog for the current buffer."
([] (save-as (tab/current-buffer)))
([buffer]
  (let [text-area (:text-area buffer)
        file @(:file buffer)
       file-path (tree.utils/get-selected-file-path @state/app)]
	  (when-let[new-file (file/save-file-as!)]
      (when @(:new-file? buffer)
        (reset! (:new-file? buffer) false)
        (reset! (:title buffer) (.getName new-file))))
    (tree.utils/update-tree))))

(defn new-project 
"Create a new Leiningen project."
  []
  (project.form/create-new-project))

(defn make-file-menu-items []
 {:new-file (seesaw.core/menu-item :text "New File" 
                              :mnemonic "N" 
                              :key (keystroke/keystroke "meta N") 
                              :listen [:action (fn [_] (new-file))])
  :save     (seesaw.core/menu-item :text "Save" 
                              :mnemonic "S" 
                              :key (keystroke/keystroke "meta S") 
                              :listen [:action (fn [_] (save))])
  :save-as  (seesaw.core/menu-item :text "Save as..." 
                              :mnemonic "M" 
                              :key (keystroke/keystroke "meta shift S")
                              :listen [:action (fn [_] (save-as))])})


(defn make-file-menu
  []
  (let [menu-items (make-file-menu-items)]
    (seesaw.core/menu :text "File"
          :mnemonic "F"
          :items [
                  (menu-items :new-file)
                  (seesaw.core/separator)
                  (menu-items :save)
                  (menu-items :save-as)])))