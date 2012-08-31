(ns sketchpad.repl.server
  (:use [clojure.pprint]
        [seesaw.core])
	(:require clojure.main
	    clojure.set
	    [leiningen.core.eval :as eval]
	    [leiningen.repl :as leiningen.repl]
	    [leiningen.core.project :as project]
	    [clojure.tools.nrepl.server :as nrepl.server]
	    [clojure.tools.nrepl :as nrepl]
	    [clojure.tools.nrepl.ack :as nrepl.ack]
	    [leiningen.core.classpath :as classpath]
	    [leiningen.core.main :as main]
	    [leiningen.core.user :as user]
	    [sketchpad.config.config :as config]
	    [sketchpad.repl.connection :as repl.connection]
	    [sketchpad.repl.print :as repl.print])
  (:import [java.util UUID]))

(def lein-repl-server
  (delay (nrepl.server/start-server
           :handler (nrepl.ack/handle-ack nrepl.server/unknown-op))))
           
(defn- repl-port [project]
  (Integer. (or (System/getenv "LEIN_REPL_PORT")
                (-> project :repl-options :port)
                0)))

(def profile {:dependencies '[[org.clojure/tools.nrepl "0.2.0-beta7"]
                              [clojure-complete "0.2.1"
                               :exclusions [org.clojure/clojure]]]})

(defn- start-server [project port ack-port]
	(let [server-starting-form
	        `(let [server# (clojure.tools.nrepl.server/start-server
	                        :port ~port :ack-port ~ack-port)]
                (println "Created REPL on port " (-> server# deref :ss .getLocalPort))
	           (while true (Thread/sleep Long/MAX_VALUE)))]
    	(eval/eval-in-project
    		(project/merge-profiles project [(:repl (user/profiles) profile)])
    		server-starting-form
    		'(do 
           (require 'clojure.tools.nrepl.server)
    			 (require 'complete.core)))))

(defn repl-server
"Create an outside REPL process for a given project and buffer."
[sketchpad-project]
  (nrepl.ack/reset-ack-port!)
  (let [prepped (promise)
  	    project (:lein-project sketchpad-project)]
	(.start
	  (Thread. 
	    (bound-fn []
          (start-server (and project (vary-meta project assoc
                                                 :prepped prepped))
                         (repl-port project)
                          (-> @lein-repl-server deref :ss .getLocalPort)))))
       (and project @prepped)
       (if-let [repl-port (nrepl.ack/wait-for-ack (or (-> project
                                                           :repl-options
                                                           :timeout)
                                                       20000))]
           repl-port)))