(ns sketchpad.tree
	(:require [sketchpad.filetree :as file-tree]))

(defonce tree-app sketchpad.state/app)

(defn selected-path []
	(file-tree/get-selected-file-path @tree-app))








