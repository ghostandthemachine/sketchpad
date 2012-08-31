(ns sketchpad.menu.file 
	(:use [seesaw meta chooser])
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
        [sketchpad.editor.info-utils :as info-utils]
        [seesaw.keystroke :as keystroke]
        [sketchpad.auto-complete.auto-complete :as auto-complete]
        [sketchpad.tree.utils :as tree.utils]))

(defn lein-project-path [lein-project]
"Returns the src path of a Leiningen project."
(first (lein-project :source-paths)))

(defn new-file
"Create a new file"
  ([] 
	(if-let [selection-path (tree.utils/get-selected-file-path @state/app)]
		(new-file selection-path)
		(new-file ".sketchpad-tmp/src/sketchpad_tmp/")))
  ([selection-path]
    (if-let [current-project-path (first (tree.utils/get-selected-projects))]
      (seesaw.core/invoke-later
        (editor.buffer/new-project-buffer! current-project-path selection-path)
        (tree.utils/update-tree))
      (seesaw.core/invoke-later
        (editor.buffer/new-project-buffer! ".sketchpad-tmp" selection-path)
        (tree.utils/update-tree)))))

(defn open-file
"Open a file."
  ([]
  (seesaw.core/invoke-later
	  (when-let [open-path (choose-file :filters [["Folders" #(.isDirectory %)]
							                       (file-filter "All files" (constantly true))]
				                       :success-fn (fn [fc file] (.getAbsolutePath file)))]
		(editor.buffer/open-buffer open-path ".sketchpad-tmp")))))

  
(defn save
"Save the current buffer."
([] (save (tab/current-buffer)))
([buffer]
(when (tab/tabs?)
	(let [new-file? @(buffer :new-file?)]
	  (if new-file?
	    (do
            (seesaw.core/invoke-later
		      (when-let [new-file (file/save-file-as! (:selection-path buffer))]
		        (let[new-file-title (.getName new-file)] 
	  	          (reset! (:file buffer) new-file)
	  	          (reset! (:new-file? buffer) false) 
	  	          (when (file/save-file! buffer)
	  	            (tab/title-at! (tab/index-of-buffer buffer) new-file-title)
	  	            (auto-complete/add-file-completion (:project buffer) new-file)
	  	            (reset! (:title buffer) new-file-title)
	  	            (tab/mark-current-tab-clean!))))))
	    (do
        (seesaw.core/invoke-later
          (when (file/save-file! buffer)
	          (tab/mark-current-tab-clean!)))))
    (seesaw.core/invoke-later
      (when (= @(get-in buffer [:component :title]) "project.clj")
        (project/update-lein-project! (project/project-from-buffer buffer)))
        (info-utils/update-doc-title-label!) 
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
  :open-file (seesaw.core/menu-item :text "Open File"
                              :mnemonic "O" 
                              :key (keystroke/keystroke "meta O")
                              :listen [:action (fn [_] (open-file))])                              
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
                  (menu-items :open-file)                  
                  (seesaw.core/separator)
                  (menu-items :save)
                  (menu-items :save-as)])))