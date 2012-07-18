(ns sketchpad.menu.menu-bar
  (:use [clojure.pprint]
        [seesaw core keystroke meta]
        [sketchpad repl-tab-builder tab-builder help file-manager tab-manager repl-communication utils edit-menu vim-mode default-mode edit-mode vim-mode layout-config toggle-vim-mode-action filetree completion-builder]
        [clooj project dev-tools indent editor doc-browser style indent])
  (:require 
        [sketchpad.rtextscrollpane :as sp]
        [sketchpad.rsyntaxtextarea :as cr]
        [sketchpad.rtextarea :as rt]
        [sketchpad.tab-manager :as tab-manager]
        [sketchpad.lein-manager :as lein]
        [sketchpad.menu.file :as sketchpad.menu.file]
        [sketchpad.menu.edit :as sketchpad.menu.edit]
        [sketchpad.menu.view :as sketchpad.menu.view]))

(defn update-menu-state [tabbed-panel]
"Based on the state of the tabbed panel, set active menu items"
  
)

(defn make-help-menu
  []
  (menu :text "Help"
        :mnemonic "H"
        :items []))

(defn make-repl-menu
  [app-atom]
  (menu :text "REPL"
        :mnemonic "R"
        :items [  (menu-item :text "Create new REPL" 
                             :mnemonic "N" 
                             :key (keystroke "meta control R") 
                             :listen [:action (fn [_] 
                                              (new-repl-tab! 
                                                app-atom))])
                    (separator)]))

(defn make-menus
  [app-atom]
  (let [app @app-atom
        file-menu (sketchpad.menu.file/make-file-menu app-atom)
        edit-menu (sketchpad.menu.edit/make-edit-menu app-atom)
        view-menu (sketchpad.menu.view/make-view-menu app-atom)
        repl-menu (make-repl-menu app-atom)
        help-menu (make-help-menu)]
    (config! 
      (:frame @app-atom) :menubar 
                      (menubar :items [ file-menu
                                        edit-menu
                                        ; (make-source-menu app-atom)
                                        view-menu
                                        repl-menu
                                        help-menu]))))




