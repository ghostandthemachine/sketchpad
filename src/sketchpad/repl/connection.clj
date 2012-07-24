(ns sketchpad.repl.connection
	(:require [sketchpad.config :as config]
		[clojure.tools.nrepl :as nrepl]))

(defn connection
"Create a connection to the given server."
[port]
	(let [conn (nrepl/connect :port port)
		  client (nrepl/client conn config/repl-response-timeout)]
		{:client client
		 :conn conn
		 :port port}))