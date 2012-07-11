(ns sketchpad.layout-config
  (:use [seesaw.core]))
(defonce show-file-tree (atom true))
(defonce file-tree-divider-position (atom nil))
(defonce show-help-panel (atom true))

(defn toggle-file-tree-panel
  ([app] (toggle-file-tree-panel app :tree))
  ([app kw]
  (cond
    (= kw :tree)
    (do 
      (if @show-file-tree
        (do 
          (swap! file-tree-divider-position (fn [_] (.getDividerLocation (app :doc-split-pane))))
          (swap! show-file-tree (fn [_] false))
          (remove! (app :doc-split-pane) (app :docs-tree-panel)))
        (do 
          (swap! show-file-tree (fn [_] true))
          (.setLeftComponent (app :doc-split-pane) (app :docs-tree-panel))
          (.setDividerLocation (app :doc-split-pane) @file-tree-divider-position)
          (.requestFocus (app :docs-tree) true))))
    (= kw :help)
    (do 
      (if @show-help-panel
        (do 
          (swap! show-help-panel (fn [_] false))
          (remove! (app :doc-split-pane) (app :docs-tree-panel)))
        (do 
          (swap! show-help-panel (fn [_] true))
          (add! (app :doc-split-pane) (app :docs-tree-panel))))))))
  
(defonce show-editor (atom true))

(defn toggle-editor
  [app]
  (if @show-file-tree
    (do 
      (swap! show-editor (fn [_] false))
      (remove! (app :doc-split-pane) (app :doc-text-panel)))
    (do 
      (swap! show-editor (fn [_] true))
      (add! (app :doc-split-pane) (app :doc-text-panel)))))

(defonce show-repl (atom true))
(defonce repl-divider-position (atom nil))

(defn toggle-repl
  [app]
  (if @show-repl
    (do 
      (swap! show-repl (fn [_] false))
      (swap! repl-divider-position (fn [_] (.getDividerLocation (app :split-pane))))
      (.remove (app :split-pane) (app :repl-tabbed-panel)))
    (do 
      (swap! show-repl (fn [_] true))
      (.setBottomComponent (app :split-pane) (app :repl-tabbed-panel))
      (.setDividerLocation (app :split-pane) @repl-divider-position)
      (.requestFocus (app :repl-tabbed-panel) true))))

(defonce show-search-panel (atom true))

(defn toggle-search
  [app-atom]
  (if @show-search-panel
    (do 
      (swap! show-search-panel (fn [_] false))
      (.remove (@app-atom :info-panel) (@app-atom :search-toolbar)))
    (do 
      (swap! show-search-panel (fn [_] true))
      (.setTopComponent (@app-atom :info-panel) (@app-atom :search-toolbar))
      (.requestFocus (@app-atom :search-toolbar) true)))
      (println "toggle search: " @show-search-panel))
