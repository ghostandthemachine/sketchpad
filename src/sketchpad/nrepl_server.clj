(ns sketchpad.nrepl-server
	(:use [clojure.tools.nrepl.server :only (start-server stop-server)]))
(defonce port (first *command-line-args*))
(spit "log.txt" "running nrepl script...")
(start-server :port port)
(spit "log.txt" (str "Started nREPL server on port: " port))
