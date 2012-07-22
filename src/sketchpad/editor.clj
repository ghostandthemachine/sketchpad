(ns sketchpad.editor
  (:use [seesaw core graphics color font border meta]
        [sketchpad prefs utils]
        [clojure.pprint]
        [sketchpad.vim-mode]
        [sketchpad.toggle-vim-mode-action]
        [sketchpad tab-manager]
        [rounded-border.core])
  (:require [sketchpad.rsyntax :as rsyntax]
            [sketchpad.tab-ui :as tab-ui]
            [sketchpad.tab :as tab]
            [sketchpad.editor-info-utils :as editor-info-utils]
            [sketchpad.state :as sketchpad.state])
  (:import  (java.awt.event FocusAdapter MouseAdapter KeyAdapter)
           (javax.swing.event ChangeListener)
           (javax.swing UIManager)))

(def app sketchpad.state/app)

(def key-stack (atom []))

(defn command-line-key-listener []
  (let [global-key-listener (proxy [KeyAdapter] []
                              (keyTyped [e]
                                (swap! key-stack (fn [s] (conj e s)))
                                (pprint @key-stack)))]))

(defn dirty-state-listener [app]
  (let [listener (proxy [KeyAdapter] []
                   (keyTyped [e]))]))

(defn set-text-area-preffs [app rta]
  (.addKeyListener rta (dirty-state-listener app))
  (.addKeyListener rta (command-line-key-listener)))

(defn doc-tab [title tip content]
  {:title title
   :tip tip
   :content content})

(defn put [laf k v]
  (.put laf (str "TabbedPane." k) v))

(defn tab-change-handler [app-atom]
  ; (listen (@app-atom :editor-tabbed-panel) :selection editor-info-utils/update-doc-title-label!)
  (listen (@app-atom :editor-tabbed-panel) :selection editor-info-utils/update-doc-position-label!))

(defn editor [app-atom]
  (let [editor-tabbed-panel   (tabbed-panel :placement :top
                                            :overflow :wrap
                                            :background (color :black)
                                            :border (empty-border :thickness 0))]
    (.setUI editor-tabbed-panel (tab-ui/sketchpad-tab-ui editor-tabbed-panel))
    (swap! app-atom conj (gen-map
                           editor-tabbed-panel))
    editor-tabbed-panel))

