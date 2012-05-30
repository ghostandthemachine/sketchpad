(defproject overtone-sketchpad "0.0.1-SNAPSHOT"
  :description "A light weight IDE for programming with Overtone and Clojure"
  :main overtone-sketchpad.core
  :dependencies [[org.clojure/clojure "1.3.0"]
  				 [overtone "0.6.0"]
  				 [franks42/seesaw "1.4.2-SNAPSHOT"]
  				 [upshot "0.0.0-SNAPSHOT"]
  				 [clooj "0.3.4.1-SNAPSHOT"]
  				 [com.fifesoft/rsyntaxtextarea "2.0.2"]
  				 [com.oracle/javafx-runtime "2.0"]
                 [com.github.insubstantial/substance "7.1"]]
  :jvm-opts ~(if (= (System/getProperty "os.name") "Mac OS X") ["-Xdock:name=Sketchpad"] [])
  :java-source-paths ["src"]
  :java-source-path "src"
  ;; Use this for Leiningen version 1
  :resources-path "resource"
  ;; Use this for Leiningen version 2
  :resource-paths ["resource"]
  )
