(ns sketchpad.project.project
	(:use [clojure.pprint]
				[seesaw color meta])
	(:require [sketchpad.state :as state]
		[sketchpad.project.theme :as theme]
		[sketchpad.project.state :as project.state]
		[leiningen.core.project :as lein-project])
	(:import [java.io File]))

(comment
	"Sketchpad project format."
	{:path project-root-path
	 :lein-project lein-project
	 :id uuid
	 :theme theme
	 :repls (atom #{})
	 :buffers (atom nil)})

(def project-ids (atom -1))
 
(defn kind [filename]
  (let [f (File. filename)]
    (cond
      (.isFile f)      :file
      (.isDirectory f) :directory
      (.exists f)      :other
      :else            :non-existent)))
 
(defn lein-project-file? []
  (not= (kind "project.clj") :non-existent))

(defn buffer
"return the text from the current buffer component"
[]
(let [tabbed-panel (@state/app :editor-tabbed-panel)
	cur-idx (.getSelectedIndex tabbed-panel)
	cur-buffer (.getComponentAt tabbed-panel cur-idx)]
	cur-buffer))

(defn projects []
	@project.state/project-map)

(defn get-project [project-path]
	(get (projects) project-path))

(defn current-project []
	(let [cur-buffer (buffer)
		  current-project (get-meta cur-buffer :project)]
		current-project))

(defn get-project-id! []
	(swap! project-ids #(inc %)))

(defn add-project [project-path]
  (let [id (get-project-id!)
  		theme (theme/get-new-theme id)
  		projects (@state/app :project-map)]
	  (swap! (@state/app :project-set) conj project-path)
	  (swap! projects 
	  		(fn [m] 
	  			(assoc m project-path {:type :project
	  									:path project-path
	  									:id id 
	  									:theme theme 
	  									:active-repls (atom #{}) 
	  									:active-buffers (atom nil)})))
	  (if (lein-project-file?)
		  (when-let [lein-project (lein-project/read (str project-path "/project.clj"))]
		  	(swap! projects (fn [m] (assoc-in m [project-path :lein-project] lein-project)))
		  	(swap! projects (fn [m] (assoc-in m [project-path :type] :lein-project)))))))

(defn add-buffer-to-project! [project-path buffer] 
	(let [text-area (:text-area buffer)
		  title (:title buffer)
		  projects-map @(@state/app :project-map)
		  project (projects-map project-path)
		  project-buffers (project :active-buffers)]   	
	(swap! project-buffers (fn [buffers] (assoc buffers title buffer)))))

(defn remove-buffer-from-project! [project-path buffer]
	(let [title (:title buffer)
		  projects @(@state/app :project-map)
		  project (projects project-path)
		  project-buffers (project :active-buffers)]
	(swap! project-buffers (fn [buffers] (dissoc buffers title)))))

(defn add-repl-to-project! [project-path repl]
	(let [projects (@state/app :project-map)
		  project (@projects project-path)
		  project-repls (project :active-repls)]   	
	(swap! project-repls (fn [repls] (conj repls repl)))))

(defn remove-repl-from-project! [project-path repl]
	(let [projects-map @(@state/app :project-map)]
		(when-let [project-map (projects-map project-path)]
			(let [project-repls (project-map :active-repls)]   	
		(swap! project-repls (fn [repls] (disj repls repl)))))))

(defn setup-non-project-map []
	(add-project @state/app "/default"))
