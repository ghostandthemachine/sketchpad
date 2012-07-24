(ns sketchpad.menu.menu-bar
  (:use [seesaw core keystroke meta])
  (:require 
        [sketchpad.tab :as tab]
        [sketchpad.project.project :as project]
        [sketchpad.menu.file :as sketchpad.menu.file]
        [sketchpad.menu.edit :as sketchpad.menu.edit]
        [sketchpad.menu.view :as sketchpad.menu.view]))


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
                                              ; (repl/new-repl-tab! (tab/current-buffer))
                                              )])]))

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


