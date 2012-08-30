(ns sketchpad.buffer.grep
  (:require [clojure.java.shell :as shell]
  			[sketchpad.state.state :as state]))

(defn grep-files
"Grep the current projects or a given path."
  ([search-term] 
  	(:out  (apply shell/sh "grep" "-nir" "-I" search-term (keys @(:projects @state/app)))))
  ([search-term & opts]
    	(:out  (apply shell/sh "grep" "-nir" "-I" search-term opts))))