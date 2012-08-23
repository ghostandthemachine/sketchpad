
(ns sketchpad.editor.mini-map
	(:require [seesaw.core :as seesaw]
						[sketchpad.rsyntax :as rsyntax]))


(defn mini-map 
"Created a mini map component for a given text area."
	[rsta]
	(let [mini-rsta (rsyntax/text-area
											:class :mini-map
											:syntax (config rsta :syntax)
											)]))