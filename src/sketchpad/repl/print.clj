(ns sketchpad.repl.print
  (:require [clojure.string :as string]
            [sketchpad.buffer.action :as buffer.action]
            [sketchpad.wrapper.rsyntaxtextarea :as rsta]
            [sketchpad.util.tab :as tab]
            [sketchpad.state.state :as state]))

(defn prompt
([]
	(prompt (get-in (:application-repl @state/app) [:component :text-area]) nil))
([rsta prompt-ns]
  (buffer.action/append-text rsta (str \newline  "sketchpad.user=> "))
  (.setCaretPosition rsta (.getLastVisibleOffset rsta))))

(defn pln 
[& values]
  (buffer.action/append-text (get-in (:application-repl @state/app) [:component :text-area]) (str values \n)))

(defn append-command
"Takes a command string to append, and the offset to possibly go back with and appends it to the editor repl."
	([cmd-str caret-offset]
		(prompt)
		(buffer.action/append-text-update
			(get-in (:application-repl @state/app) 
			[:component :text-area]) 
			cmd-str)
		(buffer.action/buffer-move-pos-by-char
			(get-in (:application-repl @state/app)
			[:component :text-area])
			caret-offset)))