(defproject sketchpad "0.0.1-SNAPSHOT"
  :description "A light weight IDE for programming with Overtone and Clojure"
  :main sketchpad.core
  :resource-paths ["resources"] ; non-code files included in classpath/jar
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ghostandthemachine/seesaw "1.4.3-SNAPSHOT"]
                 [clooj "0.3.4.2-SNAPSHOT"]
                 [rounded-border "0.0.1-SNAPSHOT"]
                 [rsyntaxtextarea-clojars "0.1.0-SNAPSHOT"]
                 [language-builder "1.0.0-SNAPSHOT"]
                 [auto-complete "0.1.0-SNAPSHOT"]
                 [leiningen "2.0.0-preview7"]
                 [timbre "0.5.1-SNAPSHOT"]
                 [org.apache.commons/commons-io "1.3.2"]]
  :jvm-opts ~(if (= (System/getProperty "os.name") "Mac OS X") ["-Xdock:name=SketchPad"] []))