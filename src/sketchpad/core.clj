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
  (let [buffer-info (info/buffer-info)
        buffer-tabbed-panel (sketchpad.editor/buffer-tabbed-panel)
        buffer-component {:type :buffer-component
                          :component {:container (vertical-panel :items[(get-in buffer-tabbed-panel [:component :container])
                                                                        :fill-h
                                                                        (get-in buffer-info [:component :container])]
                                                                 :background config/app-color
                                                                 :border (empty-border :thickness 0))}}
        file-tree {:type :file-tree
                   :component (file-tree state/app)}

        repl-tabbed-panel      (app.repl/repl-tabbed-panel)
        repl-info (repl.info/repl-info)
        repl-component (vertical-panel :items[(get-in repl-tabbed-panel [:component :container])
                                                   :fill-h
                                                   (get-in repl-info [:component :container])]
                                            :background config/app-color
                                            :border (empty-border :thickness 0))
        top-horizontal-split-panel (left-right-split
                         (get-in file-tree [:component :container])
                         (get-in buffer-component [:component :container])
                         :divider-location 0.25
                         :resize-weight 0.25
                         :divider-size 3
                         :border (empty-border :thickness 0)
                         :background config/app-color)
        main-vertical-split-pane (top-bottom-split
                     top-horizontal-split-panel
                     repl-component
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
                     :content main-vertical-split-pane)
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
                     file-tree
                     buffer-tabbed-panel
                     buffer-info
                     buffer-component
                     repl-info
                     repl-tabbed-panel
                     repl-component
                     main-vertical-split-pane
                     top-horizontal-split-panel))
        icon-url (clojure.java.io/resource "sketchpad-lambda-logo.png")
        icon (.createImage (Toolkit/getDefaultToolkit) icon-url)]
    (.setIconImage frame icon)
    (set-osx-icon icon)
    app))


(defn add-behaviors
  [app-atom]
  (let [app @app-atom]
    (setup-tree app-atom)

    ;; this should happen when the repl tabbed panel is created probably
    (repl.info/attach-repl-info-handler app-atom)
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
      (on-window-activation frame #(update-project-tree)))
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

