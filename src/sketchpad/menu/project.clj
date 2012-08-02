(ns sketchpad.menu.project
	(:require [seesaw.keystroke :as keystroke]
			[seesaw.core :as seesaw.core]))

(defonce project-menu-item-state 
  { :new-project (atom true)
    :open-project (atom true)
    :rename-project (atom false)
    :remove-project (atom false)})

(defn create-leiningen-projet
"Create a new Leiningen project."
  [app-atom]
  (new-project @app-atom))

(defn open-leiningen-project
"Open Leiningen project."
  [app-atom]
  (open-project @app-atom))

(defn rename-leiningen-project
"Rename a Leiningen project."
  [app-atom]
  (open-project @app-atom))

(defn remove-leiningen-project
"Remove a project from the session."
  [app-atom]
  (remove-project @app-atom))

(defn make-project-menu-items [app-atom]
	{:new-project 	(seesaw.core/menu-item :text "New..." 
				                           :mnemonic "N" 
				                           :key (keystroke/keystroke "meta shift N") 
				                           :listen [:action (fn [_] (create-leiningen-projet app-atom))])
	 :open-project  (seesaw.core/menu-item :text "Open..." 
				                           :mnemonic "O" 
				                           :key (keystroke/keystroke "meta shift O") 
				                           :listen [:action (fn [_] (open-leiningen-project app-atom))])
	 :rename-project (seesaw.core/menu-item :text "Rename project" 
				                           :mnemonic "M" 
				                           :listen [:action (fn [_] (rename-leiningen-project app-atom))])
	 :remove-project (seesaw.core/menu-item :text "Remove project" 
				                           :listen [:action (fn [_] (remove-leiningen-project app-atom))]))

(defn make-project-menu
[app]
  (let [menu-items (make-project-menu-items app-atom)]
	  (seesaw.core/menu :text "Project" 
	            		:mnemonic "P"
				        :items [(menu-items :new-project)
				        		(menu-items :open-project)	
				                (separator)
				                (menu-items :rename-project)
				                (menu-items :remove-project)])))
