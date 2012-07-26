(ns sketchpad.editor.info-utils
	(:use [seesaw core])
	(:require [sketchpad.state :as state]
		[sketchpad.tab :as tab]))

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
			(when-let [current-text-area (tab/current-text-area)]
				(let [coords (get-caret-coords current-text-area)]
					(swap! (@state/app :doc-position-atom) (fn [_] (format-position-str (first coords) (second coords)))))))
		(swap! (@state/app :doc-position-atom) (fn [_] ""))))

(defn update-doc-title-label!
"Update the currently displayed doc title in the info panel"
[e]
	(if (tab/tabs?)
		(if-let [title (tab/title)]
  			(swap! (@state/app :doc-title-atom) (fn [_] title)))
			(swap! (@state/app :doc-title-atom) (fn [_] ""))))

(defn attach-caret-handler [text-area]
	(listen text-area :caret-update update-doc-position-label!))