(ns sketchpad.menu.menu-utils
	(:require [seesaw.bind :as bind]))

(defn set-item-binding [state-atom menu-item]
  (bind/bind state-atom (bind/transform (fn[b] b)) (bind/property menu-item :enabled?)))

(defn set-menu-item-bindings [states menu-items]
  (doseq [[k v] states]
    (set-item-binding v (k menu-items)))) 

(defn apply-menu-update-functions [tabbed-panel state-map fns]
	(doseq [[k v] state-map] 
    	((k fns) tabbed-panel v)))