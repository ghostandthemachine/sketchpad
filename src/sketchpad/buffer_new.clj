(ns sketchpad.buffer-new
	(:use [seesaw meta])
	(:import 
           (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream))
	(:require [sketchpad.tab-builder :as tab-builder]
			[sketchpad.file :as file]
			[seesaw.core :as seesaw]
			[sketchpad.tab :as tab]
			[sketchpad.tree :as tree]
			[sketchpad.state :as state]
			[leiningen.core.project :as lein-project]
			[clojure.string :as string]))

(defn update-editor-info-file-title [title]
	(swap! (@state/app :doc-title-atom) (fn [lbl] title)))

(defn init-buffer-tab-state [buffer lein-project]
	(tab/focus-buffer buffer)
	(update-editor-info-file-title (tab/title))
	(tab/mark-tab-clean! buffer)
	(.setCaretPosition buffer 0))

(defn selected-file-path []
  (when-let [tree-path (-> @state/app :docs-tree .getSelectionPaths first)]
    (-> tree-path .getLastPathComponent .getUserObject .getAbsolutePath)))

(defn update-buffer-label-from-file [buffer file-path]
	(let [file (File. file-path)]
		(tab/title-at! (tab/index-of-component buffer) (.getName file))))

(defn update-buffer-syntax-style [buffer file-path]
	(seesaw/config! buffer :syntax (file/file-type file-path)))

(defn load-file-into-buffer [buffer file-path]
	(when-let [txt (slurp file-path)]
		(let[rdr (StringReader. txt)
			file (File. file-path)]
			(put-meta! buffer :file file)
			(.read buffer rdr nil)
			(update-buffer-syntax-style buffer file-path)
			(update-buffer-label-from-file buffer file-path))))

(defn buffer-from-file! [file-path project]
	(let [new-buffer (tab-builder/new-tab! (selected-file-path))]
		(load-file-into-buffer new-buffer file-path)
		(init-buffer-tab-state new-buffer project)
		(tab/show-tab! new-buffer)))

(defn blank-clj-buffer! []
	(let [new-buffer (tab-builder/new-tab!)]
		(init-buffer-tab-state new-buffer)
		(tab/show-tab! new-buffer)))

(defn save-new-buffer! [buffer]
	(when-let [new-file (file/save-file-as)]
		(let [new-file-title (.getName new-file)]
		  (when (file/save-file buffer new-file)
		    (put-meta! buffer :file new-file)
		    (put-meta! buffer :new-file false)
		    (tab/title-at! (tab/index-of-component buffer) new-file-title)
		    (tab/mark-current-tab-clean! (@state/app :editor-tabbed-panel))
		    (tree/update-tree)
			(update-editor-info-file-title (tab/title))))))

(defn save-buffer! [buffer]
	(let [file (get-meta buffer :file)
          file-title (.getName file)]
        (file/save-file buffer file)
        (tree/update-tree)
		(update-editor-info-file-title (tab/title))))