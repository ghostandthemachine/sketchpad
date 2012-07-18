	(ns sketchpad.project-manager 
	(:use [clojure.pprint]
			[seesaw color meta]
			[sketchpad tab-manager]))

(load-file "config/default.clj")

(def project-ids (atom -1))

(defn get-current-project [app-atom]
	(let [tabbed-panel (@app-atom :editor-tabbed-panel)
		  current-project (get-meta (current-text-area tabbed-panel) :project)]
		current-project))

(defn get-project-id! []
	(swap! project-ids #(inc %)))

(defn get-project-theme-color [id]
	(if (= id -1)
		(color :gray)
		(get default-project-style-prefs id)))

(defn add-project [app project-path]
  (let [id (get-project-id!)]
	  (swap! (app :project-set) conj project-path)
	  (swap! (app :project-map) 
	  		(fn [m] (assoc m project-path {:path project-path :id id :project-color (get-project-theme-color id) :active-repls (atom #{}) :active-buffers (atom nil)})))))

(defn add-buffer-to-project! [app-atom proj title rsta] 
	(let [projects-map @(@app-atom :project-map)
		  proj-map (projects-map proj)
		  proj-buffers (proj-map :active-buffers)]   	
	(swap! proj-buffers (fn [buffers] (assoc buffers title rsta)))))

(defn remove-buffer-from-project! [app-atom proj title]
	(let [projects-map @(@app-atom :project-map)
		  proj-map (projects-map proj)
		  proj-buffers (proj-map :active-buffers)]
	(swap! proj-buffers (fn [buffers] (dissoc buffers title)))))

(defn add-repl-to-project! [app-atom repl-buffer]
	(let [projects-map (@app-atom :project-map)
		  proj-map (@projects-map (get-current-project app-atom))
		  proj-repls (proj-map :active-repls)]   	
	(swap! proj-repls (fn [repls] (conj repls repl-buffer)))))

(defn remove-repl-from-project! [app-atom repl-buffer proj-path]
	(let [projects-map @(@app-atom :project-map)]
		(when-let [proj-map (projects-map proj-path)]
			(let [proj-repls (proj-map :active-repls)]   	
		(swap! proj-repls (fn [repls] (disj repls repl-buffer)))))))

(defn setup-non-project-map [app-atom]
	(add-project @app-atom "/default"))
