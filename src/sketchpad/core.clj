(ns sketchpad.core
    (:gen-class :name "Sketchpad")
    (:use [seesaw core graphics color border font]
          [clojure.pprint]
          [clooj.repl] 
          [clooj.help]
          [clooj.utils]
          [clooj.navigate]
          [clooj.doc-browser] 
          [clooj.filetree]
          [clooj.menus]
          [clooj.dev-tools]
          [clooj.indent]
          [sketchpad.editor]
          [sketchpad.menu]
          [sketchpad.edit-mode])
    (:require [sketchpad.theme :as theme]))

(def overtone-handlers  { :update-caret-position update-caret-position 
                          :save-caret-position save-caret-position
                          :setup-autoindent setup-autoindent
                          :switch-repl switch-repl
                          :get-selected-projects get-selected-projects
                          :apply-namespace-to-repl apply-namespace-to-repl
                          :find-file find-file})

(defn create-app []
  (let [app-init  (atom {})
        editor    (editor app-init)
        file-tree (file-tree app-init)
        repl      (repl app-init)
        doc-view  (doc-view app-init)
        doc-nav   (doc-nav app-init)
        doc-split-pane (left-right-split
                         file-tree
                         editor
                         :divider-location 0.25
                         :resize-weight 0.25
                         :divider-size 3)
        split-pane (left-right-split 
                        doc-split-pane 
                        repl
                        :divider-location 0.66
                        :resize-weight 0.66
                        :divider-size 3)
        frame (frame 
                :title "Sketchpad" 
                :width 950 
                :height 700 
                :on-close :exit
                :minimum-size [500 :by 350]
                :content split-pane)
        app (merge {:file      (atom nil)
                    :repl      (atom (create-outside-repl (@app-init :repl-out-writer) nil))
                    :changed   false}
                    @app-init
                    overtone-handlers
                    (gen-map
                      frame
                      doc-split-pane
                      split-pane))]
    app))

(defn add-behaviors
  [app]
    ;; docs
    (setup-completion-list (app :completion-list) app)    
    (setup-tab-help app (app :doc-text-area))
    ;;editor
    (setup-autoindent (app :doc-text-area))
    (doto (app :doc-text-area) attach-navigation-keys)
    (double-click-selector (app :doc-text-area))
    (add-caret-listener (app :doc-text-area) #(display-caret-position app))
    (setup-search-text-area app)
    (setup-cmd-line-area app)
    (setup-temp-writer app)
    (attach-action-keys (app :doc-text-area)
      ["cmd1 ENTER" #(send-selected-to-repl app)])

    ; (gutter-popup (app :doc-scroll-pane))
    (setup-text-area-font app)
    (set-text-area-preffs app)

    ;; repl
    (setup-autoindent (app :repl-in-text-area))
    (setup-tab-help app (app :repl-in-text-area))
    (add-repl-input-handler app)
    (doto (app :repl-in-text-area)
            double-click-selector
            attach-navigation-keys)
    ;; file tree
    (setup-tree app)
    ;; global
    (add-visibility-shortcut app)
    (dorun (map #(attach-global-action-keys % app)
                [(app :docs-tree) 
                 (app :doc-text-area) 
                 (app :repl-in-text-area) 
                 (app :repl-out-text-area) 
                 (.getContentPane (app :frame))])))

;; startup
(defn startup-overtone [app]
  (Thread/setDefaultUncaughtExceptionHandler
    (proxy [Thread$UncaughtExceptionHandler] []
      (uncaughtException [thread exception]
                       (println thread) (.printStackTrace exception))))

  (doall (map #(add-project app %) (load-project-set)))
  (let [frame (app :frame)]
    (persist-window-shape clooj-prefs "main-window" frame) 
    (on-window-activation frame #(update-project-tree (app :docs-tree))))
  ;; set theme
  (let [doc-ta (app :doc-text-area)
        repl-in-ta (app :repl-in-text-area)
        repl-out-ta (app :repl-out-text-area)
        theme (theme/theme "src/sketchpad/themes/dark.xml")]
      (theme/apply! theme doc-ta)
      (theme/apply! theme repl-in-ta)
      (theme/apply! theme repl-out-ta))
  (let [tree (app :docs-tree)]
    (load-expanded-paths tree)
    (load-tree-selection tree))
    (app :frame))

(defonce current-app (atom nil))

(defn show []
  (reset! embedded false)
  (reset! current-app (create-app))
  (add-behaviors @current-app)
  ; (config! (@current-app :frame) :on-close #())
  (make-sketchpad-menus @current-app)
  (invoke-later
    (-> 
      (startup-overtone @current-app) 
      show!)))

(defn -main [& args]
  (reset! embedded false)
  (reset! current-app (create-app))
  (add-behaviors @current-app)
  (make-sketchpad-menus @current-app)
  (invoke-later
    (-> 
      (startup-overtone @current-app) 
      show!)))



