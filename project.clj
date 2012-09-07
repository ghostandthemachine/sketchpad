(defproject sketchpad "0.0.1-SNAPSHOT"
  :description "A light weight IDE for programming with Overtone and Clojure"
  :main sketchpad.core
  :resource-paths ["resources" "resources/english_dic.zip"] ; non-code files included in classpath/jar
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [seesaw "1.4.2"]
                 [clooj "0.3.4.2-SNAPSHOT"]
                 [rsyntaxtextarea-clojars/rsyntaxtextarea-clojars "0.1.0-SNAPSHOT"]
                 [language-builder "1.0.0-SNAPSHOT"]
                 [auto-complete "0.1.0-SNAPSHOT"]
                 [leiningen "2.0.0-preview8"]
                 [org.clojure/data.xml "0.0.6"]
                 [lein-newnew "0.3.5"]
                 [enlive "1.0.1"]        
                 [language-support/language-support "0.1.0-SNAPSHOT"]
                 [rsta_spellchecker/rsta_spellchecker "0.1.0-SNAPSHOT"]])

