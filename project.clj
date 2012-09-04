(defproject sketchpad "0.0.1-SNAPSHOT"
  :description "A light weight IDE for programming with Overtone and Clojure"
  :main sketchpad.core
  :resource-paths ["resources" "resources/english_dic.zip"] ; non-code files included in classpath/jar
  :dependencies [[org.clojure/clojure "1.4.0"]
  					     ; [org.clojure/clojure-contrib "1.2.0"]
                 [seesaw "1.4.2"]
                 [clooj "0.3.4.2-SNAPSHOT"]
                 [rsyntaxtextarea-clojars/rsyntaxtextarea-clojars "0.1.0-SNAPSHOT"]
                 [language-builder "1.0.0-SNAPSHOT"]
                 [auto-complete "0.1.0-SNAPSHOT"]
                 [leiningen "2.0.0-preview7"]
                 [org.clojure/data.xml "0.0.6"]
                 [lein-newnew "0.3.4"]                 
                 [org.jruby/jruby-complete "1.6.6"]
                 [org.danlarkin/clojure-json "1.2-SNAPSHOT"]
                 [language-support/language-support "0.1.0-SNAPSHOT"]
                 [rsta_spellchecker/rsta_spellchecker "0.1.0-SNAPSHOT"]]
  :jvm-opts ~(if (= (System/getProperty "os.name") "Mac OS X") ["-Xdock:name=SketchPad"] []))

