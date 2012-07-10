(ns sketchpad.repl-dep-loader
	(:use [cemerick.pomegranate :only (add-dependencies)]))

(defn load-deps [project-path]
	(let [lein-list (read-string (slurp (str project-path "/project.clj")))]
		(add-dependencies :coordinates (do (first (nth (partition 2 lein-list) 4))))))