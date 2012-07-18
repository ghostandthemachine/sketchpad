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

(def macro-recording-state (atom false))

(defn toggle-macro-recording!
  [app]
  (if @macro-recording-state
    (do 
      (swap! macro-recording-state (fn [_] false))
      (rt/end-recording-macro! (tab-manager/current-text-area (:editor-tabbed-panel app))))
    (do
      (swap! macro-recording-state (fn [_] true))
      (rt/begin-recording-macro! (tab-manager/current-text-area (:editor-tabbed-panel app))))))

(defn playback-last-macro [app]
  (rt/playback-last-macro! (tab-manager/current-text-area (:editor-tabbed-panel app))))

(defn fold-action
  [ta] 
  (action :name "fold-action"
                         :handler #((cr/code-folding-enabled? ta (not (cr/code-folding-enabled? ta))))))

(defn view-menu 
  [ta]
  (menu :text "View" 
        :items [(checkbox-menu-item :text "Code Folding"
                                    :listen [:action #(fold-action ta)])]))

(defn about-action
  [ta]
  (action :name "About Sketchpad..."
          :handler (fn [_] )))

(defn animate-bracket-matching-action
  [ta]
  (action :name "Animate Bracket Matching"
          :handler (fn [_] (cr/animate-bracket-matching? ta (not (cr/animate-bracket-matching? ta))))))

(defn bookmarks-action
  [sp]
  (action :name "Bookmarks"
          :handler (fn [_] (sp/icon-row-header-enabled? sp (not (sp/icon-row-header-enabled? sp))))))

(defn code-folding-action
  [ta]
  (action :name "Code Folding"
          :handler (fn [_] (cr/code-folding-enabled? ta (not (cr/code-folding-enabled? ta))))))

(defn mark-occurrences-action
  [ta]
  (action :name "Mark Occurrences"
          :handler (fn [_] (cr/mark-occurrences? ta (not (cr/mark-occurrences? ta))))))

(defn sketchpad-menus
  [app]
  (when-not (contains? app :menus)
    (config! (app :frame) :menubar 
      (menubar 
        :items [(edit-text-menu (app :doc-text-area))
                (menu :text "View"
                      :items [(checkbox-menu-item :text "Animate Bracket Matching"
                                                  :listen [:action (fn [_] (animate-bracket-matching-action (app :doc-text-area)))])
                              (checkbox-menu-item :text "Code Folding"
                                                  :listen [:action (fn [_] (code-folding-action (app :doc-text-area)))])
                              (checkbox-menu-item :text "Bookmarks"
                                                  :listen [:action (fn [_] (bookmarks-action (app :doc-scroll-pane)))])])
                (menu :text "Project"
                      :items [])]))))


(defn make-source-menu
  [app-atom]
  (menu :text "Source"
        :mnemonic "U"
        :items [(menu-item :text "Comment-out" 
                           :mnemonic "C" 
                           :key (keystroke "meta SEMICOLON") 
                           :listen [:action (fn [_] (comment-out (:doc-text-area @app-atom)))])
                (menu-item :text "Uncomment-out" 
                           :mnemonic "U" 
                           :key (keystroke "meta shift SEMICOLON") 
                           :listen [:action (fn [_] (uncomment-out (:doc-text-area @app-atom)))])
                (separator)
                (menu-item :text "Find" 
                           :mnemonic "F" 
                           :key (keystroke "meta F") 
                           :listen [:action (fn [_] (toggle-search app-atom))])
               (separator)
                (menu-item :text "Begin recording macro..." 
                           :key (keystroke "ctrl Q") 
                           :listen [:action (fn [_] (toggle-macro-recording! @app-atom))])
                (menu-item :text "End recording macro..." 
                           :key (keystroke "ctrl Q") 
                           :listen [:action (fn [_] (toggle-macro-recording! @app-atom))])
                (menu-item :text "Playback last macro..." 
                           :key (keystroke "ctrl shift Q") 
                           :listen [:action (fn [_] (playback-last-macro @app-atom))])]))

(defn use-reload-current-file-ns
  [app]
  (if (= "ns" (str (first (current-ns-form app))))
    (do 
      (send-to-editor-repl (tab-manager/current-text-area (:editor-tabbed-panel app)) (str "(use :reload " \' (str (second (current-ns-form app))) ")")))))

(defn require-reload-current-file-ns
  [app]
  (if (= "ns" (str (first (current-ns-form app))))
    (do 
      (send-to-editor-repl (tab-manager/current-text-area (:editor-tabbed-panel app)) (str "(require :reload " \' (str (second(current-ns-form app))) ")")))))

(defn in-ns-current-file-ns
  [app]
  (if (= "ns" (str (first (current-ns-form app))))
    (do 
      (send-to-editor-repl (tab-manager/current-text-area (:editor-tabbed-panel app)) (str "(in-ns " \' (str (second (current-ns-form app))) ")"))
      )))

(defn make-help-menu
  [app]
  (menu :text "Help"
        :mnemonic "H"
        :items []))

(defn make-menus
  [app-atom]
  (let [app @app-atom
        file-menu (sketchpad.menu.file/make-file-menu app-atom)
        edit-menu (sketchpad.menu.edit/make-edit-menu app-atom)
        view-menu (sketchpad.menu.view/make-view-menu app-atom)]
    (config! 
      (:frame @app-atom) :menubar 
                      (menubar :items [ file-menu
                                        edit-menu
                                        ; (make-source-menu app-atom)
                                        view-menu
                                        ; (make-help-menu app)
                                        ]))))




