(ns sketchpad.editor.editor
  (:use [seesaw core graphics color border]
        [sketchpad.config.prefs])
  (:require [sketchpad.editor.ui :as editor.ui]
            [sketchpad.editor.info-utils :as editor.info-utils]
            [sketchpad.state.state :as state])
  (:import (javax.swing UIManager)))

(defn put [laf k v]
  (.put laf (str "TabbedPane." k) v))

(defn tab-change-handler [editor-tabbed-panel]
  (listen editor-tabbed-panel :selection editor.info-utils/update-doc-title-label!)
  (listen editor-tabbed-panel :selection editor.info-utils/update-doc-position-label!))

(defn editor []
  (let [editor-tabbed-panel   (tabbed-panel :placement :top
                                            :overflow :wrap
                                            :background (color :black)
                                            :border (empty-border :thickness 0))]
    (.setUI editor-tabbed-panel (editor.ui/sketchpad-tab-ui editor-tabbed-panel))
    (swap! state/app assoc :editor-tabbed-panel editor-tabbed-panel)
    (tab-change-handler editor-tabbed-panel)
  editor-tabbed-panel))