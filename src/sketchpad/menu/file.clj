(ns sketchpad.menu.file 
	(:use [seesaw meta])
	(:require [sketchpad.menu.menu-utils :as menu-utils]
        [sketchpad.filetree :as file-tree]
			  [sketchpad.tab-builder :as tab-builder]
			  [sketchpad.tab :as tab]
        [sketchpad.buffer-new :as buffer-new]
        [sketchpad.file :as file]
        [sketchpad.filetree :as file-tree]
			  [sketchpad.rsyntaxtextarea :as rsyntaxtextarea]
        [sketchpad.state :as sketchpad.state]
        [seesaw.core :as seesaw.core]
        [seesaw.keystroke :as keystroke]))

(def app sketchpad.state/app)

(defonce file-menu-item-state
  { :new-file (atom true)
    :save (atom false)
    :save-as (atom false)
    :open (atom true)})

(defn new-file? [tabbed-panel activation-atom]
  (reset! activation-atom true))

(defn open? [tabbed-panel activation-atom]
  (reset! activation-atom true))

(defn save? [tabbed-panel activation-atom]
  (if (tab/tabs? tabbed-panel)  
    (do 
      (let [rta (tab/current-text-area tabbed-panel)
            clean (:clean (get-meta rta :state))]
        (when clean
          (reset! activation-atom false)
          (reset! activation-atom true))))
    (reset! activation-atom false)))

(defn save-as? [tabbed-panel activation-atom]
  (if (tab/tabs? tabbed-panel)  
    (reset! activation-atom true)
    (reset! activation-atom false)))

(defonce file-menu-activation-fns
  { :new-file new-file?
    :save     save?
    :save-as  save-as?
    :open     open?})

(defn lein-project-path [lein-project]
"Returns the src path of a Leiningen project."
(first (lein-project :source-paths)))

(defn new-file!
"Create a new file"
[app-atom file-path]
  (buffer-new/blank-clj-buffer!))

(defn save-file! []
"Save the current buffer."
(let [buffer (tab/current-text-area)
      new-file? (get-meta buffer :new-file)]
  (if new-file?
    (do
      (let [new-file (file/save-file-as)
            new-file-title (.getName new-file)]
        (when (file/save-file buffer new-file)
          (put-meta! buffer :file new-file)
          (put-meta! buffer :new-file false)
          (tab/title-at! (tab/index-of-component buffer) new-file-title)
          (tab/mark-current-tab-clean! (@app :editor-tabbed-panel)))))
    (do
      (when (file/save-file buffer (get-meta buffer :file))
             (tab/mark-current-tab-clean! (@app :editor-tabbed-panel)))))))

(defn save-file-as! []
"Open the save as dialog for the current buffer."
(let [rsta (tab/current-text-area (:editor-tabbed-panel @app))
	   file (get-meta rsta :file)
     file-path (file-tree/get-selected-file-path @app)]
	(when-let[new-file (file/save-file-as)]
    (when (get-meta rsta :new-file)
      (put-meta! rsta :new-file false))
		(println new-file))))

(defn make-file-menu-items [app-atom]
 {:new-file (seesaw.core/menu-item :text "New File" 
                              :mnemonic "N" 
                              :key (keystroke/keystroke "meta N") 
                              :listen [:action (fn [_] (new-file! app-atom (file-tree/get-selected-file-path @app-atom)))])
  :save     (seesaw.core/menu-item :text "Save" 
                              :mnemonic "S" 
                              :key (keystroke/keystroke "meta S") 
                              :listen [:action (fn [_] (save-file!))])
  :save-as  (seesaw.core/menu-item :text "Save as..." 
                              :mnemonic "M" 
                              :key (keystroke/keystroke "meta shift S")
                              :listen [:action (fn [_] (save-file-as!))])})

(defn make-file-menu
  [app-atom]
  (let [menu-items (make-file-menu-items app-atom)]
    (menu-utils/set-menu-item-bindings file-menu-item-state menu-items)
    (seesaw.core/menu :text "File"
          :mnemonic "F"
          :items [
                  (menu-items :new-file)
                  (menu-items :save)
                  (menu-items :save-as)])))

