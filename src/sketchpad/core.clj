(ns sketchpad.core
  (:gen-class :name "Sketchpad")
  (:import (javax.swing.TollTipManager)
           (org.fife.ui.rsyntaxtextarea.RSyntaTextArea)
           (org.fife.ui.rtextarea.ToolTipSupplier)
           (org.fife.ui.rtextarea.RTextArea)
           (org.fife.ui.autocomplete AutoCompletion FunctionCompletion ParameterizedCompletion)
           (org.fife.ui.autocomplete.ClojureCompletionProvider)
           (org.fife.ui.autocomplete.demo.CCellRenderer)
           (java.io.File)
           (java.util.Vector)
           (java.awt Toolkit)
           (javax.swing UIManager JTabbedPane)
           (javax.swing.plaf.nimbus.NimbusLookAndFeel))
  (:use [seesaw core graphics color border font meta]
        [clojure.pprint]
        [clooj.navigate]
        [clooj.dev-tools]
        [clooj.indent]
        [sketchpad prefs auto-complete tab-manager utils filetree editor edit-mode default-mode completion-builder rsyntaxtextarea help])
  (:require [sketchpad.theme :as theme]
            [sketchpad.config :as config]
            [sketchpad.preview-manager :as pm]
            [sketchpad.repl :as srepl]
            [sketchpad.project-manager :as project]
            [sketchpad.menu.menu-bar :as menu]))

(defn set-laf [laf-string]
  (UIManager/setLookAndFeel laf-string))

(def overtone-handlers
  {:update-caret-position update-caret-position
   :save-caret-position save-caret-position
   :setup-autoindent setup-autoindent
   ; :switch-repl switch-repl
   :get-selected-projects get-selected-projects
   ; :apply-namespace-to-repl apply-namespace-to-repl
   :find-file find-file})

(defn create-app
  []
  (let [app-init  (atom {})
        ;; editor-info MUST init before editor so it is selectable
        ; editor-info (editor-info app-init)
        ; search-toolbar (search-toolbar app-init)
        editor    (editor app-init)

        file-tree (file-tree app-init)
        repl      (srepl/repl app-init)

        ; info-panel (top-bottom-split search-toolbar editor-info)

        doc-split-pane (left-right-split
                         file-tree
                         editor
                         :border (empty-border :thickness 0)
                         :divider-location 0.25
                         :resize-weight 0.25
                         :divider-size 3
                         :background config/app-color)
        ; doc-info-split-pane (top-bottom-split
        ;               doc-split-pane
        ;               info-panel
        ;               ; :divider-location 0.66
        ;               ; :resize-weight 0.66
        ;               :divider-size 1
        ;               :border (empty-border :thickness 0)
        ;               :background config/app-color)

        split-pane (top-bottom-split
                     doc-split-pane
                     repl
                     :divider-location 0.66
                     :resize-weight 0.66
                     :divider-size 1
                     :border (empty-border :thickness 0)
                     :background config/app-color)
        frame (frame :title "Sketchpad"
                     :width 950
                     :height 700
                     :on-close :exit
                     :minimum-size [500 :by 350]
                     :content split-pane)

        app (merge {:current-files (atom {})
                    :current-file (atom nil)
                    :current-tab -1
                    :repls (atom {})
                    :changed   false
                    :doc-text-area nil
                    :doc-scroll-pane nil}
                   @app-init
                   overtone-handlers
                   (gen-map
                     frame
                     doc-split-pane
                     split-pane))]
    (config! doc-split-pane :background (color :black))
    app))


(defn add-behaviors
  [app-atom]
  (let [app @app-atom]
    ;; file tree
    (setup-tree app-atom)
    (listen (app :editor-tabbed-panel) :selection (fn [e]
                                                    (let [cur-tab (cast JTabbedPane (.getSource e))
                                                          rsta (select cur-tab [:#editor])
                                                          current-tab-index (current-tab-index (app :editor-tabbed-panel))]
                                                      (save-tab-selections app))))
    ;; global
    (add-visibility-shortcut app)))

;; startup
(defn startup-sketchpad [app-atom]
  (let [app @app-atom]
    (Thread/setDefaultUncaughtExceptionHandler
      (proxy [Thread$UncaughtExceptionHandler] []
        (uncaughtException [thread exception]
          (println thread) (.printStackTrace exception))))
    (add-behaviors app-atom)
    (menu/make-menus app-atom)
    (project/setup-non-project-map app-atom)
    (doall (map #(project/add-project app %) (load-project-set)))
    (let [frame (app :frame)]
      (persist-window-shape sketchpad-prefs "main-window" frame)
      (on-window-activation frame #(update-project-tree (app :docs-tree))))
    (let [tree (app :docs-tree)]
      (load-expanded-paths tree)
      (load-tree-selection tree))
    (app :frame)))

(defonce current-app (atom nil))

(defn show []
  (reset! embedded false)
  (invoke-later
    (reset! current-app (create-app))
    (->
      (startup-sketchpad current-app)
      show!)))

(defn -main [& args]
  (reset! embedded false)
  (invoke-later
    (reset! current-app (create-app))
    (->
      (startup-sketchpad current-app)
      show!)))


