(ns sketchpad.editor.buffer
	(:use [seesaw meta])
	(:import 
           (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream))
	(:require [sketchpad.editor.build :as editor.build]
			[sketchpad.file.file :as file]
			[seesaw.core :as seesaw]
			[sketchpad.util.tab :as tab]
			[sketchpad.project.project :as sketchpad.project]
			[sketchpad.state.state :as state]
			[sketchpad.tree.utils :as tree.utils]
			[leiningen.core.project :as lein-project]
			[clojure.string :as string]
			[seesaw.bind :as bind]
			[seesaw.core :as seesaw]))

(defn update-buffer-info-file-title [title]
	(swap! (@state/app :doc-title-atom) (fn [lbl] title)))

(defn init-buffer-tab-state [buffer]
	(let [text-area (:text-area buffer)]
	  (tab/focus-buffer buffer)
	  (update-buffer-info-file-title (tab/title))
	  (tab/mark-tab-clean! buffer)
	  (.discardAllEdits text-area)
	  (.setCaretPosition text-area 0)))

(defn selected-file-path []
  (when-let [tree-path (-> @state/app :docs-tree .getSelectionPaths first)]
    (-> tree-path .getLastPathComponent .getUserObject .getAbsolutePath)))

(defn update-buffer-label-from-file [buffer file-path]
	(let [file (File. file-path)]
		(swap! (:title buffer) (fn [_] (.getName file)))
		(seesaw/config! (get-in buffer [:tab :label]) :text (.getName file))
		(tab/buffer-title! buffer (.getName file))))

(defn update-buffer-syntax-style [buffer file-path]
	(seesaw/config! buffer :syntax (file/file-type file-path)))

(defn load-file-into-buffer [project buffer file-path]
	(when-let [txt (slurp file-path)]
		(let[rdr (StringReader. txt)
			file (File. file-path)
			text-area (:text-area buffer)]
		(.read text-area rdr nil)
		(update-buffer-syntax-style text-area file-path)
		(update-buffer-label-from-file buffer file-path)
		(swap! (:title buffer) (fn [_] (.getName file)))
		(swap! (:file buffer) (fn [_] file)))))

(defn open-buffer [file-path project-path]
	(let [project (sketchpad.project/project-from-path project-path)
		  buffer (editor.build/project-buffer-tab project-path)]
		(load-file-into-buffer project buffer file-path)
		(init-buffer-tab-state buffer)
		(sketchpad.project/add-buffer-to-project project-path buffer)
		(sketchpad.project/add-buffer-to-app buffer)
		(tab/show-buffer buffer)))

(defn blank-clj-buffer! 
	([parent-dir] 
	(let [buffer (editor.build/scratch-buffer-tab "tmp")]
		(init-buffer-tab-state buffer)
		(sketchpad.project/add-buffer-to-app buffer)
		(tab/show-buffer buffer))))

