(ns sketchpad.project
	(:use [clojure.pprint]
				[seesaw color meta])
	(:require [sketchpad.state :as sketchpad.state]))

(load-file "config/default.clj")

(def app sketchpad.state/app)

(def project-ids (atom -1))

(defn buffer
"return the text from the current buffer component"
[]
(let [tabbed-panel (@app :editor-tabbed-panel)
	cur-idx (- (.getSelectedIndex tabbed-panel) 1)
	cur-buffer (.getComponentAt tabbed-panel cur-idx)]
	cur-buffer))

(defn current-project []
	(let [cur-buffer (buffer)
		  current-project (get-meta cur-buffer :project)]
		current-project))

(defn get-project-id! []
	(swap! project-ids #(inc %)))

(defn get-project-theme-color [id]
	(if (= id -1)
		(color :gray)
		(get default-project-style-prefs id)))

(defn add-project [project-path]
  (let [id (get-project-id!)]
	  (swap! (@app :project-set) conj project-path)
	  (swap! (@app :project-map) 
	  		(fn [m] (assoc m project-path {:path project-path :id id :project-color (get-project-theme-color id) :active-repls (atom #{}) :active-buffers (atom nil)})))))

(defn add-buffer-to-project! [proj title rsta] 
	(let [projects-map @(@app :project-map)
		  proj-map (projects-map proj)
		  proj-buffers (proj-map :active-buffers)]   	
	(swap! proj-buffers (fn [buffers] (assoc buffers title rsta)))))

(defn remove-buffer-from-project! [proj title]
	(let [projects-map @(@app :project-map)
		  proj-map (projects-map proj)
		  proj-buffers (proj-map :active-buffers)]
	(swap! proj-buffers (fn [buffers] (dissoc buffers title)))))

(defn add-repl-to-project! [repl-buffer]
	(let [projects-map (@app :project-map)
		  proj-map (@projects-map (current-project))
		  proj-repls (proj-map :active-repls)]   	
	(swap! proj-repls (fn [repls] (conj repls repl-buffer)))))

(defn remove-repl-from-project! [repl-buffer proj-path]
	(let [projects-map @(@app :project-map)]
		(when-let [proj-map (projects-map proj-path)]
			(let [proj-repls (proj-map :active-repls)]   	
		(swap! proj-repls (fn [repls] (disj repls repl-buffer)))))))

(defn setup-non-project-map []
	(add-project @app "/default"))
