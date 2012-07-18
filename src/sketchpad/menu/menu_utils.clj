(ns sketchpad.menu.menu-utils
	(:require [seesaw.bind :as bind]))


(defn set-menu-binding [state-atom menu-item]
  (bind/bind state-atom (bind/transform #(.setEnabled %)) (bind/property menu-item :enabled?)))

