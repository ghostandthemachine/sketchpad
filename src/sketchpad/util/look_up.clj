(ns sketchpad.util.look-up
	(:require [sketchpad.state.state :as state]
		[clojure.contrib.map-utils :as contrib.map-utils]))

(defn get-file-tree []
	(get-in (:file-tree @state/app) [:component :tree]))