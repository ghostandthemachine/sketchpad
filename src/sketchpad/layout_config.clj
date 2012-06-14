(ns sketchpad.layout-config
  (:use [seesaw.core]))
(def show-file-tree (atom true))
(def show-help-panel (atom true))

(defn toggle-file-tree-panel
  ([app] (toggle-file-tree-panel app :tree))
  ([app kw]
  (cond
    (= kw :tree)
    (do 
      (if @show-file-tree
        (do 
          (swap! show-file-tree (fn [_] false))
          (remove! (app :doc-split-pane) (app :docs-tree-panel)))
        (do 
          (swap! show-file-tree (fn [_] true))
          (add! (app :doc-split-pane) (app :docs-tree-panel))
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
  
(def show-editor (atom true))

(defn toggle-editor
  [app]
  (if @show-file-tree
    (do 
      (swap! show-editor (fn [_] false))
      (remove! (app :doc-split-pane) (app :doc-text-panel)))
    (do 
      (swap! show-editor (fn [_] true))
      (add! (app :doc-split-pane) (app :doc-text-panel)))))

(def show-repl (atom true))

(defn toggle-repl
  [app]
  (if @show-file-tree
    (do 
      (swap! show-repl (fn [_] false))
      (remove! (app :split-pane) (app :repl-split-pane)))
    (do 
      (swap! show-repl (fn [_] true))
      (add! (app :split-pane) (app :repl-split-pane)))))

