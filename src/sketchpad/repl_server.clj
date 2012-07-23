(ns sketchpad.repl-server
	(:require clojure.main
	    clojure.set
	    [reply.main :as reply]
	    [clojure.java.io :as io]
	    [leiningen.core.eval :as eval]
	    [leiningen.core.project :as project]
	    [clojure.tools.nrepl.ack :as nrepl.ack]
	    [clojure.tools.nrepl.server :as nrepl.server]
	    [clojure.tools.nrepl :as nrepl]
	    [leiningen.core.classpath :as classpath]
	    [leiningen.core.main :as main]))


(def repl-port-ids (atom 0))
(def base-repl-port 2000)
(defn next-repl-port! [] (+ base-repl-port (swap! repl-port-ids inc)))

; TODO: The tools.nrepl dependency should not be hard coded here.  Instead it should be setup
; to extract it from the leiningen version currently being used, so that we use the same
; nrepl lein has been written to work with.
(defn project-repl-server [project]
	(let [port (next-repl-port!)
        project (update-in project [:dependencies] #(conj % '[org.clojure/tools.nrepl "0.2.0-beta8"]))]
		(.start
			(Thread.
				(bound-fn []
					(eval/eval-in-project project
                           `(nrepl.server/start-server :port ~port)
                           `(require '[clojure.tools.nrepl.server :as nrepl.server])))))
		port))


(defn repl-server!
"Create an outside REPL process for a given project and buffer."
[lein-project repl-buffer]
	(let [new-server-port (project-repl-server lein-project)]))