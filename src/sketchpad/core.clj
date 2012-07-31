(ns sketchpad.core
  (:gen-class :name "Sketchpad")
  (:import (javax.swing.TollTipManager)
           (java.awt Toolkit))
  (:use [seesaw core graphics color border font meta]
        [clojure.pprint]
        [clooj.navigate]
        [clooj.dev-tools]
        [clooj.indent]
        [sketchpad.tree.tree]
        [sketchpad.tree.utils]
        [sketchpad.util.tab]
        [sketchpad.util.utils]
        [sketchpad.config.prefs])
  (:require [sketchpad.wrapper.theme :as theme]
            [sketchpad.config.config :as config]
            [sketchpad.repl.app.repl :as app.repl]
            [sketchpad.state.state :as state]
            [sketchpad.editor.editor :as sketchpad.editor]
            [sketchpad.project.project :as project]
            [sketchpad.menu.menu-bar :as menu]
            [sketchpad.editor.info :as info]
            [sketchpad.repl.info :as repl.info]
            [sketchpad.state.state :as sketchpad.state]))

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
        editor    (sketchpad.editor/editor)
        doc-info-split-pane (vertical-panel :items[editor
                                                   :fill-h
                                                   editor-info]
                                            :background config/app-color
                                            :border (empty-border :thickness 0))
        file-tree (file-tree state/app)
        repl      (app.repl/repl state/app)
        repl-info (repl.info/repl-info)
        repl-info-split-pane (vertical-panel :items[repl
                                                   :fill-h
                                                   repl-info]
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
                     repl-info-split-pane
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
    app))


(defn add-behaviors
  [app-atom]
  (let [app @app-atom]
    (setup-tree app-atom)

    ;; this should happen when the repl tabbed panel is created probably
    (repl.info/attach-repl-info-handler (:repl-tabbed-panel app))
    ;; global
    (add-visibility-shortcut app)))

(defn- get-classpath []
   (sort (map (memfn getPath) 
              (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader))))))

(defn init-projects []
  (project/add-project "tmp")
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
    (config/apply-sketchpad-prefs!)
    (app :frame)))

(defn show []
  (reset! embedded false)
  (invoke-later
    (reset! sketchpad.state.state/app (create-app))
    (->
      (startup-sketchpad sketchpad.state.state/app)
      show!)))

(defn -main [& args]
  (reset! embedded false)
  (invoke-later
    (reset! sketchpad.state.state/app (create-app))
    (->
      (startup-sketchpad sketchpad.state.state/app)
      show!)))

