(ns sketchpad.system.desktop
	(:require [sketchpad.state.state :as state]
      			[sketchpad.tree.utils :as utils])
	(:import  (java.io File)
						(java.awt Desktop)
						(javax.swing JOptionPane)))

(defn selected-file-path []
  (when-let [tree-path (-> (get-in (:file-tree @state/app) [:component :tree]) .getSelectionPaths first)]
    (-> tree-path .getLastPathComponent .getUserObject .getAbsolutePath)))

(defn reveal-in-finder
"Open a file or folder in the system finder/explorer."
	([] (reveal-in-finder (selected-file-path)))
	([path]
	(when-let [desktop (Desktop/getDesktop)]
		(when-let [file (File. path)]
			(if (.isFile file)
				(.open desktop (.. file getAbsoluteFile getParentFile))
				(.open desktop file))))))

(defn new-folder [dir-path]
"Creates a new folder with the given path."
	(let [new-dir (File. dir-path)]
		(if-not (.exists new-dir)
			(.mkdirs new-dir)
			(let [overwrite-response (JOptionPane/showMessageDialog (:frame @state/app)
                               (str "The directory " new-dir " already exists.\n"
                               			"Do you want to replace it?")
                               "Create new directory" JOptionPane/YES_NO_CANCEL_OPTION)]
				(when (= overwrite-response JOptionPane/YES_OPTION)
						(.mkdirs new-dir))))
	(utils/update-project-tree)))

(defn delete-folder [dir-path]
"Delete the folder at the given path."
	(let [file-dir (File. dir-path)]
		(when (.exists file-dir)
			(let [delete-response (JOptionPane/showMessageDialog nil
                               (str "Are you sure you want to delete the directory " file-dir " ?\n"
                               			"This will delete the folder from the system")
                               "Delete directory" JOptionPane/OK_CANCEL_OPTION)]
			(println delete-response)
				(when (= delete-response JOptionPane/OK_OPTION)
					(.delete file-dir))))
	(utils/update-project-tree)))

