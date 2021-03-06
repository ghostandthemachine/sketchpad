(ns sketchpad.project.project
	(:use [clojure.pprint]
				[seesaw color meta]
				[sketchpad.util.look-up])
	(:require [sketchpad.state.state :as state]
						[sketchpad.project.theme :as theme]
						[sketchpad.project.state :as project.state]
						[sketchpad.auto-complete.auto-complete :as auto-complete]
						[seesaw.core :as seesaw]
						[leiningen.core.project :as lein-project])
	(:import (java.io File)
										(javax.swing JOptionPane)))

(defn confirmed? [question title]
  (= JOptionPane/YES_OPTION
     (JOptionPane/showConfirmDialog
       nil question title  JOptionPane/YES_NO_OPTION)))

(defn current-buffers
	[]
	(into {} (mapcat #(deref (:buffers %)) (vals @(:projects @state/app)))))

(defonce auto-completion-providers (atom {}))

(def project-ids (atom -1))
 
(defn kind [filename]
  (let [f (File. filename)]
    (cond
      (.isFile f)      :file
      (.isDirectory f) :directory
      (.exists f)      :other
      :else            :non-existent)))
 
(defn lein-project-file? [project-path]
  (not= (kind (str project-path "/project.clj")) :non-existent))

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
	  (let [repls (atom {})
	  		last-focused-repl (atom nil)
	  		project {:type :project
					:path project-path
					:id id 
					:theme theme
					:repls repls
					:last-focused-repl last-focused-repl
					:last-focused-buffer (atom nil)
			  		:buffers (atom {})}]
	(swap! (@state/app :project-set) conj project-path)
	(swap! projects (fn [m] (assoc m project-path project)))
	
	(auto-complete/add-files-to-fuzzy-complete project-path)
	
	(if (lein-project-file? project-path)
		(try
			(when-let [lein-project (lein-project/read (str project-path "/project.clj"))]
				(swap! projects (fn [m] (assoc-in m [project-path :lein-project] lein-project)))
				(seesaw/invoke-later
					(when  (= (:name lein-project) "sketchpad")
						(let [app-repl-uuid (get-in @state/app [:application-repl :uuid])]
							(swap! repls assoc (get-in @state/app [:application-repl :uuid]) (:application-repl @state/app))
							(reset! (get project :last-focused-repl)  (get-in @state/app [:application-repl :uuid]))))))
				(catch Exception e))))))

(defn remove-project [project-path]
	(let [projects (:projects @state/app)]
		(swap! projects dissoc project-path))
	(swap! (@state/app :project-set) disj project-path)
	(auto-complete/update-fuzzy-completions))

(defn update-lein-project! [project]
	(let [projects (@state/app :projects)
		 project-path (:path project)]
		(try
			(when-let [lein-project (lein-project/read (str project-path "/project.clj"))]
				(swap! projects (fn [m] (assoc-in m [project-path :lein-project] lein-project))))
			(catch Exception e))))

(defn clear-project-set []
(reset! (:project-set @state/app) (sorted-set)))

(defn clear-projects []
	(clear-project-set)
	(reset! (:projects @state/app) {}))

(defn setup-non-project-map []
	(add-project @state/app "/default"))

(defn add-buffer-to-app [buffer]
	(swap! (:current-buffers @state/app) assoc (:uuid buffer) buffer))

(defn remove-buffer-from-app [buffer]
	(swap! (:current-buffers @state/app) dissoc (:uuid buffer)))

(defn add-buffer-to-project [project-path buffer]
	(let [buffers (get-in @(@state/app :projects) [project-path :buffers])]
  (swap! buffers assoc (:uuid buffer) buffer)))

(defn remove-buffer-from-project [buffer]
  	(let [buffers (get-in @(@state/app :projects) [(:project buffer) :buffers])]
  (swap! buffers dissoc (:uuid buffer))))

(defn add-repl-to-project [project-path repl]
	(let [repls (get-in @(@state/app :projects) [project-path :repls])]
  (swap! repls assoc (:uuid repl) repl)))

(defn remove-repl-from-project [repl]
  	(let [buffers (get-in @(@state/app :projects) [(:project repl) :repls])]
  (swap! buffers dissoc (:uuid repl))))

(defn project-from-path [project-path]
	(get @(@state/app :projects) project-path))

(defn project-from-buffer [buffer]
	(project-from-path (:project buffer)))

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

(defn delete-file
  "Delete file f. Raise an exception if it fails unless silently is true. From Clojure-Contrib"
  [f & [silently]]
  (or (.delete (clojure.java.io/file f))
      silently
      (throw (java.io.IOException. (str "Couldn't delete " f)))))

(defn delete-file-recursively
  "Delete file f. If it's a directory, recursively delete all its contents.
Raise an exception if any deletion fails unless silently is true. From Clojure-Contrib"
  [f & [silently]]
  (let [f (clojure.java.io/file f)]
    (if (.isDirectory f)
      (doseq [child (.listFiles f)]
        (delete-file-recursively child silently)))
    (delete-file f silently)))

(defn delete-project [project-path]
		(let [proj (get @(:projects @state/app) project-path)
								confirm (confirmed? (str "Are you sure you want to delete the project " project-path "?\n" "This will permanently delete all files in this directory.") "Delete Project")]
				(when confirm JOptionPane/YES_OPTION)
						(do
								(remove-project project-path)
								(delete-file-recursively project-path))))