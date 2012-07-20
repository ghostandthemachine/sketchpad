(ns sketchpad.buffer-new
	(:use [seesaw meta])
	(:import 
           (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream))
	(:require [sketchpad.tab-builder :as tab-builder]
			[sketchpad.file :as file]
			[seesaw.core :as seesaw]
			[sketchpad.tab :as tab]
			[sketchpad.state :as sketchpad.state]
			[clojure.string :as string]))

(def new-buff-app sketchpad.state/app)

(defn selected-file-path []
  (when-let [tree-path (-> @new-buff-app :docs-tree .getSelectionPaths first)]
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

(defn buffer-from-file! [file-path]
	(let [buffer-new (tab-builder/new-tab! (selected-file-path))]
		(load-file-into-buffer buffer-new file-path)))

(defn blank-clj-buffer! []
	(tab-builder/new-tab!))