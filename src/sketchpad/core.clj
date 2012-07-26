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
        [sketchpad.tree.tree]
        [sketchpad.tree.utils]
        [sketchpad prefs auto-complete tab utils editor edit-mode default-mode completion-builder rsyntaxtextarea help])
  (:require [sketchpad.theme :as theme]
            [sketchpad.config :as config]
            [sketchpad.repl :as srepl]
            [sketchpad.state :as state]
            [sketchpad.project.project :as project]
            [sketchpad.menu.menu-bar :as menu]
            [sketchpad.editor.info :as info]
            [sketchpad.state :as sketchpad.state]))

(defn set-osx-icon
  [icon]
  (try
    (import 'com.apple.eawt.Application)
    (.setDockIconImage (com.apple.eawt.Application/getApplication) icon)
    (catch Exception e
      false))
  true)

(defn create-app
  []
  (let [;; editor-info MUST init before editor so it is selectable
        editor-info (info/editor-info)
        editor    (editor state/app)
        file-tree (file-tree state/app)
        repl      (srepl/repl state/app)
        doc-info-split-pane (vertical-panel :items[editor
                                                   :fill-h
                                                   editor-info]
                                            :background config/app-color
                                            :border (empty-border :thickness 0))
        doc-split-pane (left-right-split
                         file-tree
                         doc-info-split-pane
                         :divider-location 0.25
                         :resize-weight 0.25
                         :divider-size 3
                         :border (empty-border :thickness 0)
                         :background config/app-color)
        split-pane (top-bottom-split
                     doc-split-pane
                     repl
                     :divider-location 0.66
                     :resize-weight 0.66
                     :divider-size 3
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
                    :current-buffers (atom {})
                    :current-tab -1
                    :repls (atom {})
                    :changed   false
                    :doc-text-area nil
                    :doc-scroll-pane nil}
                   @state/app
                   (gen-map
                     frame
                     doc-split-pane
                     split-pane))
        icon-url (clojure.java.io/resource "sketchpad-icon.png")
        icon (.createImage (Toolkit/getDefaultToolkit) icon-url)]
    (.setIconImage frame icon)
    (set-osx-icon icon)
    (config! doc-split-pane :background (color :black))
    app))


(defn add-behaviors
  [app-atom]
  (let [app @app-atom]
    (setup-tree app-atom)
    ;; global
    (add-visibility-shortcut app)))

(defn init-projects []
  ; (project/add-project "~/")
  (doall (map #(project/add-project %) (load-project-set))))

;; startup
(defn startup-sketchpad [app-atom]
  (let [app @app-atom]
    (Thread/setDefaultUncaughtExceptionHandler
      (proxy [Thread$UncaughtExceptionHandler] []
        (uncaughtException [thread exception]
          (println thread) (.printStackTrace exception))))
    (add-behaviors app-atom)
    (menu/make-menus app-atom)
    
    (init-projects)

    (let [tree (app :docs-tree)]
      (load-expanded-paths tree)
      (load-tree-selection tree))
    (let [frame (app :frame)]
      (persist-window-shape sketchpad-prefs "main-window" frame)
      (on-window-activation frame #(update-project-tree (app :docs-tree))))
    (app :frame)))

(defn show []
  (reset! embedded false)
  (invoke-later
    (reset! sketchpad.state/app (create-app))
    (->
      (startup-sketchpad sketchpad.state/app)
      show!)))

(defn -main [& args]
  (reset! embedded false)
  (invoke-later
    (reset! sketchpad.state/app (create-app))
    (->
      (startup-sketchpad sketchpad.state/app)
      show!)))

