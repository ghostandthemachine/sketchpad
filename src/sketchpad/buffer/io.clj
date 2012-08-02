(ns sketchpad.buffer.io
	(:require 
			[sketchpad.file.file :as file]
			[sketchpad.util.tab :as tab]
			[sketchpad.tree.utils :as tree.utils]
			[sketchpad.state.state :as state]))

(defn update-info-title [title]
	(swap! (@state/app :doc-title-atom) (fn [lbl] title)))

(defn save-new-buffer! [buffer]
	(when-let [new-file (file/save-file-as!)]
		(let [new-file-title (.getName new-file)]
		  (when (file/save-file! buffer new-file)
		    (assoc (:file buffer) new-file)
		    (tab/title-at! (tab/index-of-buffer buffer) new-file-title)
		    (tab/mark-current-tab-clean! (get-in (:buffer-tabbed-panel @state/app) [:component :container]))
		    (tree.utils/update-tree)
			(update-info-title (tab/title))))))

(defn save-buffer! [buffer]
	(let [file @(:file buffer)
          file-title (.getName file)]
        (file/save-file! buffer file)
        (tree.utils/update-tree)
		(update-info-title (tab/title))))
