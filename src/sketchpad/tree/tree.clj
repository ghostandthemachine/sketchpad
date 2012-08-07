(ns sketchpad.tree.tree
  (:use [seesaw core keystroke border meta]
        [sketchpad.util.utils]
        [sketchpad.config.prefs]
        [sketchpad.tree.utils]
        [clojure.pprint])
  (:require [seesaw.color :as c]
            [seesaw.chooser :as chooser]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [sketchpad.config.config :as config]
            [sketchpad.file.file :as file]
            [sketchpad.project.state :as project-state]
            [sketchpad.editor.buffer :as editor.buffer]
            [sketchpad.tree.popup :as popup]
            [sketchpad.state.state :as state]
            [sketchpad.buffer.action :as buffer.action]
            [leiningen.core.project :as lein-project])
  (:import 
         (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
         (java.awt GridLayout)
         (javax.swing JButton JTree JOptionPane JWindow SwingUtilities)
         (javax.swing.event TreeSelectionListener
                            TreeExpansionListener)
         (java.awt.event MouseAdapter MouseListener)
         (javax.swing.tree DefaultTreeCellRenderer DefaultMutableTreeNode DefaultTreeModel
                           TreePath TreeSelectionModel)
         (org.fife.ui.rsyntaxtextarea SyntaxConstants RSyntaxDocument)))

(defn popup-trigger?
[e]
(.isPopupTrigger e))

(defn double-click?
[e]
(= (.getClickCount e) 2))

(defn handle-filetree-double-click
[e app]
(let [tree (:docs-tree app)
      path (.getPathForLocation tree (.getX e) (.getY e))]
  (awt-event
    (save-tree-selection tree path))))

(defn handle-single-click [row path]
  (.setSelectionRow (@state/app :docs-tree) row))

(defn handle-double-click [row path]
(try 
  (let [file (.. path getLastPathComponent getUserObject)
        proj (.getPathComponent path 1)
        project-str (str (buffer.action/trim-parens (last (string/split (.toString proj) #"   "))))]
    (when (file/text-file? file) ;; handle if dir is selected instead of file
      (do 
        (editor.buffer/open-buffer (get-selected-file-path @state/app) project-str)
        (save-tree-selection tree path))))
  (catch java.lang.NullPointerException e)))

(defn click-handler [e]
  (let [tree (get-in (@state/app :file-tree) [:component :tree])
        sel-row (.getRowForLocation tree (.getX e) (.getY e))
        sel-path (.getPathForLocation tree (.getX e) (.getY e))
        click-count (.getClickCount e)]
  (.setSelectionPath tree sel-path)
  (if-not (SwingUtilities/isRightMouseButton e)
    (do
      (cond
        (= click-count 1)
          (handle-single-click sel-row sel-path)
        (= click-count 2)
          (handle-double-click sel-row sel-path)))
    (do
      (if-not (nil? (.getLastSelectedPathComponent tree))
        (let [row (.getClosestRowForLocation tree (.getX e) (.getY e))
              tree-path (.getPathForLocation tree (.getX e) (.getY e))
              file-path (-> tree-path .getLastPathComponent .getUserObject .getAbsolutePath)
              file? (.isFile (java.io.File. file-path))]
          (if file?
            (do
              (.show (popup/file-popup file-path) (.getComponent e) (.getX e) (.getY e)))
            (do
              (let [project-dir? (contains? @(:projects @state/app) file-path)]
                (if project-dir?
                  (.show (popup/project-popup file-path) (.getComponent e) (.getX e) (.getY e))
                  (.show (popup/directory-popup file-path) (.getComponent e) (.getX e) (.getY e)))))))
        (do
          (.clearSelection tree)
          (.show (popup/no-selection-popup) (.getComponent e) (.getX e) (.getY e))))))))

(defn tree-listener
[app-atom]
(let [app @app-atom
      listener (proxy [MouseAdapter] []
                  (mousePressed [e] )
                  (mouseClicked [e]
                    (invoke-later (click-handler e))))]
      listener))


(defn tree-model [app]
(let [model (DefaultTreeModel. nil)]
  model))

(defn setup-tree [app-atom]
(let [app @app-atom
      tree (get-in (:file-tree app) [:component :tree])
      save #(save-expanded-paths tree)]
  (doto tree
    (.setRootVisible false)
    (.setShowsRootHandles true)
    (.. getSelectionModel (setSelectionMode TreeSelectionModel/CONTIGUOUS_TREE_SELECTION))
    (.addTreeExpansionListener
      (reify TreeExpansionListener
        (treeCollapsed [this e] (save))
        (treeExpanded [this e] (save))))

   (.addTreeSelectionListener
     (reify TreeSelectionListener
       (valueChanged [this e]
         (awt-event
           (save-tree-selection tree (.getNewLeadSelectionPath e))))))
  (.addMouseListener (tree-listener app-atom)))))

(defn file-tree
[app-atom]
(let [docs-tree             (tree   :model          (tree-model @app-atom)
                                    :id             :file-tree
                                    :class          :file-tree
                                    :background config/file-tree-bg)
      docs-tree-scroll-pane (scrollable             docs-tree
                                    :id             :file-tree-scrollable
                                    :class          :file-tree
                                    :background config/file-tree-bg)
      docs-tree-label       (horizontal-panel 
                                    :items          [(label :text "Projects"
                                                           :foreground config/file-tree-fg
                                                           :border (empty-border :thickness 5))]
                                    :id             :file-tree-label
                                    :class          :file-tree
                                    :background config/file-tree-bg)
      docs-tree-label-panel (horizontal-panel       
                                    :items          [docs-tree-label
                                                     :fill-h]
                                    :id             :docs-tree-label-panel
                                    :class          :file-tree
                                    :background config/file-tree-bg)
      docs-tree-panel (vertical-panel 
                                    :items          [docs-tree-label-panel
                                                    docs-tree-scroll-pane]
                                    :id             :file-tree-panel
                                    :class          :file-tree
                                    :background config/file-tree-bg)]
  (let [cell-renderer (cast DefaultTreeCellRenderer (.getCellRenderer docs-tree))]
  (.setBackgroundNonSelectionColor cell-renderer config/file-tree-bg))
  (project-state/add-projects-to-app app-atom)
  (config/apply-file-tree-scroller-prefs! docs-tree-scroll-pane)
  ; (config/apply-file-tree-prefs! docs-tree)
  (swap! app-atom conj (gen-map
                          docs-tree
                          docs-tree-scroll-pane
                          docs-tree-label
                          docs-tree-panel))
  {:type :file-tree-component
   :container docs-tree-panel
   :tree docs-tree
   :label docs-tree-label}))