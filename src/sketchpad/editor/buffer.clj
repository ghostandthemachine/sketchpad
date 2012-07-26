(ns sketchpad.editor.buffer
	(:use [seesaw meta])
	(:import 
           (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream))
	(:require [sketchpad.editor.build :as editor.build]
			[sketchpad.file.file :as file]
			[seesaw.core :as seesaw]
			[sketchpad.tab :as tab]
			[sketchpad.tree.utils :as tree.utils]
			[sketchpad.project.project :as sketchpad.project]
			[sketchpad.state :as state]
			[sketchpad.tree.utils :as tree.utils]
			[leiningen.core.project :as lein-project]
			[clojure.string :as string]
			[seesaw.bind :as bind]
			[seesaw.core :as seesaw]))

(defn update-editor-info-file-title [title]
	(swap! (@state/app :doc-title-atom) (fn [lbl] title)))

(defn init-buffer-tab-state [buffer]
	(let [text-area (:text-area buffer)]
	  (tab/focus-buffer buffer)
	  (update-editor-info-file-title (tab/title))
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

(defn buffer-from-file! [file-path project-path]
	(let [project (sketchpad.project/project-from-path project-path)
		  buffer (editor.build/project-buffer-tab project-path)]
		  (clojure.pprint/pprint project)
		(load-file-into-buffer project buffer file-path)
		(init-buffer-tab-state buffer)
		(sketchpad.project/add-buffer-to-project project-path buffer)
		(sketchpad.project/add-buffer-to-app buffer)
		(tab/show-buffer buffer)))

(defn blank-clj-buffer! []
	(let [buffer (editor.build/scratch-buffer-tab (tree.utils/get-root-path))]
		(init-buffer-tab-state buffer)
		(sketchpad.project/add-buffer-to-app buffer)
		(tab/show-buffer buffer)))

(defn save-new-buffer! [buffer]
	(when-let [new-file (file/save-file-as!)]
		(let [new-file-title (.getName new-file)]
		  (when (file/save-file! buffer new-file)
		    (assoc (:file buffer) new-file)
		    (tab/title-at! (tab/index-of-buffer buffer) new-file-title)
		    (tab/mark-current-tab-clean! (@state/app :editor-tabbed-panel))
		    (tree.utils/update-tree)
			(update-editor-info-file-title (tab/title))))))

(defn save-buffer! [buffer]
	(let [file @(:file buffer)
          file-title (.getName file)]
        (file/save-file! buffer file)
        (tree.utils/update-tree)
		(update-editor-info-file-title (tab/title))))

