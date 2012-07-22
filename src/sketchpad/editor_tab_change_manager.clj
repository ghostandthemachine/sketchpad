(ns sketchpad.editor-tab-change-manager
	(:use [sketchpad tab])
	(:require [sketchpad.menu.file :as file-menu]
    [sketchpad.tab :as tab]
		[seesaw.core :as seesaw]
    [sketchpad.state :as sketchpad.state]))

(def handler-app sketchpad.state/app)

(defn attach-tab-change-handler [app-atom]
  (seesaw/listen (@app-atom :editor-tabbed-panel) :selection
          (fn [e]
            (let [num-tabs (tab/tab-count)
                  i (tab/current-tab-index)]
                (println "call tab change handler num tabs: " num-tabs)
              (when (> num-tabs 0)
                (println "udpte with: " (tab/title))
                  )))))