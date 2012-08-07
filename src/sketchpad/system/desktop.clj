(ns sketchpad.system.desktop
	(:require [sketchpad.state.state :as state])
	(:import  (java.io File)
						(java.awt Desktop)))

(defn selected-file-path []
  (when-let [tree-path (-> (get-in (:file-tree @state/app) [:component :tree]) .getSelectionPaths first)]
    (-> tree-path .getLastPathComponent .getUserObject .getAbsolutePath)))

(defn reveal-in-finder
"Open a file or folder in the system finder/explorer."
	([] (reveal-in-finder (selected-file-path)))
	([path]
		(println path)
	(when-let [desktop (Desktop/getDesktop)]
		(when-let [file (File. path)]
			(if (.isFile file)
				(.open desktop (.. file getAbsoluteFile getParentFile))
				(.open desktop file))))))

; (defn print-file [path]
; 	(when-let [desktop (java.awt.Desktop/getDesktop)]
; 		(when-let [file (.getAbsoluteFile (java.io.File. path))]
; 			(.print desktop file))))