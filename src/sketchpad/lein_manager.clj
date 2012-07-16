(ns sketchpad.lein-manager
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


(def uniq-id (atom 0))
(defn get-next [] (swap! uniq-id inc))

(defn get-lein-project [path] 
	(let [project-clj-path (str path "/project.clj")]
		(project/read project-clj-path)))

(defn create-lein-repl [proj]
	(let [server-port (nrepl.server/start-server :port 7888)
				conn (nrepl/connect :port server-port)
				client (nrepl/client conn 1000)
				session-id (get-next)]
	{:server-port server-port 
	 :conn conn
	 :client client 
	 :session-id session-id}))

(defn server-port [server]
  (-> server deref :ss .getLocalPort))

(comment 
(require '[sketchpad.lein-manager :as lein])
(require '[leiningen.core.project :as project])
(require '[leiningen.core.eval :as eval])
(require '[leiningen.core.user :as user])
(require '[sketchpad.user :as u]))

(def port-ids (atom 0))
(def base-port-number 2000)
(defn next-repl-port! [] (+ base-port-number (swap! port-ids inc)))


(defn repl-port [project]
  (Integer. (or (System/getenv "LEIN_REPL_PORT")
                (-> project :repl-options :port)
                0)))

(defn repl-host [project]
  (or (System/getenv "LEIN_REPL_PORT")
      (-> project :repl-options :host)))

(defn ack-port [project]
  (when-let [p (or (System/getenv "LEIN_REPL_ACK_PORT")
                   (-> project :repl-options :ack-port))]
    (Integer. p)))


(defn project-repl-server [project]
	(let [port (next-repl-port!)
				non-prep-task-project (dissoc :prep-tasks)]
		(.start
			(Thread. 
				(bound-fn []
					(eval/eval-in-project
			      non-prep-task-project
			      `(nrepl.server/start-server :port ~port)
			      '(require [clojure.tools.nrepl.server :as nrepl.server])))))
		{:port port}))
;
;(defn project-repl-server [project]
;	(let [port (repl-port project)]
;	    (eval/eval-in-project
;	      project
;	      `(nrepl.server/start-server :port port)
;	      '(require [clojure.tools.nrepl.server :as nrepl.server]))
;		port))
