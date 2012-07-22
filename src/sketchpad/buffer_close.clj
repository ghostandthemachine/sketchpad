(ns sketchpad.buffer_close
	(:use [seesaw meta])
	(:require [sketchpad.file :as file]
			[seesaw.core :as seesaw]
			[sketchpad.tab :as tab]
			[sketchpad.tab-builder :as tab-builder]
			[sketchpad.state :as sketchpad.state]
			[clojure.string :as string]
			[sketchpad.]))

(def app sketchpad.state/app)

(defn close-buffer
"Close the current buffer."
([]
	(close-buffer (tab/current-buffer)))
"Close the specified buffer."
([rsta]
	(let [new-file? (get-meta rsta :new-file)]
		(if new-file?
			;; new save as dialog

			;; save and close

			))))