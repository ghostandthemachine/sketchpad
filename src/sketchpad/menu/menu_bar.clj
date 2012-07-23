(ns sketchpad.menu.menu-bar
  (:use [clojure.pprint]
        [seesaw core keystroke meta])
  (:require 
        [sketchpad.tab :as tab]
        [sketchpad.menu.file :as sketchpad.menu.file]
        [sketchpad.menu.edit :as sketchpad.menu.edit]
        [sketchpad.menu.view :as sketchpad.menu.view]
        [sketchpad.repl-tab-builder :as repl]))

(defn update-menu-state [tabbed-panel]
"Based on the state of the tabbed panel, set active menu items"
  
)

(defn make-help-menu
  []
  (menu :text "Help"
        :mnemonic "H"
        :items []))

(defn make-repl-menu
  []
  (menu :text "REPL"
        :mnemonic "R"
        :items [  (menu-item :text "Create new REPL" 
                             :mnemonic "N" 
                             :key (keystroke "meta control R") 
                             :listen [:action (fn [_] 
                                              (repl/new-repl-tab! (tab/current-buffer)))])
                    (separator)]))

(defn make-menus
  [app-atom]
  (let [app @app-atom
        file-menu (sketchpad.menu.file/make-file-menu app-atom)
        edit-menu (sketchpad.menu.edit/make-edit-menu)
        view-menu (sketchpad.menu.view/make-view-menu)
        repl-menu (make-repl-menu)
        help-menu (make-help-menu)]
    (config! 
      (:frame @app-atom) :menubar 
                      (menubar :items [ 
                        file-menu
                                        ; edit-menu
                                        ; (make-source-menu app-atom)
                                        view-menu
                                        repl-menu
                                        help-menu]))))


