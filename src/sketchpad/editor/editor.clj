(ns sketchpad.editor.editor
  (:use [seesaw core graphics color border]
        [sketchpad.config.prefs]
        [sketchpad.system.desktop])
  (:require [sketchpad.editor.ui :as editor.ui]
            [sketchpad.tree.popup :as popup]
            [sketchpad.project.project :as sketchpad.project]
            [sketchpad.editor.info-utils :as editor.info-utils]
            [sketchpad.state.state :as state]
            [sketchpad.util.tab :as tab])
  (:import (javax.swing UIManager)))

(defn put [laf k v]
  (.put laf (str "TabbedPane." k) v))

(defn tab-change-handler [buffer-tabbed-panel]

  ; (listen buffer-tabbed-panel :selection 
  ;   (fn [e]
  ;     (let [buffer (tab/current-buffer)]
  ;       (if-let[buffer-project (sketchpad.project/project-by-name (:project buffer))]
  ;         (reset! (:last-active-buffer buffer-project) buffer)))))

  (listen buffer-tabbed-panel :selection editor.info-utils/update-doc-title-label!)
  (listen buffer-tabbed-panel :selection editor.info-utils/update-doc-position-label!))

(defn buffer-tabbed-panel []
  (let [buffer-tabbed-panel   (tabbed-panel :placement :top
                                            :overflow :wrap
                                            :background (color :black)
                                            :border (empty-border :thickness 0))]
    (.setUI buffer-tabbed-panel (editor.ui/sketchpad-tab-ui buffer-tabbed-panel))
    (swap! state/app assoc :buffer-tabbed-panel buffer-tabbed-panel)
    (tab-change-handler buffer-tabbed-panel)
  {:type :buffer-tabbed-panel
   :component {:container buffer-tabbed-panel}}))