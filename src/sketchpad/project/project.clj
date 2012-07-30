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

(defn projects []
	@project.state/project-map)

(defn get-project [project-path]
	(get (projects) project-path))

(defn get-project-id! []
	(swap! project-ids #(inc %)))

(defn add-project [project-path]
  (let [id (get-project-id!)
  		theme (theme/get-new-theme id)
  		projects (@state/app :projects)]
	  (swap! (@state/app :project-set) conj project-path)
	  (swap! projects 
	  		(fn [m] 
	  			(assoc m project-path {:type :project
	  									:path project-path
	  									:id id 
	  									:theme theme 
	  									:repls (atom {}) 
	  									:buffers (atom {})})))
	  (if (lein-project-file?)
		  (when-let [lein-project (lein-project/read (str project-path "/project.clj"))]
		  	(swap! projects (fn [m] (assoc-in m [project-path :lein-project] lein-project)))))))

(defn add-buffer-to-project! [project-path buffer] 
	(let [text-area (:text-area buffer)
		  title (:title buffer)
		  projects-map @(@state/app :projects)
		  project (projects-map project-path)
		  project-buffers (project :buffers)]   	
	(swap! project-buffers (fn [buffers] (assoc buffers title buffer)))))

(defn remove-buffer-from-project! [project-path buffer]
	(let [title (:title buffer)
		  projects @(@state/app :projects)
		  project (projects project-path)
		  project-buffers (project :buffers)]
	(swap! project-buffers (fn [buffers] (dissoc buffers title)))))

(defn setup-non-project-map []
	(add-project @state/app "/default"))

(defn current-buffers []
	(:current-buffers @state/app))

(defn add-buffer-to-app [buffer]
	(swap! (current-buffers) assoc (:uuid buffer) buffer))

(defn remove-buffer-from-app [buffer]
	(swap! (current-buffers) dissoc (:uuid buffer)))

(defn add-buffer-to-project [project-path buffer]
	(let [buffers (get-in @(@state/app :projects) [project-path :buffers])]
  (swap! buffers assoc (:uuid buffer) buffer)))

(defn remove-buffer-from-project [buffer]
  	(let [buffers (get-in @(@state/app :projects) [(:project-path buffer) :buffers])]
  (swap! buffers dissoc (:uuid buffer))))

(defn add-repl-to-project [project-path repl]
	(let [buffers (get-in @(@state/app :projects) [project-path :repls])]
  (swap! buffers assoc (:uuid repl) repl)))

(defn remove-repl-from-project [repl]
  	(let [buffers (get-in @(@state/app :projects) [(:project-path repl) :repls])]
  (swap! buffers dissoc (:uuid repl))))

(defn project-from-path [project-path]
	(get @(@state/app :projects) project-path))

(defn project-theme [project-path]
	(:theme (project-from-path project-path)))

(defn project-color [project-path]
	(:color (project-theme project-path)))

(defn buffer-theme [buffer]
	(project-theme (:project buffer)))

(defn buffer-color [buffer]
	(get-in (project-from-path (:project buffer)) [:theme :color]))

(defn repl-color [repl]
	(get-in (project-from-path (:project repl)) [:theme :color]))

(defn project-by-name [project-name]
	(let [short-keys (map #(last (clojure.string/split (str %) #"/")) (keys @(:projects @state/app)))
		  long-keys (keys @(:projects @state/app))]
		 (or (contains? short-keys project-name) (contains? long-keys project-name))))

