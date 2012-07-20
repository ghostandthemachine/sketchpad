(ns sketchpad.menu.menu-utils
	(:require [seesaw.bind :as bind]
						[seesaw.core :as seesaw]
						[seesaw.keystroke :as keystroke]))

(defn set-item-binding [state-atom menu-item]
  (bind/bind state-atom (bind/transform (fn[b] b)) (bind/property menu-item :enabled?)))

(defn set-menu-item-bindings [states menu-items]
  (doseq [[k v] states]
    (set-item-binding v (k menu-items)))) 

(defn apply-menu-update-functions [tabbed-panel state-map fns]
	(doseq [[k v] state-map] 
    	((k fns) tabbed-panel v)))

(defn make-menu [items]
	(reduce
		(fn [menu-map item]
			(let [[ikey iname istroke ihandler] item
						mitem (seesaw/menu-item :text iname 
												:key (keystroke/keystroke istroke)
												:listen [:action ihandler])]))
		{}
		items))