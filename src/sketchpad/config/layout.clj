(ns sketchpad.config.layout
  (:use [seesaw.core])
  (:require [sketchpad.state.state :as sketchpad.state]))

(defonce app sketchpad.state.state/app)

(defonce show-file-tree (atom true))
(defonce file-tree-divider-position (atom nil))
(defonce show-help-panel (atom true))

(defn toggle-file-tree-panel
"Toggle if the file tree is displayed"
  []
  (if @show-file-tree
    (do 
      (swap! file-tree-divider-position (fn [_] (.getDividerLocation (@app :doc-split-pane))))
      (swap! show-file-tree (fn [_] false))
      (remove! (@app :doc-split-pane) (@app :docs-tree-panel)))
    (do 
      (swap! show-file-tree (fn [_] true))
      (.setLeftComponent (@app :doc-split-pane) (@app :docs-tree-panel))
      (.setDividerLocation (@app :doc-split-pane) @file-tree-divider-position)
      (.requestFocus (@app :docs-tree) true))))
  
(defonce show-editor (atom true))

(defn toggle-editor
"Toggle if the editor is displayed"
  []
  (if @show-file-tree
    (do 
      (swap! show-editor (fn [_] false))
      (remove! (@app :doc-split-pane) (@app :doc-text-panel)))
    (do 
      (swap! show-editor (fn [_] true))
      (add! (@app :doc-split-pane) (@app :doc-text-panel)))))

(defonce show-repl (atom true))
(defonce repl-divider-position (atom nil))

(defn toggle-repl
"Toggle if the repl component is displayed"
  []
  (if @show-repl
    (do 
      (swap! show-repl (fn [_] false))
      (swap! repl-divider-position (fn [_] (.getDividerLocation (@app :split-pane))))
      (.remove (@app :split-pane) (@app :repl-tabbed-panel)))
    (do 
      (swap! show-repl (fn [_] true))
      (.setBottomComponent (@app :split-pane) (@app :repl-tabbed-panel))
      (.setDividerLocation (@app :split-pane) @repl-divider-position)
      (.requestFocus (@app :repl-tabbed-panel) true))))

(defonce show-search-panel (atom true))

(defn toggle-search
  [app-atom]
  (if @show-search-panel
    (do 
      (swap! show-search-panel (fn [_] false))
      (.remove (@app :info-panel) (@app :search-toolbar)))
    (do 
      (swap! show-search-panel (fn [_] true))
      (.setTopComponent (@app :info-panel) (@app :search-toolbar))
      (.requestFocus (@app :search-toolbar) true)))
      (println "toggle search: " @show-search-panel))
