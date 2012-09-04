(ns sketchpad.menu.menu-bar
  (:use [seesaw core keystroke meta])
  (:require
        [sketchpad.wrapper.rsyntaxtextarea :as rsta]
        [sketchpad.util.tab :as tab]
        [sketchpad.project.project :as project]
        [sketchpad.menu.file :as sketchpad.menu.file]
        [sketchpad.menu.edit :as sketchpad.menu.edit]
        [sketchpad.menu.goto :as sketchpad.menu.goto]
        [sketchpad.menu.project :as sketchpad.menu.project]
        [sketchpad.menu.source :as sketchpad.menu.source]
        [sketchpad.menu.view :as sketchpad.menu.view]))

(defn make-help-menu
  []
  (menu :text "Help"
        :mnemonic "H"
        :items []))

(defn make-menus
  [app-atom]
  (let [app @app-atom
        file-menu (sketchpad.menu.file/make-file-menu)
        edit-menu (sketchpad.menu.edit/make-edit-menu)
        project-menu (sketchpad.menu.project/make-project-menu)
        view-menu (sketchpad.menu.view/make-view-menu)
        goto-menu (sketchpad.menu.goto/make-goto-menu)
        source-menu (sketchpad.menu.source/make-source-menu)
        help-menu (make-help-menu)
        items [file-menu
                        edit-menu
                        project-menu
                        view-menu
                        goto-menu
                        source-menu]
        items (if (rsta/is-osx?)
                (conj items help-menu)
                items)]
    (config! 
      (:frame @app-atom) :menubar 
                      (menubar :items items))))
