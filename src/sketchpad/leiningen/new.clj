(ns sketchpad.leiningen.new
  (:require [net.cgrand.enlive-html :as html]
  			[leiningen.new :as lein-new]
  			[sketchpad.project.project :as project]
  			[sketchpad.tree.utils :as tree.utils]
  			[sketchpad.config.config :as config]
  			[clojure.string :as clojure.string]))

(def base-url "https://clojars.org/search?q=lein-template")

(def sketchpad-project-path config/project-path)

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn- lein-templates []
  (map html/text (html/select (fetch-url base-url) [:li.search-results :a])))

(defn- lein-new-templates []
	(let [ templates (take-nth 2 (lein-templates))]
		(map #(first (clojure.string/split % #"/")) templates)))

(defn is-available? [template-name]
	(let [templates (map #(first (clojure.string/split % #"/")) (take-nth 2 (lein-templates)))
	  		 available (not (nil? (some #(= (name template-name)  %) templates)))]
		 available))

(defn templates []
	(let [templates (map #(first (clojure.string/split % #"/")) (take-nth 2 (lein-templates)))]
		(doseq [template templates]
			(println (keyword template)))))

(defn lein-new
"Use lein-new to create a new Leiningen project from a lein template."
  ([template project-name] (lein-new template sketchpad-project-path (name project-name)))
  ([template project-path project-name]
  (let [project-name-str (name project-name)
      new-project-abs-path (str project-path project-name)]
    (if-let [new-project-dir (clojure.java.io/file new-project-abs-path)]
    	(do
    		(println "new project dir " new-project-dir)
	      (when (.mkdir new-project-dir)
	        (println "made new dir " new-project-dir)
	        (let[abs-path (.getAbsolutePath new-project-dir)]
	          (println abs-path)
	          (try
		          (lein-new/new "--to-dir" abs-path (name template) (name project-name))
             (catch Exception e (println e)))
	          (project/add-project abs-path)
	          (tree.utils/update-tree))))
	      (println new-project-abs-path " could not be createad...")))))

; (defn lein-new-template
;   ([template-name] (lein-new-template (name template-name) (str template-name "-template")))
;   ([template-name template-dir]
;   (let [project-name-str (name template-name)
;         template-path (str (name project-name-str) "-template")
;         new-project-abs-path (str project-path template-path)]
;     (if-let [new-project-dir (clojure.java.io/file new-project-abs-path)]
;       (do
;         (when (.mkdir new-project-dir)
;           (let[abs-path (.getAbsolutePath new-project-dir)]
;             (lein-new/new "template" (name project-name-str) "--to-dir" new-project-abs-path)
;             (project/add-project abs-path)
;             (invoke-later
;               (tree.utils/update-tree)))))
;       (println new-project-abs-path " could not be createad..."))
;     )))