(ns sketchpad.menu
  (:use [clojure.pprint]
        [seesaw core keystroke meta]
        [sketchpad repl-tab-builder tab-builder help file-manager tab-manager repl-communication utils edit-menu vim-mode default-mode edit-mode vim-mode layout-config toggle-vim-mode-action filetree completion-builder]
        [clooj project dev-tools indent editor doc-browser style indent])
  (:require 
        [sketchpad.rtextscrollpane :as sp]
        [sketchpad.rsyntaxtextarea :as cr]
        [sketchpad.rtextarea :as rt]
        [sketchpad.tab-manager :as tab-manager]
        [sketchpad.lein-manager :as lein]))

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

(defn create-new-file [app-atom]
  (when-let [new-file (new-file app-atom (first (get-selected-projects @app-atom)))]))

(defn make-file-menu
  [app-atom]
  (let [app @app-atom]
    (menu :text "File"
        :mnemonic "F"
        :items [
                (menu-item :text "New" 
                           :mnemonic "N" 
                           :key (keystroke "meta N") 
                           :listen [:action (fn [_] (create-new-file app-atom))])
                (menu-item :text "Save" 
                           :mnemonic "S" 
                           :key (keystroke "meta S") 
                           :listen [:action (fn [_] 
                                              (let [rsta (tab-manager/current-text-area (:editor-tabbed-panel app))]
                                                  (if (save-file (tab-manager/current-text-area (:editor-tabbed-panel app)))
                                                    (mark-current-tab-clean! (app :editor-tabbed-panel)))))])
                (separator)
                (menu-item :text "Move/Rename" 
                           :mnemonic "M" 
                           :listen [:action (fn [_] (rename-file app))])
                (menu-item :text "Revert" 
                           :mnemonic "R" 
                           :listen [:action (fn [_] (revert-file app))])
                (menu-item :text "Delete" 
                           :listen [:action (fn [_] (delete-file app))])
                (if (is-mac)
                  (do 
                  	(separator)
                    (menu-item :text "Quit"
                               :mnemonic "Q"
                               :key (keystroke "meta Q")
                               :listen [:action (fn [_] (System/exit 0))])))])))

(defn make-edit-menu
  [app]
  (menu :text "Edit" 
            :mnemonic "E"
        :items [(menu-item :text "Undo" 
                           :mnemonic "U" 
                           :key (keystroke "meta Z"))
                (menu-item :text "Redo" 
                           :mnemonic "Y" 
                           :key (keystroke "meta shift Z"))
                (separator)
                (menu-item :text "Copy" 
                           :mnemonic "C" 
                           :key (keystroke "meta C") 
                           :listen [:action (fn [_] (cr/copy-as-rtf (app :doc-text-area)))])
                (menu-item :text "Paste" 
                           :mnemonic "P" 
                           :key (keystroke "meta V") 
                           :listen [:action (fn [_] (rt/paste (:doc-text-area app)))])
                (separator)
                (menu-item :text "Increase font size" 
                           :key (keystroke "meta shift PLUS") 
                           :listen [:action (fn [_] (grow-font app))])
                (menu-item :text "Decrease font size" 
                           :key (keystroke "meta shift MINUS") 
                           :listen [:action (fn [_] (shrink-font app))])
                (separator)
                (menu-item :text "Choose font..." 
                           :listen [:action (fn [_] (apply show-font-window app set-font @current-font))])
                (separator)
                (menu :text "Mode"
                      :items [(checkbox-menu-item :text "Vim Command Mode"
                                                  :listen [:action (fn [_] (toggle-vim-mode! (app :doc-text-area)))])])]))

(defn make-project-menu
  [app]
  (menu :text "Project" 
            :mnemonic "P"
        :items [(menu-item :text "New..." 
                           :mnemonic "N" 
                           :key (keystroke "meta shift N") 
                           :listen [:action (fn [_] (new-project app))])
                (menu-item :text "Open..." 
                           :mnemonic "O" 
                           :key (keystroke "meta shift O") 
                           :listen [:action (fn [_] (open-project app))])
                (separator)
                (menu-item :text "Move/Rename" 
                           :mnemonic "M" 
                           :listen [:action (fn [_] (rename-project app))])
                (menu-item :text "Remove" 
                           :listen [:action (fn [_] (remove-project app))])]))

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

(defn make-view-menu
  [app]
  (menu :text "View"
        :mnemonic "V"
        :items [(menu-item :text "Go to REPL input" 
                           :mnemonic "R" 
                           :key (keystroke "meta alt 3") 
                           :listen [:action (fn [_] (.requestFocusInWindow (:editor-repl app)))])
                (menu-item :text "Go to Editor" 
                           :mnemonic "E" 
                           :key (keystroke "meta alt 2") 
                           :listen [:action (fn [_] (.requestFocusInWindow (:doc-text-area app)))])
                (menu-item :text "Go to Project Tree" 
                           :mnemonic "P" 
                           :key (keystroke "meta alt 1") 
                           :listen [:action (fn [_] (.requestFocusInWindow (:docs-tree app)))])
                (separator)
                (menu-item :text "Show File Tree" 
                           :key (keystroke "meta 1") 
                           :listen [:action (fn [_] (toggle-file-tree-panel app))])
                (menu-item :text "Show REPL" 
                           :key (keystroke "meta 2") 
                           :listen [:action (fn [_] (toggle-repl app))])
                (separator)
                (menu-item :text "Next tab"
                					 :key (keystroke "meta alt RIGHT")
                					 :listen [:action (fn [_] (select-next-tab (app :editor-tabbed-panel)))])
                (menu-item :text "Previous tab"
                					 :key (keystroke "meta alt LEFT")
                					 :listen [:action (fn [_] (select-previous-tab (app :editor-tabbed-panel)))])
                (separator)
                (menu-item :text "Close tab"
                           :key (keystroke "meta W")
                           :listen [:action (fn [_] (close-current-tab app))])]))

(defn make-help-menu
  [app]
  (menu :text "Help"
        :mnemonic "H"
        :items []))

(defn make-sketchpad-menus
  [app-atom]
  (let [app @app-atom]
    (config! 
      (:frame @app-atom) :menubar 
                      (menubar :items [ (make-file-menu app-atom)
                                        (make-edit-menu app)
                                        (make-project-menu app)
                                        (make-source-menu app-atom)
                                        (make-repl-menu app-atom)
                                        (make-view-menu app)
                                        (make-help-menu app)]))))




