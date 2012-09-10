(ns sketchpad.project.create
	(:use [sketchpad.util.look-up])
	(:require [sketchpad.tree.utils :as tree.utils]
			[sketchpad.project.project :as project]
			[sketchpad.repl.print :as repl.print]
			[seesaw.core :as seesaw]))

(defn- core-clj-path [pm]
	(str (:path pm) (:clean-name pm) "/src/" (:clean-name pm) "/core.clj"))

(defn- project-path [pm]
	(str (:path pm) (:clean-name pm)))

(defn- core-clj-header [project-name]
	(str "(ns " project-name ".core)\n"))

(defn- new-core-clj [project-map]
	(let [file-text (core-clj-header (:project-name project-map))]
    (spit (java.io.File. (str (:path project-map) "/" (:clean-name project-map) "/src/" (:clean-name project-map)) "core.clj") file-text)))

(defn- make-dirs [project-map]
	(.mkdirs (java.io.File. (:path project-map) (str (:project project-map) "/src/" (:clean-name project-map)))))

(defn- new-project-clj [project-map]
  (let [file-text (:project-clj project-map)]
    (spit (java.io.File. (str (:path project-map) (:clean-name project-map)) "project.clj") file-text)))

(defn- make-readme [project-map]
	(spit (java.io.File. (str (:path project-map) (:clean-name project-map)) "README.md") "FIXME"))

(defn- print-creation-info [project-map]
	(repl.print/pln
		"Created new project titled: " (:project-name project-map)
		"Version: " (:version project-map)
		"Path: " (:path project-map))
	(repl.print/prompt))

(defn new-project [project-map]
	(seesaw/invoke-later
		(make-dirs project-map)
		(new-project-clj project-map)
		(new-core-clj project-map)
		(make-readme project-map)
		(project/add-project (project-path project-map))
		(tree.utils/update-tree)
        (tree.utils/set-tree-selection (get-file-tree) (core-clj-path project-map))
        ; (print-creation-info project-map)
        ))