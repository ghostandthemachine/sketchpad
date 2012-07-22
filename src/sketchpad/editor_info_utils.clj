(ns sketchpad.editor-info-utils
	(:use [seesaw core])
	(:require [sketchpad.state :as sketchpad.state]
		[sketchpad.tab :as tab]))

(def info-app sketchpad.state/app)

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
([e]
	(when (tab/tabs?)
		(when-let [current-buffer (tab/current-buffer)]
			(let [coords (get-caret-coords current-buffer)]
				(swap! (@info-app :doc-position-atom) (fn [_] (format-position-str (first coords) (second coords)))))))))

(defn update-doc-title-label!
"Update the currently displayed doc title in the info panel"
([e]
	(when (tab/tabs?)
		(if-let [title (tab/title-at (tab/current-buffer (@info-app :editor-tabbed-panel)))]
  			(swap! (@info-app :doc-title-atom) (fn [_] title))))))

(defn attach-caret-handler [rsta lbl]
	(listen rsta :caret-update update-doc-position-label!))