(ns overtone-sketchpad.core
  (:import (javax.swing AbstractListModel BorderFactory JDialog
                        JFrame JLabel JList JMenuBar JOptionPane
                        JPanel JScrollPane JSplitPane JTextArea
                        JTextField JTree KeyStroke SpringLayout JTextPane
                        ListSelectionModel
                        UIManager)
           (javax.swing.event TreeSelectionListener
                              TreeExpansionListener)
           (javax.swing.tree DefaultMutableTreeNode DefaultTreeModel
                             TreePath TreeSelectionModel)
           (java.awt Insets Rectangle Window)
           (java.awt.event AWTEventListener FocusAdapter MouseAdapter
                           WindowAdapter KeyAdapter)
           (java.awt AWTEvent Color Font GridLayout Toolkit)
           (java.net URL)
           (java.util Map)
           (javax.xml.parsers DocumentBuilder)
           (java.io File FileReader StringReader InputStream
                    BufferedWriter OutputStreamWriter FileOutputStream)
           (org.fife.ui.rsyntaxtextarea RSyntaxTextArea SyntaxConstants TokenMakerFactory Theme)  
           (org.fife.ui.rtextarea RTextScrollPane))
   (:use  [seesaw core graphics color]
          [clojure.pprint :only (pprint)]
          [clooj.dev-tools]
          [clooj.brackets]
          [clooj.highlighting]
          [clooj.repl]
          [clooj.search]
          [clooj.help]
          [clooj.project]
          [clooj.utils]
          [clooj.indent]
          [clooj.style]
          [clooj.navigate]
          [overtone-sketchpad.rsyntax])
   (:require [clojure.java.io :as io]
             [upshot.core :as upshot]))

(defn fx-panel 
  []
  (javafx.embed.swing.JFXPanel. ))

(def pad [:fill-v 3])

(def divider-size 2)

(def toggle-doc-tree (atom false))

(defn toggle-doc-tree-visibility [app]
  (if @toggle-doc-tree
    (do 
      (add!  (:doc-split-pane app) (:docs-tree-panel app))
      (config! (:doc-split-pane app) 
                  :resize-weight 0.3
                  :divider-location 0.3
                  :divider-size 4)
      (reset! toggle-doc-tree false))
    (do
      (remove!  (:doc-split-pane app) (:docs-tree-panel app))
      (reset! toggle-doc-tree true))))


(defn create-overtone-app []
  (let [
        arglist-label (label :foreground (color :blue))
        search-text-area (text)
        arg-search-panel (horizontal-panel 
                            :items [arglist-label search-text-area])

        pos-label (label)
        position-search-panel (horizontal-panel 
                                :items [pos-label 
                                        [:fill-h 10]
                                        arg-search-panel
                                        :fill-h]
                                :maximum-size [2000 :by 15])
        
        help-text-area (text-area :wrap-lines? true)
        help-text-scroll-pane (scrollable help-text-area)

        completion-label (label "Name search")
        completion-list (listbox )
        completion-scroll-pane (scrollable completion-list)
        completion-panel (vertical-panel :items [completion-label 
                                                 completion-scroll-pane])

        cp (:content-pane frame)

        doc-label (label "Source Editor")        
        doc-text-area (text-area :wrap-lines? false)
        doc-scroll-pane (make-scroll-pane doc-text-area)
        doc-text-panel (vertical-panel
                          :items [:fill-h
                                  doc-label 
                                  doc-scroll-pane
                                  pad
                                  position-search-panel
                                  pad])

        docs-tree (tree)
        docs-tree-scroll-pane (scrollable 
                                docs-tree)
        docs-tree-label (border-panel 
                          :west (label "Projects")
                          :size [200 :by 15]
                          :vgap 5)

        docs-tree-panel (vertical-panel 
                            :items [docs-tree-label 
                                    docs-tree-scroll-pane])

        toggle-tree     (vertical-panel
                            :items [(button 
                                      :text ">"
                                      :id :toggle-tree
                                      :size [16 :by 16])])


        doc-split-pane (left-right-split
                         docs-tree-panel
                         doc-text-panel
                         :divider-location 0.2
                         :resize-weight 0.2
                         :divider-size 4)

        repl-out-text-area (text-area :wrap-lines?  false)
        repl-out-writer (make-repl-writer repl-out-text-area)
        
        repl-out-scroll-pane (scrollable repl-out-text-area)
        repl-output-vertical-panel (vertical-panel :items [repl-out-scroll-pane])

        repl-in-text-area (text-area :wrap-lines?  false)
        repl-input-vertical-panel (vertical-panel :items [repl-in-text-area])

        repl-split-pane (top-bottom-split 
                            repl-output-vertical-panel 
                            repl-input-vertical-panel
                            :divider-location 0.7
                            :resize-weight 0.7
                            :divider-size divider-size)


        split-pane (top-bottom-split 
                        doc-split-pane
                        repl-split-pane 
                        :divider-location 0.7
                        :divider-size 3
                        :resize-weight 0.7)

        frame (frame 
                :title "Overtone sketch" 
                :width 950 
                :height 700 
                :on-close :exit
                :minimum-size [500 :by 350]
                :content split-pane)

        app (merge {:file (atom nil)
                    :repl (atom (create-outside-repl repl-out-writer nil))
                    :doc-tree-visible? (atom false)
                    :changed false}
                   (gen-map
                     doc-text-area
                     doc-label
                     repl-out-text-area
                     repl-in-text-area
                     frame
                     help-text-area
                     help-text-scroll-pane
                     repl-out-scroll-pane
                     toggle-tree
                     docs-tree
                     docs-tree-scroll-pane
                     docs-tree-panel
                     docs-tree-label
                     search-text-area
                     pos-label
                     repl-out-writer
                     doc-split-pane
                     repl-split-pane
                     split-pane
                     arglist-label
                     completion-list
                     completion-scroll-pane
                     completion-panel))]


    (doto doc-text-area
      attach-navigation-keys)
    
    (setup-completion-list completion-list app)

    
    (double-click-selector doc-text-area)
    
    (doto repl-in-text-area
      double-click-selector
      attach-navigation-keys)

    (.setSyntaxEditingStyle repl-in-text-area
                            SyntaxConstants/SYNTAX_STYLE_CLOJURE)

    (.setModel docs-tree (DefaultTreeModel. nil))

    (setup-search-text-area app)
    
    (add-caret-listener doc-text-area #(display-caret-position app))
    
    (activate-caret-highlighter app)
    
    (setup-temp-writer app)
    
    (attach-action-keys doc-text-area
      ["cmd1 ENTER" #(send-selected-to-repl app)])
    
    (doto repl-out-text-area (.setEditable false))
    
    (doto help-text-area (.setEditable false)
                         (.setBackground (color 0xFF 0xFF 0xE8)))
    
    (setup-autoindent repl-in-text-area)
    
    (setup-tab-help app doc-text-area)
    
    (dorun (map #(attach-global-action-keys % app)
                [docs-tree doc-text-area repl-in-text-area repl-out-text-area (.getContentPane frame)]))
    
    (setup-autoindent doc-text-area)
    app))
;; startup

(defonce current-overtone-app (atom nil))

(defn make-overtone-menus [app]
  (when (is-mac)
    (System/setProperty "apple.laf.useScreenMenuBar" "true"))
  (let [menu-bar (JMenuBar.)]
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
      ; ["Name search/docs" "S" "TAB" #(show-tab-help app (find-focused-text-pane app) inc)]
      ;["Go to definition" "G" "cmd1 D" #(goto-definition (get-file-ns app) app)]
      )
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
      ["File Broswer..." "F" "cmd1 shift 1" #(toggle-doc-tree-visibility app)]
      ["Choose font..." nil nil #(apply show-font-window
                                        app set-font @current-font)])

    ;; help menu
    (add-menu menu-bar "Help" "H"
      ["Browse Docs..." "B" "cmd1 shift B" #()]
      ["Search Docs..." "S" "cmd1 alt S" #()]
      ["Clojure" "C" "" #()]
      ["Overtone" "O" "" #()]

      )
    ))


(defn startup-overtone [current-app]
  (Thread/setDefaultUncaughtExceptionHandler
    (proxy [Thread$UncaughtExceptionHandler] []
      (uncaughtException [thread exception]
                       (println thread) (.printStackTrace exception))))
  (UIManager/setLookAndFeel (UIManager/getSystemLookAndFeelClassName))
  (let [app (create-overtone-app)]
    (reset! current-app app)
    
    (make-overtone-menus app)
    (add-visibility-shortcut app)
    (add-repl-input-handler app)
    ; (setup-tab-help app (app :repl-in-text-area))
    (doall (map #(add-project app %) (load-project-set)))
    (let [frame (app :frame)]
      (persist-window-shape clooj-prefs "main-window" frame) 
      (.setVisible frame true)
      (on-window-activation frame #(update-project-tree (app :docs-tree))))
    (setup-temp-writer app)
    (setup-tree app)
    (let [tree (app :docs-tree)]
      (load-expanded-paths tree)
      (load-tree-selection tree))
    (load-font app)

    (let [doc-ta (app :doc-text-area)
          repl-in-ta (app :repl-in-text-area)
          repl-out-ta (app :repl-out-text-area)
          theme (Theme/load (io/input-stream "src/overtone_sketchpad/dark.xml"))]
          (.apply theme doc-ta)
          (.apply theme repl-in-ta)
          (.apply theme repl-out-ta)

      )))



; (defn -show []
;   (reset! embedded true)
;   (if (not @current-overtone-app)
;     (startup-overtone current-overtone-app)
;     (.setVisible (:frame @current-overtone-app) true)))

(defn -main [& args]
  (reset! embedded false)
  (startup-overtone current-overtone-app))

