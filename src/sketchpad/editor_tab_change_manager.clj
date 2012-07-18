(ns sketchpad.editor-tab-change-manager
	(:use [sketchpad tab-manager])
	(:require [sketchpad.menu.file :as file-menu]
		[sketchpad.menu.edit :as edit-menu]
		[sketchpad.menu.view :as view-menu]
		[sketchpad.menu.menu-utils :as menu-utils]
		[seesaw.core :as seesaw]))


(defn apply-menu-update [tabbed-panel]
  (menu-utils/apply-menu-update-functions tabbed-panel file-menu/file-menu-item-state file-menu/file-menu-activation-fns))

(defn attach-tab-change-handler [app-atom editor-tabbed-panel]
  (seesaw/listen editor-tabbed-panel :selection
          (fn [e]
            (apply-menu-update editor-tabbed-panel)
            (let [num-tabs (tab-count editor-tabbed-panel)
                  i (current-tab-index editor-tabbed-panel)]
              (if (> 0 num-tabs)
                (do
                  (swap! app-atom assoc :doc-text-area (current-text-area editor-tabbed-panel))
                  (swap! app-atom (fn [app] (assoc app :current-tab i))))
                (do
                  (swap! app-atom assoc :doc-text-area nil)
                  (swap! app-atom (fn [app] (assoc app :current-tab i)))))))))