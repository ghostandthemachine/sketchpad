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
      (swap! file-tree-divider-position (fn [_] (.getDividerLocation (@app :top-horizontal-split-panel))))
      (swap! show-file-tree (fn [_] false))
      (remove! (@app :top-horizontal-split-panel) (@app :docs-tree-panel)))
    (do 
      (swap! show-file-tree (fn [_] true))
      (.setLeftComponent (@app :top-horizontal-split-panel) (@app :docs-tree-panel))
      (.setDividerLocation (@app :top-horizontal-split-panel) @file-tree-divider-position)
      (.requestFocus (@app :docs-tree) true))))
  
(defonce show-editor (atom true))

(defn toggle-editor
"Toggle if the editor is displayed"
  []
  (if @show-file-tree
    (do 
      (swap! show-editor (fn [_] false))
      (remove! (@app :top-horizontal-split-panel) (@app :doc-text-panel)))
    (do 
      (swap! show-editor (fn [_] true))
      (add! (@app :top-horizontal-split-panel) (@app :doc-text-panel)))))

(defonce show-repl (atom true))
(defonce repl-divider-position (atom nil))

(defn toggle-repl
"Toggle if the repl component is displayed"
  []
  (let [repl-tabbed-panel (get-in (@app :repl-tabbed-panel) [:component :container])]
    (if @show-repl
      (do 
        (reset! show-repl false)
        (swap! repl-divider-position (fn [_] (.getDividerLocation (@app :main-vertical-split-pane))))
        (.remove (@app :main-vertical-split-pane) repl-tabbed-panel))
      (do 
        (reset! show-repl true)
        (.setBottomComponent (@app :main-vertical-split-pane) repl-tabbed-panel)
        (.setDividerLocation (@app :main-vertical-split-pane) @repl-divider-position)
        (.requestFocus repl-tabbed-panel true)))))

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
