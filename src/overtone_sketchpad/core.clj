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
           	  (java.awt AWTEvent Color Font GridLayout Toolkit)
           	  (org.fife.ui.rsyntaxtextarea RSyntaxTextArea SyntaxConstants TokenMakerFactory)	
              (org.fife.ui.rtextarea RTextScrollPane)
	)
	(:use [clooj.core :exclude (-main -show)]
	   	[clooj.brackets]
        [clooj.highlighting]
        [clooj.repl]
        [clooj.search]
        [clooj.help :only (arglist-from-caret-pos show-tab-help setup-tab-help
                           setup-completion-list help-handle-caret-move
                           find-focused-text-pane  
                           token-from-caret-pos)]
        [clooj.project :only (add-project load-tree-selection
                              load-expanded-paths load-project-set
                              save-expanded-paths
                              save-tree-selection get-temp-file
                              get-selected-projects
                              get-selected-file-path
                              remove-selected-project update-project-tree
                              rename-project set-tree-selection
                              get-code-files get-selected-namespace)]
        [clooj.utils :only (clooj-prefs write-value-to-prefs read-value-from-prefs
                            is-mac count-while add-text-change-listener
                            set-selection scroll-to-pos add-caret-listener
                            attach-child-action-keys attach-action-keys
                            get-caret-coords add-menu
                            add-menu-item
                            choose-file choose-directory
                            comment-out uncomment-out
                            indent unindent awt-event persist-window-shape
                            confirmed? create-button is-win
                            get-keystroke printstream-to-writer
                            focus-in-text-component
                            scroll-to-caret when-lets
                            constrain-to-parent make-split-pane
                            gen-map on-click
                            remove-text-change-listeners get-text-str 
                            scroll-to-line get-directories)]
        [clooj.indent :only (setup-autoindent fix-indent-selected-lines)]
        [clooj.style :only (get-monospaced-fonts show-font-window)]
        [clooj.navigate :only (attach-navigation-keys)]
		[seesaw.core]))


(defn create-overtone-app []
  (let [
        arglist-label (create-arglist-label)
        
        search-text-area (text :size [200 :by 15])
        pos-label (label)
        position-search-panel (border-panel :hgap 15 :west pos-label :center search-text-area)

        doc-label (label "Source Editor")        
        doc-text-area (make-text-area false)
        doc-scroll-pane (make-scroll-pane doc-text-area)
        ; doc-tabbed-pane (vertical-panel doc-scroll-pane doc-label)
        doc-text-panel (vertical-panel :items [doc-label doc-scroll-pane position-search-panel arglist-label])
        
        help-text-area (make-text-area true)
        help-text-scroll-pane (scrollable help-text-area)

        completion-label (label "Name search")
        completion-list (JList.)
        completion-scroll-pane (scrollable completion-list)
        completion-panel (vertical-panel :items [completion-label completion-scroll-pane])

        cp (:content-pane frame)

        docs-tree (tree)
        docs-tree-scroll-pane (scrollable docs-tree)
        docs-tree-label (border-panel 
                          :west (label "Projects")
                          :size [200 :by 15]
                          :vgap 5)
        docs-tree-panel (vertical-panel :items [docs-tree-label docs-tree-scroll-pane])

        doc-split-pane (left-right-split
                         docs-tree-panel
                         doc-text-panel)

        repl-out-text-area (make-text-area false)
        repl-out-writer (make-repl-writer repl-out-text-area)
        
        repl-out-scroll-pane (scrollable repl-out-text-area)
        repl-label (label "Clojure REPL output")
        repl-output-vertical-panel (vertical-panel :items [repl-out-scroll-pane repl-label])

        repl-in-text-area (make-text-area false)
        repl-input-label (label "Clojure REPL input \u2191")
        repl-input-vertical-panel (vertical-panel :items [repl-in-text-area repl-input-label])

        repl-split-pane (top-bottom-split repl-output-vertical-panel repl-input-vertical-panel)
                
        split-pane (top-bottom-split doc-split-pane repl-split-pane)

        frame (frame 
                :title "Overtone sketch" 
                :width 950 
                :height 700 
                :minimum-size [500 :by 350]
                :content split-pane)

        app (merge {:file (atom nil)
                    :repl (atom (create-outside-repl repl-out-writer nil))
                    :changed false}
                   (gen-map
                     doc-text-area
                     doc-label
                     repl-out-text-area
                     repl-in-text-area
                     repl-label
                     frame
                     help-text-area
                     help-text-scroll-pane
                     repl-out-scroll-pane
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
    
    (doto pos-label
      (.setFont (Font. "Courier" Font/PLAIN 13)))
    
    (double-click-selector doc-text-area)
    
    (doto repl-in-text-area
      double-click-selector
      attach-navigation-keys)

    (.setSyntaxEditingStyle repl-in-text-area
                            SyntaxConstants/SYNTAX_STYLE_CLOJURE)

    (.setModel docs-tree (DefaultTreeModel. nil))

    (exit-if-closed frame)

    (setup-search-text-area app)
    
    (add-caret-listener doc-text-area #(display-caret-position app))
    
    (activate-caret-highlighter app)
    
    (setup-temp-writer app)
    
    (attach-action-keys doc-text-area
      ["cmd1 ENTER" #(send-selected-to-repl app)])
    
    (doto repl-out-text-area (.setEditable false))
    
    (doto help-text-area (.setEditable false)
                         (.setBackground (Color. 0xFF 0xFF 0xE8)))
    
    (setup-autoindent repl-in-text-area)
    
    (setup-tab-help app doc-text-area)
    
    (dorun (map #(attach-global-action-keys % app)
                [docs-tree doc-text-area repl-in-text-area repl-out-text-area (.getContentPane frame)]))
    
    (setup-autoindent doc-text-area)
    app))
;; startup

(defonce current-overtone-app (atom nil))

(defn startup-overtone-sketchpad[]
  (Thread/setDefaultUncaughtExceptionHandler
    (proxy [Thread$UncaughtExceptionHandler] []
      (uncaughtException [thread exception]
                       (println thread) (.printStackTrace exception))))
  (UIManager/setLookAndFeel (UIManager/getSystemLookAndFeelClassName))
  (let [app (create-overtone-app)]
    (reset! current-overtone-app app)
    (make-menus app)
    (add-visibility-shortcut app)
    (add-repl-input-handler app)
    (setup-tab-help app (app :repl-in-text-area))
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
    (load-font app)))

(defn -show-overtone-sketchpad []
  (reset! embedded true)
  (if (not @current-app)
    (startup-overtone-sketchpad)
    (.setVisible (:frame @current-app) true)))

(defn -main [& args]
  (reset! embedded false)
  (startup-overtone-sketchpad))

