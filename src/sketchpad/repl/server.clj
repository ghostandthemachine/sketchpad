(ns sketchpad.repl.server
  (:use [clojure.pprint])
	(:require clojure.main
	    clojure.set
	    [leiningen.core.eval :as eval]
	    [leiningen.core.project :as project]
	    [clojure.tools.nrepl.server :as nrepl.server]
	    [clojure.tools.nrepl :as nrepl]
	    [leiningen.core.classpath :as classpath]
	    [leiningen.core.main :as main]
	    [sketchpad.config :as config]
	    [sketchpad.repl.print :as repl.print]))


(def repl-port-ids (atom 0))
(def base-repl-port 2000)
(defn next-repl-port! [] (+ base-repl-port (swap! repl-port-ids inc)))

; TODO: The tools.nrepl dependency should not be hard coded here.  Instead it should be setup
; to extract it from the leiningen version currently being used, so that we use the same
; nrepl lein has been written to work with.
(defn server [project]
  (println "create a server for project: " (:path project))
	(let [port (next-repl-port!)]
        (when-let [lein-project (:lein-project project)]
        	(let [updated-lein-project (update-in lein-project [:dependencies] #(conj % '[org.clojure/tools.nrepl "0.2.0-beta8"]))]
				(.start
					(Thread.
						(bound-fn []
							(eval/eval-in-project updated-lein-project
		                           `(require '[clojure.tools.nrepl.server :as nrepl.server])
		                           `(let [server (nrepl.server/start-server :port ~port)])))))))
		port))

(defn success [port]
  (repl.print/pln)
	(repl.print/pln "Created new nREPL server on port: " port)
	(repl.print/prompt)
	port)

(defn failure []
	(repl.print/pln)
	(repl.print/pln "Could not create nREPL server...")
	(repl.print/prompt))

(defn repl-server
"Create an outside REPL process for a given project and buffer."
[project]
	(if-let [port (server project)]
	  	(success port)
  		(failure))
	(println "Not a Leiningen project.."))