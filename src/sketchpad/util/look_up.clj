(ns sketchpad.util.look-up
	(:require [sketchpad.state.state :as state]))

(defn get-file-tree []
	(get-in (:file-tree @state/app) [:component :tree]))