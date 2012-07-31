(ns sketchpad.tree.utils
  (:import (java.io File)
           (javax.swing.tree DefaultTreeCellRenderer DefaultMutableTreeNode DefaultTreeModel
                             TreePath TreeSelectionModel)
           (javax.swing JButton JTree JOptionPane JWindow))
  (:use [sketchpad.config.prefs]
        [sketchpad.util.utils])
  (:require [sketchpad.project.state :as project.state]
            [sketchpad.project.project :as project]
            [sketchpad.state :as state]))



(defn update-tree []
  ; (update-project-tree (@tree-app :docs-tree))
  (.reload (.getModel (@state/app :docs-tree))))

(defn save-project-set []
  (write-value-to-prefs sketchpad-prefs "project-set" @project.state/project-set))
    
(defn load-project-set []
  (println (read-value-from-prefs sketchpad-prefs "project-set"))
  (reset! project.state/project-set (into (sorted-set)
                            (read-value-from-prefs sketchpad-prefs "project-set"))))

(defn save-project-map []
  (write-value-to-prefs sketchpad-prefs "project-map" @project.state/project-map))
    
(defn load-project-map []
  (reset! project.state/project-map (into {}
                            (read-value-from-prefs sketchpad-prefs "project-map"))))

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
  :dependencies [[org.clojure/clojure \"1.3.0\"]])"))
      
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

; (defn file-name-choose [app-atom project-dir title]
;   (chooser/choose-file :type :save
;                :dir project-dir
;                :success-fn (fn [fc file] (do 
;                															(spit (File. (.getAbsolutePath file)) "") 
;                                             (new-file-tab! app-atom file project-dir)
;                															(println "Createded file " (.getAbsolutePath file))))))


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

(defn tree-nodes [tree]
  (when-let [root (.. tree getModel getRoot)]
    (tree-seq (complement #(.isLeaf %))
              #(for [i (range (.getChildCount %))] (.getChildAt % i))
              root)))

(defn get-root-path 
([] (get-root-path (@state/app :docs-tree)))
([tree]
  (TreePath. (.. tree getModel getRoot))))

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
  (let [paths (read-value-from-prefs sketchpad-prefs "expanded-paths")]
    (when paths
      (expand-paths tree paths))))

(defn save-tree-selection [tree path]
  (write-value-to-prefs
    sketchpad-prefs "tree-selection"
    (tree-path-to-file path)))

(defn path-to-node [tree path]
  (first
    (for [node (rest (tree-nodes tree))
      :when (= path (try (.. node getUserObject getAbsolutePath)
                      (catch Exception e)))]
      node)))

(defn set-tree-selection 
([path] (set-tree-selection (@state/app :docs-tree) path))
([tree path]
  (awt-event
    (when-let [node (path-to-node tree path)]
      (let [node-path (.getPath node)
            paths (map #(.. % getUserObject getAbsolutePath) (rest node-path))]
        (expand-paths tree paths)
        (when-let [row (row-for-path tree path)]
          (.setSelectionRow tree row)))))))

(defn load-tree-selection [tree]
  (let [path (read-value-from-prefs sketchpad-prefs "tree-selection")]
    (set-tree-selection tree path)))

(defn save-expanded-paths [tree]
  (write-value-to-prefs sketchpad-prefs "expanded-paths" (get-expanded-paths tree)))

;; clooj docs
(defn project-set-to-tree-model []
   (let [model (DefaultTreeModel. (DefaultMutableTreeNode. "projects"))
         root (.getRoot model)]
     (doseq [project (sort-by #(.getName (File. %)) @project.state/project-set)]
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

(defn get-selected-projects 
([] (get-selected-projects @state/app))
([app]
  (let [tree (app :docs-tree)
        selections (.getSelectionPaths tree)]
    (for [selection selections]
      (->> selection .getLastPathComponent (get-project-node tree)
           .getUserObject .getAbsolutePath)))))

(defn update-project-tree [tree]
  (let [model (project-set-to-tree-model)]
    (awt-event
      (.setModel tree model)
      (save-project-set)
      (save-project-map)
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
    (when (confirmed? (str 
                        "Are you sure you want to delete " 
                        path
                        " ?\n"
                        "Deleting cannot be undone.") path)
      (let [f (File. path)]
        (println "Delete file: " f)
        (.delete f))
      (update-project-tree (app :docs-tree)))))

(defn open-project [app]
  (when-let [dir (choose-directory (app :f) "Choose a project directory")]
    (let [project-dir (if (= (.getName dir) "src") (.getParentFile dir) dir)]
      (write-value-to-prefs sketchpad-prefs "last-open-dir" (.getAbsolutePath (.getParentFile project-dir)))
      (project/add-project (.getAbsolutePath project-dir))
      (update-project-tree (:docs-tree app))
      (when-let [clj-file (or (-> (File. project-dir "src")
                                 .getAbsolutePath
                                 (get-code-files ".clj")
                                 first)
                              project-dir)]
        (awt-event (set-tree-selection (app :docs-tree) (.getAbsolutePath clj-file)))))))

(defn new-project [app-atom]
  (let [app @app-atom]
    (try
      (when-let [dir (choose-file (@app-atom :frame) "Create a project directory" nil false)]
        (awt-event
          (let [path (.getAbsolutePath dir)]
            (.mkdirs (File. dir "src"))
            (new-project-clj app dir)
            (project/add-project app path)
            (update-project-tree (:docs-tree app))
            (set-tree-selection (app :docs-tree) path)
            ; (create-file app-atom dir (str (.getName dir) ".core"))
            )))
        (catch Exception e (do (JOptionPane/showMessageDialog nil
                                 "Unable to create project."
                                 "Oops" JOptionPane/ERROR_MESSAGE)
                             (.printStackTrace e))))))

(defn rename-project [app]
  (when-let [dir (choose-file (app :frame) "Move/rename project directory" nil false)]
    (let [old-project (first (get-selected-projects app))]
      (if (.renameTo (File. old-project) dir)
        (do
          (swap! project.state/project-set
                 #(-> % (disj old-project) (conj (.getAbsolutePath dir))))
          (update-project-tree (:docs-tree app)))
        (JOptionPane/showMessageDialog nil "Unable to move project.")))))

(defn remove-selected-project [app]
  (apply swap! project.state/project-set disj (get-selected-projects app))
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