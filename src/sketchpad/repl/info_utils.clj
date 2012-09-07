(ns sketchpad.repl.info-utils
	(:require [sketchpad.state.state :as state]
			[seesaw.core :as seesaw]))

(defn post-msg
"Post a notification message to the output label."
	([msg]
	(let [info-atom (:repl-info-atom @state/app)]
		(seesaw/invoke-later
			(reset! info-atom msg))))
	([msg delay-time]
	(let [info-atom (:repl-info-atom @state/app)]
		(seesaw/invoke-later
			(reset! info-atom msg)
			(seesaw/timer
				#(reset! info-atom %)
				:repeats? false
				:initial-value ""
				:delay delay-time)))))

(defn load-msg
	([msg]
	(let [info-atom (:repl-info-atom @state/app)
		  delay 500]
		(reset! info-atom msg)
		(seesaw/timer
			#(swap! info-atom (fn [m] (str m ".")))))))

(defn append-msg
	[msg]
	(swap! (:repl-info-atom @state/app) #(str % msg)))