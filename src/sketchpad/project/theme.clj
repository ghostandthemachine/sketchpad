(ns sketchpad.project.theme
	(:use [seesaw color])
	(:require [sketchpad.config :as config]))

(comment 
	"Sketchpad Theme format."
	{:color theme-color})

(defn get-project-theme-color [id]
	(if (= id -1)
		(color :gray)
		(get config/project-theme-colors id)))

(defn get-new-theme [uu-id]
	{:color (get-project-theme-color uu-id)})