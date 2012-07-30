(ns sketchpad.repl.server
  (:use [clojure.pprint])
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
	    [sketchpad.config :as config]
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

(def profile {:dependencies '[[org.clojure/tools.nrepl "0.2.0-beta7"
                               :exclusions [org.clojure/clojure]]
                              [clojure-complete "0.2.1"
                               :exclusions [org.clojure/clojure]]]})
; TODO: The tools.nrepl dependency should not be hard coded here.  Instead it should be setup
; to extract it from the leiningen version currently being used, so that we use the same
; nrepl lein has been written to work with.
;(defn- server [project]
;  (println "create a server for project: " (:path project))
;	(let [port (get-port)
;		  ack-port (.getLocalPort (:ss @lein-repl-server))]
;        (when-let [lein-project (:lein-project project)]
;        	(let [updated-lein-project (update-in lein-project [:dependencies] #(conj % '[org.clojure/tools.nrepl "0.2.0-beta8"]))]
;				(.start
;					(Thread.
;						(bound-fn []
;							(eval/eval-in-project updated-lein-project
;		                           `(let [server# (nrepl.server/start-server :port ~port :ack-port ~ack-port)])
;		                           `(require '[clojure.tools.nrepl.server :as nrepl.server])))))))
;		port))



(defn- start-server [project port ack-port]
	(let [server-starting-form
	        `(let [server# (clojure.tools.nrepl.server/start-server
	                        :port ~port :ack-port ~ack-port)]

                (println "Created server on port " (-> server# deref :ss .getLocalPort))
	           (while true (Thread/sleep Long/MAX_VALUE)))]
    	(eval/eval-in-project
    		(project/merge-profiles project [(:repl (user/profiles) profile)])
    		server-starting-form
    		'(do 
           (require 'clojure.tools.nrepl.server)
    			 (require 'complete.core)))))

(defn- success [port]
	(println "Created new nREPL server on port: " port)
	port)

(defn- failure []
	(repl.print/pln "Could not create nREPL server..."))
;;
(defn repl-server
"Create an outside REPL process for a given project and buffer."
[sketchpad-project]
  (println "create a server for project: " (:path sketchpad-project))
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
;        (with-open [conn (repl.connection/connection repl-port)]
;        	(println conn)
;        	conn)
;        	(println "REPL server launch timed out."))
;       (let [conn-uuid (.. UUID randomUUID toString)]
;          (reset! cur-connection-uuid conn-uuid)
;          (println "created new connection uuid: " conn-uuid)
;          conn-uuid)
           repl-port
          )))


; (defn repl
;   ([] (repl nil))
;   ([project]
;      (let [port (get-port)]
;        (nrepl/reset-ack-port!)
;        (start-server port (-> lein-repl-server first .getLocalPort))
;        (reply/launch-nrepl {:attach (str (nrepl/wait-for-ack 5000))}))))
