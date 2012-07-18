(ns sketchpad.menu.file 
	(:use [seesaw meta bind])
	(:require [sketchpad.menu.menu-utils :as menu-utils]
        [sketchpad.filetree :as file-tree]
			  [sketchpad.tab-builder :as tab-builder]
			  [sketchpad.tab-manager :as tab-manager]
			  [sketchpad.file-manager :as file-manager]
        [sketchpad.filetree :as file-tree]
			  [sketchpad.rsyntaxtextarea :as rsyntaxtextarea]
        [seesaw.core :as seesaw.core]
        [seesaw.keystroke :as keystroke]))


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
  (if (tab-manager/tabs? tabbed-panel)  
    (do 
      (let [rta (tab-manager/current-text-area tabbed-panel)
            clean (:clean (get-meta rta :state))]
        (when clean
          (reset! activation-atom false)
          (reset! activation-atom true))))
    (reset! activation-atom false)))

(defn save-as? [tabbed-panel activation-atom]
  (if (tab-manager/tabs? tabbed-panel)  
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
	   file (get-meta rsta :file)
     file-path (file-tree/get-selected-file-path app)]
	(when-let[new-file (file-tree/save-file-as app-atom file-path file)]
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
                              :listen [:action (fn [_] (save-file-as! app-atom))])})

; (defn new-file!
; "Create a new file"
; []
; (tab/new-tab!))

; (defn save-file! 
; "Save the current buffer."
; []
; (file/save-file!))

; (defn save-file-as!
; "Open the save as dialog for the current buffer."
; []
; (let [app @app-atom
;     rsta (tab-manager/current-text-area (:editor-tabbed-panel app))
;     file (get-meta rsta :file)]
;   (when-let[new-file (file-tree/save-file-as rsta file)]
;     (println new-file)
;     (tab-builder/new-file-tab! app-atom new-file))))

; (defn file-menu-items [app-atom]
;  [[:new-file "New File" "cmd N" ]])



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

