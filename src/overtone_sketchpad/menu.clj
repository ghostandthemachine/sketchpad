(ns overtone-sketchpad.menu
  (:use [clojure.pprint]
        [seesaw core]
        [clooj repl utils project dev-tools indent editor filetree doc-browser search style indent])
  (:require 
        [overtone-sketchpad.rsyntaxtextarea :as cr]
        [overtone-sketchpad.rtextarea :as rt]))

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

(defn make-sketchpad-menus [app]
  (when-not (contains? app :menus)
    (when (is-mac)
      (System/setProperty "apple.laf.useScreenMenuBar" "true"))
    (let [menu-bar (menubar)]
      (. (app :frame) setJMenuBar menu-bar)
      (let [file-menu
            (add-menu menu-bar "File" "F"
              ["New" "N" "cmd1 N" #(create-file app (first (get-selected-projects app)) "")]
              ["Save" "S" "cmd1 S" #(save-file app)]
              ["Move/Rename" "M" nil #(rename-file app)]
              ["Revert" "R" nil #(revert-file app)]
              ["Delete" nil nil #(delete-file app)])]
        (when-not (is-mac)
          (add-menu-item file-menu "Exit" "X" nil #(System/exit 0))))
      (add-menu menu-bar "Edit" "E"
        ["Undo" "U" "cmd1 Z" #()]
        ["Redo" "Y" "cmd1 shift Z" #(rt/redo-last-action (app :doc-text-area))]
        ["Copy" "C" "cmd1 C" #(cr/copy-as-rtf (app :doc-text-area))]
        ["Paste" "P" "cmd1 V" #(rt/paste (:doc-text-area app))])
      (add-menu menu-bar "Project" "P"
        ["New..." "N" "cmd1 shift N" #(new-project app)]
        ["Open..." "O" "cmd1 shift O" #(open-project app)]
        ["Move/Rename" "M" nil #(rename-project app)]
        ["Remove" nil nil #(remove-project app)])
      (add-menu menu-bar "Source" "U"
        ["Comment-out" "C" "cmd1 SEMICOLON" #(comment-out (:doc-text-area app))]
        ["Uncomment-out" "U" "cmd1 shift SEMICOLON" #(uncomment-out (:doc-text-area app))]
        ["Fix indentation" "F" "cmd1 BACK_SLASH" #(fix-indent-selected-lines (:doc-text-area app))]
        ["Indent lines" "I" "cmd1 CLOSE_BRACKET" #(indent (:doc-text-area app))]
        ["Unindent lines" "D" "cmd1 OPEN_BRACKET" #(indent (:doc-text-area app))]
        ["Name search/docs" "S" "TAB" #(show-tab-help app (find-focused-text-pane app) inc)])
      (add-menu menu-bar "Tools" "T"
        ["Begin recording macro..." nil "ctrl Q" #(toggle-macro-recording! app)]
        ["End recording macro..." nil "ctrl Q" #(toggle-macro-recording! app)]
        ["Playback last macro..." nil "ctrl shift Q" #(rt/playback-last-macro! (:doc-text-area app))])
      (add-menu menu-bar "REPL" "R"
        ["Evaluate here" "E" "cmd1 ENTER" #(send-selected-to-repl app)]
        ["Evaluate entire file" "F" "cmd1 E" #(send-doc-to-repl app)]
        ["Apply file ns" "A" "cmd1 shift A" #(apply-namespace-to-repl app)]
        ["Clear output" "C" "cmd1 K" #(.setText (app :repl-out-text-area) "")]
        ["Restart" "R" "cmd1 R" #(restart-repl app
                              (first (get-selected-projects app)))]
        ["Print stack trace for last error" "T" "cmd1 T" #(print-stack-trace app)])
      (add-menu menu-bar "Search" "S"
        ["Find" "F" "cmd1 F" #(start-find app)]
        ["Find next" "N" "cmd1 G" #(highlight-step app false)]
        ["Find prev" "P" "cmd1 shift G" #(highlight-step app true)])
      (add-menu menu-bar "Window" "W"
        ["Go to REPL input" "R" "cmd1 3" #(.requestFocusInWindow (:repl-in-text-area app))]
        ["Go to Editor" "E" "cmd1 2" #(.requestFocusInWindow (:doc-text-area app))]
        ["Go to Project Tree" "P" "cmd1 1" #(.requestFocusInWindow (:docs-tree app))]
        ["Increase font size" nil "cmd1 PLUS" #(grow-font app)]
        ["Decrease font size" nil "cmd1 MINUS" #(shrink-font app)]
        ["Choose font..." nil nil #(apply show-font-window
                                          app set-font @current-font)])
      (when (is-mac) (add-menu menu-bar "Help" "H")))))