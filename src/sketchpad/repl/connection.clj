(ns sketchpad.repl.connection
	(:require [sketchpad.config :as config]
		[clojure.tools.nrepl :as nrepl]))

(defn connection
"Create a connection to the given server."
[port]
	(with-open [conn (nrepl/connect :port port)]
		(let [client (nrepl/client conn config/repl-response-timeout)]
		{:client client
		 :conn conn
		 :port port})))