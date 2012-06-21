(ns sketchpad.filetree
    (:use [seesaw core keystroke border]
          [sketchpad utils editor]
          [clojure.pprint])
    (:require [seesaw.color :as c]
              [seesaw.chooser :as chooser]
              [sketchpad.config :as config])
    (:import (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
           (java.awt GridLayout)
           (javax.swing JButton JTree JOptionPane JWindow)
           (javax.swing.event TreeSelectionListener
                              TreeExpansionListener)
           (java.awt.event MouseAdapter MouseListener)
           (javax.swing.tree DefaultTreeCellRenderer DefaultMutableTreeNode DefaultTreeModel
                             TreePath TreeSelectionModel)
           (org.fife.ui.rsyntaxtextarea SyntaxConstants RSyntaxDocument)))

(def project-set (atom (sorted-set)))
;; setup

(defn add-project [app project-path]
  (swap! project-set conj project-path))

(defn save-project-set []
  (write-value-to-prefs clooj-prefs "project-set" @project-set))
    
(defn load-project-set []
  (reset! project-set (into (sorted-set)
                            (read-value-from-prefs clooj-prefs "project-set"))))

(defn get-project-root [path]
  (let [f (File. path)
        name (.getName f)]
    (if (and (or (= name "src")
                 (= name "lib"))
             (.isDirectory f))
      (File. (.getParent f))
      f)))

(defn get-code-files [dir suffix]
  (let [dir (File. dir)]
    (sort (filter #(.endsWith (.getName %) suffix)
                  (file-seq dir)))))

(defn path-to-namespace [src-dir file-path]
  (let [drop-suffix #(apply str (drop-last 4 %))]
    (-> file-path
        (.replace (str src-dir File/separator) "")
        drop-suffix
        (.replace File/separator "."))))

(defn file-node [text file-path] 
  (proxy [File] [file-path]
    (toString []
      (let [mark (if (.exists (get-temp-file this)) "*" "")]
        (str mark text mark)))))

(defn file-tree-node [^File f]
  (proxy [DefaultMutableTreeNode] [f]
    (isLeaf [] (not (.isDirectory f)))))

(defn add-node [parent node-str file-path]
  (let [node  (file-tree-node
                (file-node node-str file-path))]
    (.add parent node)

    node))

(defn add-file-tree [root-file-node]
  (doseq [f (filter #(let [name (.getName %)]
                       (not (or (.startsWith name ".")
                                (.endsWith name "~"))))
                    (sort (.. root-file-node getUserObject listFiles)))]
    (let [node (add-node root-file-node (.getName f) (.getAbsolutePath f))]
      (add-file-tree node))))

(def project-clj-text (.trim
"
(defproject PROJECTNAME \"1.0.0-SNAPSHOT\"
  :description \"FIXME: write\"
  :dependencies [[org.clojure/clojure \"1.3.0\"]])
"))
      
(defn specify-source [project-dir title default-namespace]
  (when-let [namespace (JOptionPane/showInputDialog nil
                         "Please enter a fully-qualified namespace"
                         title
                         JOptionPane/QUESTION_MESSAGE
                         nil
                         nil
                         default-namespace)]
    (let [tokens (map munge (.split namespace "\\."))
          dirs (cons "src" (butlast tokens))
          dirstring (apply str (interpose File/separator dirs))
          name (last tokens)
          the-dir (File. project-dir dirstring)]
      (.mkdirs the-dir)
      [(File. the-dir (str name ".clj")) namespace])))

(defn file-name-choose [project-dir title]
  (chooser/choose-file :type :save
               :dir project-dir
               :success-fn (fn [fc file] (do 
               															(spit (File. (.getAbsolutePath file)) "") 
               															(println "Createded file " (.getAbsolutePath file))))))


(defn new-project-clj [app project-dir]
  (let [project-name (.getName project-dir)
        file-text (.replace project-clj-text "PROJECTNAME" project-name)]
    (spit (File. project-dir "project.clj") file-text)))

(defn- dir-rank [dir]
  (get {"src" 0 "test" 1 "lib" 2} (.getName dir) 100))

(defn find-file [project-path relative-file-path]
  (let [classpath-dirs (sort-by dir-rank < (get-directories (File. project-path)))
        file-candidates (map 
                          #(File. (str (.getAbsolutePath %) File/separatorChar relative-file-path)) 
                          classpath-dirs)]
    (first (filter #(and (.exists %) (.isFile %)) file-candidates))))


(defn file-suffix [^File f]
  (when-lets [name (.getName f)
             last-dot (.lastIndexOf name ".")
             suffix (.substring name (inc last-dot))]
    suffix))

(defn text-file? [f]
  (not (some #{(file-suffix f)}
             ["jar" "class" "dll" "jpg" "png" "bmp"])))

(defn tree-nodes [tree]
  (when-let [root (.. tree getModel getRoot)]
    (tree-seq (complement #(.isLeaf %))
              #(for [i (range (.getChildCount %))] (.getChildAt % i))
              root)))

(defn get-root-path [tree]
  (TreePath. (.. tree getModel getRoot)))

(defn tree-path-to-file [^TreePath tree-path]
  (when tree-path
    (try (.. tree-path getLastPathComponent getUserObject getAbsolutePath)
         (catch Exception e nil))))

(defn get-row-path [tree row]
  (tree-path-to-file (. tree getPathForRow row)))

(defn get-expanded-paths [tree]
  (for [i (range (.getRowCount tree)) :when (.isExpanded tree i)]
    (get-row-path tree i)))

(defn row-for-path [tree path]
  (first
    (for [i (range 1 (.getRowCount tree))
          :when (= path
                   (-> tree (.getPathForRow i)
                            .getPath last .getUserObject .getAbsolutePath))]
      i)))

(defn expand-paths [tree paths]
  (doseq [i (range) :while (< i (.getRowCount tree))]
    (when-let [x (some #{(tree-path-to-file (. tree getPathForRow i))} paths)]
      (.expandPath tree (. tree getPathForRow i)))))

(defn load-expanded-paths [tree]
  (let [paths (read-value-from-prefs clooj-prefs "expanded-paths")]
    (when paths
      (expand-paths tree paths))))

(defn save-tree-selection [tree path]
  (write-value-to-prefs
    clooj-prefs "tree-selection"
    (tree-path-to-file path)))
  
(defn path-to-node [tree path]
  (first
    (for [node (rest (tree-nodes tree))
      :when (= path (try (.. node getUserObject getAbsolutePath)
                      (catch Exception e)))]
      node)))

(defn set-tree-selection [tree path]
  (awt-event
    (when-let [node (path-to-node tree path)]
      (let [node-path (.getPath node)
            paths (map #(.. % getUserObject getAbsolutePath) (rest node-path))]
        (expand-paths tree paths)
        (when-let [row (row-for-path tree path)]
          (.setSelectionRow tree row))))))

(defn load-tree-selection [tree]
  (let [path (read-value-from-prefs clooj-prefs "tree-selection")]
    (set-tree-selection tree path)))

(defn save-expanded-paths [tree]
  (write-value-to-prefs clooj-prefs "expanded-paths" (get-expanded-paths tree)))

;; clooj docs
(defn project-set-to-tree-model []
   (let [model (DefaultTreeModel. (DefaultMutableTreeNode. "projects"))
         root (.getRoot model)]
     (doseq [project (sort-by #(.getName (File. %)) @project-set)]
       (add-file-tree (add-node root
                                (str (-> project File. .getName) 
                                     "   (" project ")")
                                project)))
     model))

(defn get-project-node [tree node]
  (let [parent-node (.getParent node)]
    (if (= parent-node
           (.getLastPathComponent (get-root-path tree)))
      node
      (get-project-node tree (.getParent node)))))

(defn get-node-path [node]
  (.. node getUserObject getAbsolutePath))

(defn get-selected-file-path [app]
  (when-let [tree-path (-> app :docs-tree .getSelectionPaths first)]
    (-> tree-path .getLastPathComponent .getUserObject .getAbsolutePath)))

(defn get-selected-namespace [tree]
  (->> tree .getSelectionPaths first
       .getLastPathComponent .getUserObject .toString))

(defn get-selected-projects [app]
  (let [tree (app :docs-tree)
        selections (.getSelectionPaths tree)]
    (for [selection selections]
      (->> selection .getLastPathComponent (get-project-node tree)
           .getUserObject .getAbsolutePath))))

(defn save-file [app]
  (try
    (let [f @(app :current-file)
          ft (File. (str (.getAbsolutePath f) "~"))]
      (with-open [writer (BufferedWriter.
                           (OutputStreamWriter.
                             (FileOutputStream. f)
                             "UTF-8"))]
        (.write (app :doc-text-area) writer))
      (send-off temp-file-manager (fn [_] 0))
      (.delete ft)
      (.updateUI (app :docs-tree)))
    (catch Exception e (JOptionPane/showMessageDialog
                         nil "Unable to save file."
                         "Oops" JOptionPane/ERROR_MESSAGE))))
(defn update-project-tree [tree]
  (let [model (project-set-to-tree-model)]
    (awt-event
      (.setModel tree model)
      (save-project-set)
      (load-expanded-paths tree)
      (load-tree-selection tree)
      (save-expanded-paths tree))))
  
(defn rename-file [app]
  (when-let [old-file @(app :current-file)]
    (let [tree (app :docs-tree)
          [file namespace] (specify-source
                             (first (get-selected-projects app))
                             "Rename a source file"
                             (get-selected-namespace tree))]
      (when file
        (.renameTo @(app :current-file) file)
        (update-project-tree (:docs-tree app))
        (awt-event (set-tree-selection tree (.getAbsolutePath file)))))))

(defn delete-file [app]
  (let [path (get-selected-file-path app)]
    (when (confirmed? "Are you sure you want to delete this file?\nDeleting cannot be undone." path)
      (loop [f (File. path)]
        (when (and (empty? (.listFiles f))
                   (let [p (-> f .getParentFile .getAbsolutePath)]
                     (or (.contains p (str File/separator "src" File/separator))
                         (.endsWith p (str File/separator "src")))))
          (.delete f)
          (recur (.getParentFile f))))
      (update-project-tree (app :docs-tree)))))

(defn create-file [app project-dir default-namespace]
   (when-let [file-name (file-name-choose project-dir "Create a new file")]
    ))

; (defn create-file [app project-dir default-namespace]
;    (when-let [[file namespace] (specify-source project-dir
;                                           "Create a new file"
;                                           default-namespace)]
;      (let [tree (:docs-tree app)]
;        (spit file (str "(ns " namespace ")\n"))
;        (update-project-tree (:docs-tree app))
;        (set-tree-selection tree (.getAbsolutePath file)))))

(defn open-project [app]
  (when-let [dir (choose-directory (app :f) "Choose a project directory")]
    (let [project-dir (if (= (.getName dir) "src") (.getParentFile dir) dir)]
      (write-value-to-prefs clooj-prefs "last-open-dir" (.getAbsolutePath (.getParentFile project-dir)))
      (add-project app (.getAbsolutePath project-dir))
      (update-project-tree (:docs-tree app))
      (when-let [clj-file (or (-> (File. project-dir "src")
                                 .getAbsolutePath
                                 (get-code-files ".clj")
                                 first)
                              project-dir)]
        (awt-event (set-tree-selection (app :docs-tree) (.getAbsolutePath clj-file)))))))

(defn new-project [app]
  (try
    (when-let [dir (choose-file (app :frame) "Create a project directory" "" false)]
      (awt-event
        (let [path (.getAbsolutePath dir)]
          (.mkdirs (File. dir "src"))
          (new-project-clj app dir)
          (add-project app path)
          (update-project-tree (:docs-tree app))
          (set-tree-selection (app :docs-tree) path)
          (create-file app dir (str (.getName dir) ".core")))))
      (catch Exception e (do (JOptionPane/showMessageDialog nil
                               "Unable to create project."
                               "Oops" JOptionPane/ERROR_MESSAGE)
                           (.printStackTrace e)))))

(defn rename-project [app]
  (when-let [dir (choose-file (app :frame) "Move/rename project directory" "" false)]
    (let [old-project (first (get-selected-projects app))]
      (if (.renameTo (File. old-project) dir)
        (do
          (swap! project-set
                 #(-> % (disj old-project) (conj (.getAbsolutePath dir))))
          (update-project-tree (:docs-tree app)))
        (JOptionPane/showMessageDialog nil "Unable to move project.")))))

(defn remove-selected-project [app]
  (apply swap! project-set disj (get-selected-projects app))
  (update-project-tree (app :docs-tree)))  

(defn remove-project [app]
  (when (confirmed? "Remove the project from list? (No files will be deleted.)"
                    "Remove project")
    (remove-selected-project app)))

(defn revert-file [app]
  (when-let [f @(:file app)]
    (let [temp-file (get-temp-file f)]
      (when (.exists temp-file))
        (let [path (.getAbsolutePath f)]
          (when (confirmed? "Revert the file? This cannot be undone." path)
            (.delete temp-file)
            (update-project-tree (:docs-tree app))
            (restart-doc app f))))))

(defn save-file-state
  [app] 
  )

(defn popup-trigger?
  [e]
  (.isPopupTrigger e))

(defn double-click?
  [e]
  (= (.getClickCount e) 2))

(defn handle-filtree-popup
  [e app]
  )

(defn handle-filetree-double-click
  [e app]
;  (save-tree-selection (app :docs-tree) (.getClosestPathForLocation (app :docs-tree) (.getX e) (.getY e)))
;  (let [f (.. e getPath getLastPathComponent getUserObject)]
;		(when (and
;			(not= f @(app :current-file))
;			(text-file? f))
;			(restart-doc app f))
;  )
  )

(defn handle-single-click [row path app])

(defn handle-double-click [row path app])
  
(defn handle-right-click [row path app]
  (.setSelectionRow (app :docs-tree) row))

(defn tree-listener
  [app]
  (let [tree (app :docs-tree)
        listener (proxy [MouseAdapter] []
                    (mousePressed [e]
                      (let [sel-row (.getRowForLocation tree (.getX e) (.getY e))
                            sel-path (.getPathForLocation tree (.getX e) (.getY e))
                            click-count (.getClickCount e)]
                        (cond
                          ;; handle single click 
                          (= click-count 1)
                          	(if (.isMetaDown e)
                          		;; handle right click
                          		(handle-right-click sel-row sel-path app)
                          		;; handle single left click
                            	(handle-single-click sel-row sel-path app))
                          ;; handle double click
                          (= click-count 2)
                            (handle-double-click sel-row sel-path app)

                          )))
                    (mouseClicked [e]
                    	(pprint (.getPathForLocation tree (.getX e) (.getY e)))
                      (if (double-click? e)
                        (handle-filetree-double-click e app)
                       )))]
        listener))


(defn tree-model [app]
  (let [model (DefaultTreeModel. nil)]
    ; (doto 
    ;   model
    ;   (.addTreeModelListener (tree-listener app)))
    model))

(defn make-filetree-popup
  [app]
  (popup 
    :id :filetree-popup
    :class :popup
    :items [(menu-item :text "New File" 
                      :listen [:action (fn [_] (create-file app (first (get-selected-projects app)) ""))])
            (menu-item :text "New Folder" 
                      ; :listen [:action (fn [_] (create-folder app))]
                      )
            (separator)
            (menu-item :text "New..." 
                      :mnemonic "N" 
                      :key (keystroke "meta shift N") 
                      :listen [:action (fn [_] (new-project app))])
            (menu-item :text "Open..." 
                      :mnemonic "O" 
                      :key (keystroke "meta shift O") 
                      :listen [:action (fn [_] (open-project app))])
            (separator)
            (menu-item :text "Remove" 
                      :mnemonic "M" 
                      :listen [:action (fn [_] (remove-project app))])
            (menu-item :text "Rename Project" 
                      :listen [:action (fn [_] (rename-project app))])
            (separator)
            (menu-item :text "Move/Rename" 
                      :listen [:action (fn [_] (rename-file app))])
            (separator)
            (menu-item :text "Delete" 
                      :listen [:action (fn [_] (delete-file app))])]))

; (defn dirty? [tree-path app]
;   (if (>= 5 (.getPathCount tree-path))
;     @()
;   )

(defn setup-tree [app]
  (let [tree (:docs-tree app)
        save #(save-expanded-paths tree)]
    (config! tree :popup (make-filetree-popup app))
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
             (save-tree-selection tree (.getNewLeadSelectionPath e))
             ; (println (.toString (.getNewLeadSelectionPath e)))

             ; (if (dirty? @(app :current-file-path))
             ;    ;; store this text in a buffer
             ;    )
             
             (let [f (.. e getPath getLastPathComponent
                           getUserObject)]
               (when (and
                       (not= f @(app :current-file))
                       (text-file? f))
                  ; (if (contains? ))
                  ; (swap! @(app :current-files) (fn [_] (.getNewLeadSelectionPath e)))
                  (restart-doc app f)
                  ; (update-editor-content app f)
                  ))))))
    (.addMouseListener (tree-listener app))
    )))


; { :path nil
;   :file-name nil
;   :dirty false
;   :open false
;   :buffer nil}

;; view


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
                                      ; :size           [200 :by 15]
                                      ; :vgap           5
                                      :background config/file-tree-bg)
        docs-tree-label-panel (horizontal-panel       
                                      :items          [docs-tree-label
                                                       :fill-h]
                                      :id             :docs-tree-label-panel
                                      :class          :file-tree
                                      :background config/file-tree-bg
                                        )
        docs-tree-panel (vertical-panel 
                                      :items          [docs-tree-label-panel
                                                      docs-tree-scroll-pane]
                                      :id             :file-tree-panel
                                      :class          :file-tree
                                      :background config/file-tree-bg)]

    (.setBackground (.getHorizontalScrollBar docs-tree-scroll-pane) config/file-tree-bg)

    (let [cell-renderer (cast DefaultTreeCellRenderer (.getCellRenderer docs-tree))]
      (.setBackgroundNonSelectionColor cell-renderer config/file-tree-bg)
      )

    (swap! app-atom conj (gen-map
                            docs-tree
                            docs-tree-scroll-pane
                            docs-tree-label
                            docs-tree-panel))
    docs-tree-panel))