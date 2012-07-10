;(ns sketchpad.user
;	(:require [sketchpad.tab-manager :as tab]
;			  [sketchpad.rsyntaxtextarea :as rsta]
;			  [sketchpad.core :as core])
;)
;
;(def app @core/current-app)
;
;(defn help 
;([]
;	(println)
;	(println "Sketchapd Help")
;	(println)
;	(println "useful commands:")
;	(println)
;	(println  "\t :file")
;	(println  "\t :project"))
;([kw]
;	(println)
;	(println "Sketchapd Help")
;	(println)
;	(cond
;		(= kw :file)
;			(println "file helper functions:")
;			(println)
;			(println "\t :open/:o")
;			(println "\t :close/:c")
;		(= kw :project)
;			(println "project helper functions:")
;			(println)
;			(println "\t :open/:o")
;			(println "\t :close/:c")
;	)))
;
;;; clojure tools
;
;(defn preflect [obj]
;	(clojure.pprint/pprint (clojure.reflect/reflect obj)))
;
;
;;; sketchpad tools
;
;(def component-look-up-table 
;	{
;	:current (tab/current-text-area (:editor-tabbed-panel app))
;	:repl (:editor-repl app)
;
;	})
;
;(defn current-rsta []
;	(:current component-look-up-table))
;
;(defn make-icon []
;	
;)

