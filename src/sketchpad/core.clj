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
          [sketchpad editor-info prefs auto-complete tab-manager utils repl filetree editor menu edit-mode default-mode completion-builder rsyntaxtextarea help])
    (:require [sketchpad.theme :as theme]
    					[sketchpad.config :as config]
              [sketchpad.preview-manager :as pm]))

(defn set-laf [laf-string]
  (UIManager/setLookAndFeel laf-string))

(def overtone-handlers  { :update-caret-position update-caret-position 
                          :save-caret-position save-caret-position
                          :setup-autoindent setup-autoindent
                          :switch-repl switch-repl
                          :get-selected-projects get-selected-projects
                          :apply-namespace-to-repl apply-namespace-to-repl
                          :find-file find-file})

(defn create-app []
  (let [app-init  (atom {})
        ;; editor-info MUST init before editor so it is selectable
        editor-info (editor-info app-init)
        editor    (editor app-init)

        file-tree (file-tree app-init)
        repl      (repl app-init)
        
        editor-panel (vertical-panel :border nil :items [editor editor-info])
        doc-split-pane (left-right-split
                         file-tree
                         editor-panel
                         :border (empty-border :thickness 0)
                         :divider-location 0.25
                         :resize-weight 0.25
                         :divider-size 3
                         :background config/app-color)
        split-pane (top-bottom-split 
                        doc-split-pane 
                        repl
                        :divider-location 0.66
                        :resize-weight 0.66
                        :divider-size 1
                        :border (empty-border :thickness 0)
                        :background config/app-color)
        app (merge {:current-files (atom {})
                    :current-file (atom nil)
                    :current-tab -1
                    :repl      (atom (create-outside-repl (@app-init :repl-out-writer) nil))
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
    ;;editor
    ; (setup-search-text-area app)
    ; (setup-temp-writer app)
    ;; init preview manager
    ; (pm/make-preview app-atom)
    ;; repl
    (add-repl-input-handler app)
    ;; file tree
    (setup-tree app-atom)

    (listen (app :editor-tabbed-panel) :selection (fn [e] 
                                                  (let [cur-tab (cast JTabbedPane (.getSource e))
                                                        rsta (select cur-tab [:#editor])
                                                        current-tab-index (current-tab-index (app :editor-tabbed-panel))]
                                                    (save-tab-selections app)
                                                    )))
    ;; global
    (add-visibility-shortcut app)
    ; (dorun (map #(attach-global-action-keys % app)
    ;             [(app :docs-tree) 
    ;              (app :doc-text-area) 
    ;              (app :repl-in-text-area) 
    ;              ; (app :repl-out-text-area) 
    ;              (.getContentPane (app :frame))]))
    ))

;; startup
(defn startup-sketchpad [app-atom]
  (let [app @app-atom]    
    (Thread/setDefaultUncaughtExceptionHandler
      (proxy [Thread$UncaughtExceptionHandler] []
        (uncaughtException [thread exception]
                         (println thread) (.printStackTrace exception))))
    ;; add behaviors                       
    (add-behaviors app-atom)
    ; (set-text-area-preffs app)


    ; (pm/make-preview app-atom)

    ;; create menus
    (make-sketchpad-menus app)
    ;; load projects
    (doall (map #(add-project app %) (load-project-set)))
    (let [frame (app :frame)]
      (persist-window-shape clooj-prefs "main-window" frame) 
      (on-window-activation frame #(update-project-tree (app :docs-tree))))
    (let [tree (app :docs-tree)]
      (load-expanded-paths tree)
      (load-tree-selection tree)
      )

      ; (load-tab-selections app-atom)
    	;; load default prefs
      (config/apply-editor-prefs! config/default-editor-prefs (:repl-in-text-area app))

  		;; done with init
      (app :frame)))

(defonce current-app (atom nil))

(defn show []
  (reset! embedded false)
  (reset! current-app (create-app))
  (let [frame (frame :title "Sketchpad" 
                     :width 950 
                     :height 700 
                     :minimum-size [500 :by 350]
                     :content (@current-app :split-pane))]
  (swap! current-app (fn [app] (assoc app :frame frame)))
  (invoke-later
    (-> 
      (startup-sketchpad current-app) 
      show!))))

(defn -main [& args]
  ; (set-laf "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel")
  (reset! embedded false)
  (reset! current-app (create-app))
  (let [frame (frame :title "Sketchpad" 
                     :width 950 
                     :height 700 
                     :on-close :exit
                     :minimum-size [500 :by 350]
                     :content (@current-app :split-pane))]
  (swap! current-app (fn [app] (assoc app :frame frame)))
  (invoke-later
    (-> 
      (startup-sketchpad current-app)
      show!))))

  