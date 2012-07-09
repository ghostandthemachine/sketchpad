(ns sketchpad.repl-writer
	(:use [sketchpad buffer-edit utils])
	(:require [sketchpad.repl :as srepl])
	(:import [java.io.StringWriter])
	)



(defn repl-writer [rsta]
	(let [writer (proxy [java.io.StringWriter] []
		(append [c]
;			(proxy-super append c)
			(append-text rsta (str c))
			)

		(append [csq]
;			(proxy-super append csq)
			(append-text rsta (str csq))
			)

		(close []
			(proxy-super close))

		(flush [] 
			(proxy-super flush))

		(getBuffer []
			(proxy-super getBuffer))

		(toString [] 
			(proxy-super toString))

		(write [cbuf off len]
			(proxy-super write cbuf off len))

		(write [c]
			(proxy-super write c)))]
		writer))

		
		