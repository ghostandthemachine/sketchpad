(ns sketchpad.repl-new
	(:use [seesaw meta])
	(:import 
           (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream))
	(:require [sketchpad.repl-tab-builder :as repl-builder]
			[seesaw.core :as seesaw]
			[sketchpad.tab :as tab]
			[sketchpad.state :as sketchpad.state]
			[leiningen.core.project :as lein-project]
			[clojure.string :as string]))



(defn repl-from-project! 
"Create a new outside REPL process for a given Leiningen project."
([lein-project]
	(let [new-repl-buffer (repl-builder/build-new-repl-tab! lein-project)])))