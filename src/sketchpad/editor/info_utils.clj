(ns sketchpad.editor.info-utils
	(:use [seesaw core])
	(:require [sketchpad.state.state :as state]
		[sketchpad.util.tab :as tab]
		[sketchpad.tree.utils :as tree.utils]
		[sketchpad.state.state :as state]))

(defn format-position-str [line column]
  (str "Line " line ", Column " column))

(defn get-coords [text-comp offset]
  (let [row (.getLineOfOffset text-comp offset)
        col (- offset (.getLineStartOffset text-comp row))]
    [row col]))

(defn get-caret-coords [text-comp]
  (get-coords text-comp (.getCaretPosition text-comp)))

(defn update-doc-position-label!
"Update the editor info position label."
[e]
	(if (tab/tabs?)
		(do
			(let [current-text-area (tab/current-text-area)
			      coords (get-caret-coords current-text-area)]
				(swap! (@state/app :doc-position-atom) (fn [_] (format-position-str (first coords) (second coords))))))
		(swap! (@state/app :doc-position-atom) (fn [_] ""))))

(defn update-doc-title-label!
"Update the currently displayed doc title in the info panel"
	[e]
	(invoke-later
		(if (tab/tabs? (.getSource e))
			(do
				(let [doc-title (:doc-title-label @state/app)]
					(when-let [buffer (tab/current-buffer (.getSource e))]
						(config! doc-title :text @(:title buffer))
						(when-let [file @(:file buffer)]
							(tree.utils/set-tree-selection (.getAbsolutePath file))))))
			(do
				(config! (:doc-title-label @state/app) :text "")))))

(defn attach-caret-handler [text-area]
	(listen text-area :caret-update update-doc-position-label!))