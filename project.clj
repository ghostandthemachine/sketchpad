
(defproject sketchpad "0.0.1-SNAPSHOT"
  :description "A light weight IDE for programming with Overtone and Clojure"
  :main sketchpad.core
  :dependencies [[org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/clojure "1.3.0"]
        	       [overtone "0.6.0"]
        		     [franks42/seesaw "1.4.2-SNAPSHOT"]
        		     [clooj "0.3.4.2-SNAPSHOT"]
                 [rounded-border "0.0.1-SNAPSHOT"]
                 [rsyntax-clojars "0.1.0-SNAPSHOT"]
                 [language-builder "1.0.0-SNAPSHOT"]
                 [auto-complete "0.1.0-SNAPSHOT"]
								 [leiningen-core "2.0.0-SNAPSHOT"]
								 ; [classlojure "0.6.5"]
                 [timbre "0.5.1-SNAPSHOT"]
                 [org.lpetit/paredit.clj "0.12.4.STABLE01"]
                 [reply "0.1.0-beta8"]
                 [clj-http "0.4.2"]]
  :jvm-opts ~(if (= (System/getProperty "os.name") "Mac OS X") ["-Xdock:name=Sketchpad"] []))