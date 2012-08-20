(ns sketchpad.project.form
	(:use [seesaw.core]
				[sketchpad.auto-complete.auto-complete])
	(:require [seesaw.forms :as forms]
						[sketchpad.rsyntax :as rsyntax]
						[sketchpad.project.create :as create]
						[sketchpad.project.create :as create]
						[sketchpad.config.config :as config]))

; (defn form-success )

(defn- re-pack! [e]
	(pack! (.. e getComponent getParent)))

(defn project-creation-form
  []
  (let [project-path-str (str (.getAbsoluteFile (java.io.File. config/project-path)) "/")
  			repo-text-area (rsyntax/text-area :columns 200 
  																				:id :project-form-dependencies
  																				:listen [:component-resized re-pack!])
  			project-title (text :id :project-form-project)
  			version-number (text :id :project-form-version)
  			project-path (text :id :project-form-path)
  			project-description (text :id :project-form-description)
				form	(forms/forms-panel
								  "pref,4dlu,80dlu,8dlu,pref,4dlu,80dlu"
								  :column-groups [[1 5]]
								  :items [(forms/separator "Leiningen Project Setup")
								          "Project Title" (forms/span project-title 5)
								          "Version Number" (forms/span version-number 5)
								          "Project Path" (forms/span project-path 5)
								          "Description" (forms/span project-description 5)
								          "Dependencies" (forms/span repo-text-area 5)]
								  :default-dialog-border? true)]
			(config! project-title :text "project-title")
			(config! version-number :text "0.0.1-SNAPSHOT")
			(config! project-path :text project-path-str)
			(config! project-description :text "FIXME: write")
			(config! repo-text-area :text "[org.clojure/clojure \"1.4.0\"]\n")
			(.setCaretPosition repo-text-area (.getLastVisibleOffset repo-text-area))
		(install-clojars-auto-completions repo-text-area)
		form))

(defn defproject-template [project-name version]
	(str "defproject " project-name " \"" version "\"\n"))

(defn description-template [description]
	(str "\t:description \"" description "\"\n"))

(defn dependencies-template [dependencies-str]
	(let [dependencies (clojure.string/split dependencies-str #"\n")
				deps-str (str "\t:dependencies [" 
									(eval dependencies-str)
									"]")]
		deps-str))

(defn project-clj-template [opts-map])

(defn select-text [component id]
	(config
		(select component [id])
		:text))

(defn- make-project-map [component]
	(let [name (select-text component :#project-form-project)
				version (select-text component :#project-form-version)
		 		path (select-text component :#project-form-path)
		 		description (select-text component :#project-form-description)
		 		dependencies-str (select-text component :#project-form-dependencies)
		 		project-clj-str 
		 			(str 
		 				"("
		 				(defproject-template name version)
		 				(description-template description)
		 				(dependencies-template dependencies-str)
		 				")")
		 	  project-map {:path path
										 :project-name name
										 :clean-name (clojure.string/replace name \- \_)
										 :version version
										 :description description
										 :dependencies dependencies-str
										 :project-clj project-clj-str}]
				project-map))

(defn- handle-project-form [project-map]
	(create/new-project (make-project-map project-map)))

(defn create-new-project
"Create a new Leiningen project."
	([]
		(invoke-later
			(pack!
				(show!
					(dialog :content (project-creation-form)
									:success-fn handle-project-form)))))
	([project-map]
		(create/new-project project-map)))


