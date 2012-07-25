(ns sketchpad.project.state)

(defonce project-set (atom (sorted-set)))
(defonce project-map (atom {}))

(defn add-projects-to-app [app-atom]
	(swap! app-atom assoc :project-map project-map)
	(swap! app-atom assoc :project-set project-set))
