(ns sketchpad.menu
  (:use [clojure.pprint]
        [seesaw core keystroke]
        [sketchpad utils edit-menu vim-mode default-mode edit-mode vim-mode layout-config toggle-vim-mode-action filetree completion-builder]
        [clooj repl help project dev-tools indent editor doc-browser search style indent])
  (:require 
        [sketchpad.rtextscrollpane :as sp]
        [sketchpad.rsyntaxtextarea :as cr]
        [sketchpad.rtextarea :as rt]))

(def macro-recording-state (atom false))

(defn toggle-macro-recording!
  [app]
  (if @macro-recording-state
    (do 
      (swap! macro-recording-state (fn [_] false))
      (rt/end-recording-macro! (:doc-text-area app)))
    (do
      (swap! macro-recording-state (fn [_] true))
      (rt/begin-recording-macro! (:doc-text-area app)))))

(defn fold-action
  [ta] 
  (action :name "fold-action"
                         :handler #((cr/code-folding-enabled? ta (not (cr/code-folding-enabled? ta))))))

(defn view-menu 
  [ta]
  (menu :text "View" 
        :items [(checkbox-menu-item :text "Code Folding"
                                    :listen [:action #(fold-action ta)])]))

;;actions

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
                                                  :listen [:action (fn [_] (do 
                                                                              (pprint (app :doc-scroll-pane))
                                                                              (bookmarks-action (app :doc-scroll-pane))))])])
                (menu :text "Project"
                      :items [])]))))

(defn make-file-menu
  [app]
  (menu :text "File"
        :mnemonic "F"
        :items [
                (menu-item :text "New" 
                           :mnemonic "N" 
                           :key (keystroke "meta N") 
                           :listen [:action (fn [_] (create-file app (first (get-selected-projects app)) ""))])
                (menu-item :text "Save" 
                           :mnemonic "S" 
                           :key (keystroke "meta S") 
                           :listen [:action (fn [_] (save-file app))])
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
                               :listen [:action (fn [_] (System/exit 0))])))
                ]))

(defn make-edit-menu
  [app]
  (menu :text "Edit" 
            :mnemonic "E"
        :items [(menu-item :text "Undo" 
                           :mnemonic "U" 
                           :key (keystroke "meta Z") 
                           :listen [:action (fn [_] ())])
                (menu-item :text "Redo" 
                           :mnemonic "Y" 
                           :key (keystroke "meta shift Z") 
                           :listen [:action (fn [_] (rt/redo-last-action (app :doc-text-area)))])
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
  [app]
  (menu :text "Source"
        :mnemonic "U"
        :items [(menu-item :text "Comment-out" 
                           :mnemonic "C" 
                           :key (keystroke "meta SEMICOLON") 
                           :listen [:action (fn [_] (comment-out (:doc-text-area app)))])
                (menu-item :text "Uncomment-out" 
                           :mnemonic "U" 
                           :key (keystroke "meta shift SEMICOLON") 
                           :listen [:action (fn [_] (uncomment-out (:doc-text-area app)))])
                (separator)
                (menu-item :text "Fix indentation" 
                           :mnemonic "F" 
                           :key (keystroke "meta BACK_SLASH") 
                           :listen [:action (fn [_] (fix-indent-selected-lines (:doc-text-area app)))])
                (menu-item :text "Indent lines"
                           :mnemonic "I" 
                           :key (keystroke "meta CLOSE_BRACKET") 
                           :listen [:action (fn [_] (indent (:doc-text-area app)))])
                (menu-item :text "Unindent lines"
                           :mnemonic "D" 
                           :key (keystroke "meta OPEN_BRACKET") 
                           :listen [:action (fn [_] (indent (:doc-text-area app)))])
                (separator)
                (menu-item :text "Find" 
                           :mnemonic "F" 
                           :key (keystroke "meta F") 
                           :listen [:action (fn [_] (start-find app))])
                (menu-item :text "Find next" 
                           :mnemonic "N" 
                           :key (keystroke "meta G") 
                           :listen [:action (fn [_] (highlight-step app false))])
                (menu-item :text "Find prev" 
                           :mnemonic "P" 
                           :key (keystroke "meta shift G") 
                           :listen [:action (fn [_] (highlight-step app true))])
                (menu-item :text "Name search/docs"
                           :mnemonic "S" 
                           :key (keystroke "alt TAB") 
                           :listen [:action (fn [_] (show-tab-help app (find-focused-text-pane app) inc toggle-file-tree-panel))])
                (separator)
                (menu-item :text "Begin recording macro..." 
                           :key (keystroke "ctrl Q") 
                           :listen [:action (fn [_] (toggle-macro-recording! app))])
                (menu-item :text "End recording macro..." 
                           :key (keystroke "ctrl Q") 
                           :listen [:action (fn [_] (toggle-macro-recording! app))])
                (menu-item :text "Playback last macro..." 
                           :key (keystroke "ctrl shift Q") 
                           :listen [:action (fn [_] (rt/playback-last-macro! (:doc-text-area app)))])]))

(defn use-reload-current-file-ns
  [app]
  (if (= "ns" (str (first (current-ns-form app))))
    (do 
      (send-to-repl app (str "(use :reload " \' (str (second (current-ns-form app))) ")"))
      (add-completions-from-ns (quote (second (current-ns-form app))))
      )))

(defn require-reload-current-file-ns
  [app]
  (if (= "ns" (str (first (current-ns-form app))))
    (do 
      (send-to-repl app (str "(require :reload " \' (str (second(current-ns-form app))) ")")))))

(defn make-repl-menu
  [app]
  (menu :text "REPL"
        :mnemonic "R"
        :items [  (menu-item :text "Evaluate form" 
                             :mnemonic "E" 
                             :key (keystroke "meta shift ENTER") 
                             :listen [:action (fn [_] (send-selected-to-repl app))])
                  (menu-item :text "Evaluate entire file" 
                             :mnemonic "F" 
                             :key (keystroke "meta shift E")
                             :listen [:action (fn [_] (send-doc-to-repl app))])
                  (separator)
                  (menu-item :text "Apply file ns"
                             :mnemonic "A"
                             :key (keystroke "meta shift A")
                             :listen [:action  (fn [_] (apply-namespace-to-repl app))])
                  (separator)
                  (menu-item :text "Use :reload file"
                             :mnemonic "U"
                             :key (keystroke "meta shift U")
                             :listen [:action (fn [_] (use-reload-current-file-ns app))])
                  (menu-item :text "Require :reload file"
                             :mnemonic "R"
                             :key (keystroke "meta shift R")
                             :listen [:action (fn [_] (require-reload-current-file-ns app))])
                  (separator)
                  (menu-item :text "Clear output"
                             :mnemonic "P"
                             :key (keystroke "meta P")
                             :listen [:action (fn [_] (.setText (app :repl-out-text-area) ""))])
                  (separator)
                  (menu-item :text "Restart"
                             :mnemonic "R"
                             :key (keystroke "meta R")
                             :listen [:action (fn [_] (restart-repl app (first (get-selected-projects app))))])
                  (separator)
                  (menu-item :text "Print stack trace for last error"
                             :mnemonic "T"
                             :key (keystroke "meta T")
                             :listen [:action (fn [_] (print-stack-trace app))])]))

(defn make-view-menu
  [app]
  (menu :text "View"
        :mnemonic "V"
        :items [(menu-item :text "Go to REPL input" 
                           :mnemonic "R" 
                           :key (keystroke "meta alt 3") 
                           :listen [:action (fn [_] (.requestFocusInWindow (:repl-in-text-area app)))])
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
                (separator)
                (menu-item :text "Folding"
                           :listen [:action (fn [_] (fold-action (app :doc-text-area)))])]))

(defn make-help-menu
  [app]
  (menu :text "Help"
        :mnemonic "H"
        :items []))

(defn make-sketchpad-menus
  [app]
  (config! 
    (:frame app) :menubar 
                    (menubar :items [ (make-file-menu app)
                                      (make-edit-menu app)
                                      (make-project-menu app)
                                      (make-source-menu app)
                                      (make-repl-menu app)
                                      (make-view-menu app)
                                      (make-help-menu app)])))


