(ns sketchpad.project.theme
	(:use [seesaw color])
	(:require [sketchpad.config.config :as config]))

(comment 
	"SketchPad Theme format."
	{:color theme-color})

(defn get-project-theme-color [id]
	(if (= id -1)
		(color :gray)
		(get config/project-theme-colors id)))

(defn get-new-theme [id]
	{:color (atom (get-project-theme-color id))})