; Copyright (c) 2011, Arthur Edelstein
; All rights reserved.
; Eclipse Public License 1.0
; arthuredelstein@gmail.com

(ns overtone-sketchpad.core
    (:use [seesaw core graphics color border]
          [clojure.pprint :only (pprint)]
          [clooj.dev-tools]
          [clooj.highlighting]
          [clooj.repl]
          [clooj.search]
          [clooj.help]
          [clooj.project]
          [clooj.utils]
          [clooj.indent]
          [clooj.style]
          [clooj.navigate])
    (:import 
           (javax.swing JMenuBar)
           (javax.swing.event TreeSelectionListener
                              TreeExpansionListener)
           (javax.swing.tree DefaultMutableTreeNode DefaultTreeModel
                             TreePath TreeSelectionModel)
           (org.fife.ui.rsyntaxtextarea RSyntaxTextArea SyntaxConstants TokenMakerFactory Theme))
    (:require [seesaw.rsyntax :as rsyntax]
              [clojure.java.io :as io]))


(defn text-editor
  [app-atom]
  (let [arglist-label         (label  :foreground     (color :blue)
                                      :id             :arglist-label
                                      :class          :arg-response)
        search-text-area      (text   :id             :search-text-area
                                      :class          :search-area)
        arg-search-panel      (horizontal-panel 
                                      :items          [arglist-label search-text-area]
                                      :id             :arg-search-panel
                                      :class          :search-panel)
        pos-label             (label  :id             :pos-label
                                      :class          :pos-label)
        position-search-panel (horizontal-panel 
                                      :items          [pos-label 
                                                     [:fill-h 10]
                                                       arg-search-panel
                                                      :fill-h]
                                      :maximum-size   [2000 :by 15]
                                      :id             :position-search-panel
                                      :class          :search-panel)
        doc-label             (label  :text           " Source Editor"
                                      :id             :doc-label
                                      :class          :text-editor-comp)
        doc-label-panel       (horizontal-panel       
                                      :items          [doc-label
                                                       :fill-h]
                                      :id             :doc-label-panel
                                      :class          :text-editor-comp)
        doc-text-area         (rsyntax/text-area    
                                      :wrap-lines?    false
                                      :syntax         :clojure
                                      :border         (line-border 
                                                          :thickness 10
                                                          :color (color "#292924" 0))
                                      :id             :doc-text-area
                                      :class          :text-editor-comp)
        doc-scroll-pane       (scrollable              doc-text-area
                                      :id             :doc-scrollable
                                      :class          :text-editor-comp)
        doc-text-panel        (vertical-panel       
                                      :items         [doc-label-panel 
                                                      doc-scroll-pane 
                                                      position-search-panel]
                                      :id             :doc-text-panel
                                      :class          :text-editor-comp)]
    (swap! app-atom conj (gen-map
                            arglist-label
                            search-text-area
                            arg-search-panel
                            pos-label
                            position-search-panel 
                            doc-label
                            doc-text-area
                            doc-scroll-pane
                            doc-text-panel))
    doc-text-panel))

(defn file-tree
  [app-atom]
  (let [docs-tree             (tree   :model          (DefaultTreeModel. nil)
                                      :id             :file-tree
                                      :class          :file-tree)
        docs-tree-scroll-pane (scrollable             docs-tree
                                      :id             :file-tree-scrollable
                                      :class          :file-tree)
        docs-tree-label       (border-panel 
                                      :west           (label " Projects")
                                      :id             :file-tree-label
                                      :class          :file-tree
                                      :size           [200 :by 15]
                                      :vgap           5)
        docs-tree-label-panel (horizontal-panel       
                                      :items          [docs-tree-label
                                                       :fill-h]
                                      :id             :docs-tree-label-panel
                                      :class          :file-tree)
        docs-tree-panel (vertical-panel 
                                      :items          [docs-tree-label-panel
                                                      docs-tree-scroll-pane]
                                      :id             :file-tree-panel
                                      :class          :file-tree)]
    (swap! app-atom conj (gen-map
                            docs-tree
                            docs-tree-scroll-pane
                            docs-tree-label
                            docs-tree-panel))
    docs-tree-panel))

(defn repl
  [app-atom]
  (let [
        repl-label       (border-panel 
                                      :west           (label " Repl")
                                      :id             :file-tree-label
                                      :class          :file-tree
                                      :size           [200 :by 15]
                                      :vgap           5)
        repl-label-panel  (horizontal-panel       
                                      :items          [repl-label
                                                       :fill-h]
                                      :id             :repl-label-panel
                                      :class          :repl)
        repl-out-text-area  (rsyntax/text-area 
                                      :wrap-lines?    false
                                      :editable?      false
                                      :border         (line-border 
                                                          :thickness 5
                                                          :color (color "#292924" 0))
                                      :id             :repl-out-text-area
                                      :class          :repl)
        repl-out-writer   (make-repl-writer repl-out-text-area)
        repl-out-scroll-pane (scrollable repl-out-text-area                                      
                                      :id             :repl-out-scrollable
                                      :class          :repl)
        repl-in-text-area (rsyntax/text-area 
                                      :wrap-lines?    false
                                      :syntax         :clojure
                                      :border         (line-border 
                                                          :thickness 5
                                                          :color (color "#292924" 0))                                      
                                      :id             :repl-in-text-area
                                      :class          :repl)
        repl-split-pane (top-bottom-split             repl-out-scroll-pane 
                                                      repl-in-text-area
                                      :divider-location 0.66
                                      :resize-weight 0.66
                                      :divider-size   3)
        repl-vertical-panel (vertical-panel 
                                      :items          [repl-split-pane]
                                      :id             :repl-vertical-panel
                                      :class          :repl)]
    (swap! app-atom conj (gen-map
                            repl-out-scroll-pane
                            repl-out-text-area
                            repl-in-text-area
                            repl-out-writer
                            repl-split-pane))
    repl-vertical-panel))

(defn doc-nav
  [app-atom]
  (let [completion-label (label       :text           "Name search"
                                      :id             :doc-nav-label
                                      :class          :doc-nav-comp)
        completion-list (listbox      :border         (compound-border "Doc List")
                                      :id             :doc-nav-list
                                      :class          :doc-nav-comp)
        completion-scroll-pane (scrollable            completion-list
                                      :id             :doc-nav-scrollable
                                      :class          :doc-nav-comp)
        completion-panel (vertical-panel 
                                      :items          [completion-label 
                                                      completion-scroll-pane]
                                      :id             :doc-nav-panel
                                      :class          :doc-nav-comp)]
    (swap! app-atom conj (gen-map
                            completion-label
                            completion-list
                            completion-scroll-pane
                            completion-panel))

    completion-panel))

(defn doc-view
  [app-atom]
  (let [help-text-area (rsyntax/text-area  
                                      :wrap-lines?    true
                                      :editable?      false
                                      :background     (color 0xFF 0xFF 0xE8)
                                      :border         (compound-border "Documentation")
                                      :id             :doc-view-text-area
                                      :class          :doc-view-comp)
        help-text-scroll-pane (scrollable             help-text-area
                                      :id             :doc-view-scrollable
                                      :class          :doc-view-comp)]
    (swap! app-atom conj (gen-map
                            help-text-area
                            help-text-scroll-pane))  
    help-text-scroll-pane))
    
(defn create-app []
  (let [app-init  (atom {})
        editor    (text-editor app-init)
        file-tree (file-tree app-init)
        repl      (repl app-init)
        doc-view  (doc-view app-init)
        doc-nav   (doc-nav app-init)
        doc-split-pane (left-right-split
                         file-tree
                         editor
                         :divider-location 0.25
                         :resize-weight 0.25
                         :divider-size 5)
        split-pane (left-right-split 
                        doc-split-pane 
                        repl
                        :divider-location 0.66
                        :resize-weight 0.66
                        :divider-size 5)
        frame (frame 
                :title "Clooj" 
                :width 950 
                :height 700 
                :on-close :exit
                :minimum-size [500 :by 350]
                :content split-pane)
        app (merge {:file      (atom nil)
                    :repl      (atom (create-outside-repl (@app-init :repl-out-writer) nil))
                    :changed   false}
                    @app-init
                    (gen-map
                      frame
                      doc-split-pane
                      split-pane))]
    app))

(defn add-behaviors
  [app]
    ;; docs
    (setup-completion-list (app :completion-list) app)    
    (setup-tab-help app (app :doc-text-area))
    ;;editor
    (setup-autoindent (app :doc-text-area))
    (doto (app :doc-text-area) attach-navigation-keys)
    (double-click-selector (app :doc-text-area))
    (add-caret-listener (app :doc-text-area) #(display-caret-position app))
    (setup-search-text-area app)
    (activate-caret-highlighter app)
    (setup-temp-writer app)
    (attach-action-keys (app :doc-text-area)
      ["cmd1 ENTER" #(send-selected-to-repl app)])
    ;; repl
    (setup-autoindent (app :repl-in-text-area))
    (setup-tab-help app (app :repl-in-text-area))
    (doto (app :repl-in-text-area)
            double-click-selector
            attach-navigation-keys)
    ;; global
    (dorun (map #(attach-global-action-keys % app)
                [(app :docs-tree) 
                 (app :doc-text-area) 
                 (app :repl-in-text-area) 
                 (app :repl-out-text-area) 
                 (.getContentPane (app :frame))])))


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
      ["Choose font..." nil nil #(apply show-font-window
                                        app set-font @current-font)])

    ;; help menu
    (if (is-mac) 
        (add-menu menu-bar "Help" "H"))))


;; startup
(defn startup-overtone [app]
  (Thread/setDefaultUncaughtExceptionHandler
    (proxy [Thread$UncaughtExceptionHandler] []
      (uncaughtException [thread exception]
                       (println thread) (.printStackTrace exception))))
  (add-visibility-shortcut app)
  (make-overtone-menus app)
  (add-repl-input-handler app)
  (doall (map #(add-project app %) (load-project-set)))
  (let [frame (app :frame)]
    (persist-window-shape clooj-prefs "main-window" frame) 
    (on-window-activation frame #(update-project-tree (app :docs-tree))))
  (setup-temp-writer app)
  (load-font app)
  (setup-tree app)
  (let [doc-ta (app :doc-text-area)
        repl-in-ta (app :repl-in-text-area)
        repl-out-ta (app :repl-out-text-area)
        theme (Theme/load (io/input-stream "src/overtone_sketchpad/themes/dark.xml"))]
        (.apply theme doc-ta)
        (.apply theme repl-in-ta)
        (.apply theme repl-out-ta))
  (let [tree (app :docs-tree)]
    (load-expanded-paths tree)
    (load-tree-selection tree))
    (app :frame))

(defonce current-app (atom nil))

(defn -main [& args]
  (reset! embedded false)
  (reset! current-app (create-app))
  (add-behaviors @current-app)
  (invoke-later
    (-> 
      (startup-overtone @current-app) 
      show!)))




