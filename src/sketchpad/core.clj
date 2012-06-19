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
             (java.util.Vector))
    (:use [seesaw core graphics color border font]
          [clojure.pprint]
          [clooj.help]
          [clooj.navigate]
          [clooj.doc-browser] 
          [clooj.menus]
          [clooj.dev-tools]
          [clooj.indent]
          [sketchpad utils repl filetree editor menu edit-mode default-mode completion-builder])
    (:require [sketchpad.theme :as theme]
    					[sketchpad.config :as config]))

(def overtone-handlers  { :update-caret-position update-caret-position 
                          :save-caret-position save-caret-position
                          :setup-autoindent setup-autoindent
                          :switch-repl switch-repl
                          :get-selected-projects get-selected-projects
                          :apply-namespace-to-repl apply-namespace-to-repl
                          :find-file find-file})

(defn create-completion-provider
  ([] (create-completion-provider :default))
  ([kw]
  (let [cp (org.fife.ui.autocomplete.ClojureCompletionProvider. )]
	  (add-all-ns-completions cp)
    (.setParameterizedCompletionParams cp \space " " \))
     cp)))
     

(defn install-auto-completion
  [rta]
  (let [provider (create-completion-provider)
        auto-complete (org.fife.ui.autocomplete.AutoCompletion. provider)]
    ;; load prefs from config/default.clj
    (config/apply-auto-completion-prefs! config/default-auto-completion-prefs auto-complete)
    (.install auto-complete rta)))

(defn create-app []
  (let [app-init  (atom {})
        editor    (editor app-init)
        file-tree (file-tree app-init)
        repl      (repl app-init)
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
    ;;editor
    (add-caret-listener (app :doc-text-area) #(display-caret-position app))
    (setup-search-text-area app)
    (setup-temp-writer app)

    ; (add-defaults-to-input-map (.getInputMap (app :doc-text-area)))

    ;; install auto completion
    (install-auto-completion (app :doc-text-area))
    (install-auto-completion (app :repl-in-text-area))

    ;; repl
    (add-repl-input-handler app)
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
  ;; add behaviors                       
  (add-behaviors app)
;  (setup-text-area-font app)
;  (set-text-area-preffs app)

  ;; create menus
  (make-sketchpad-menus app)
  ;; load projects
  (doall (map #(add-project app %) (load-project-set)))
  (let [frame (app :frame)]
    (persist-window-shape clooj-prefs "main-window" frame) 
    (on-window-activation frame #(update-project-tree (app :docs-tree))))
  (let [tree (app :docs-tree)]
    (load-expanded-paths tree)
    (load-tree-selection tree))
  	;; load default prefs
	  (config/apply-editor-prefs! config/default-editor-prefs (:doc-text-area app))
		;; done with init
    (app :frame))

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
      (startup-overtone @current-app) 
      show!))))

(defn -main [& args]
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
      (startup-overtone @current-app)
      show!))))

