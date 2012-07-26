(ns sketchpad.editor.editor
  (:use [seesaw core graphics color font border meta]
        [sketchpad prefs utils]
        [clojure.pprint]
        [sketchpad.vim-mode]
        [sketchpad.toggle-vim-mode-action]
        [rounded-border.core])
  (:require [sketchpad.rsyntax :as rsyntax]
            [sketchpad.editor.ui :as editor.ui]
            [sketchpad.editor.info-utils :as editor.info-utils]
            [sketchpad.state :as state])
  (:import  (java.awt.event FocusAdapter MouseAdapter KeyAdapter)
           (javax.swing.event ChangeListener)
           (javax.swing UIManager)))

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
    (.setUI editor-tabbed-panel (editor.ui/sketchpad-editor.ui editor-tabbed-panel))
    (swap! state/app conj (gen-map
                           editor-tabbed-panel))
    (tab-change-handler editor-tabbed-panel)
    editor-tabbed-panel))