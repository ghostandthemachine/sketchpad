; Copyright (c) 2011, Arthur Edelstein
; All rights reserved.
; Eclipse Public License 1.0
; arthuredelstein@gmail.com

(ns sketchpad.help.help
  (:import (java.io LineNumberReader InputStreamReader PushbackReader)
           (clojure.lang RT Reflector)
           (java.io File))
  (:use [seesaw.core :only (select)]
        [sketchpad.util.brackets]
        [clj-inspector.jars :only (clj-sources-from-jar jar-files)]
        [clj-inspector.vars :only (analyze-clojure-source
                                    parse-ns-form)]
        [sketchpad.util.tab])
  (:require [clojure.string :as string]))


; from http://clojure.org/special_forms
(def special-forms
  {"def" "(def symbol init?)"
   "if"  "(if test then else?)"
   "do"  "(do exprs*)"
   "let" "(let [bindings* ] exprs*)"
   "quote" "(quote form)"
   "var" "(var symbol)"
   "fn"  "(fn name? [params* ] exprs*)"
   "loop" "(loop [bindings* ] exprs*)"
   "recur" "(recur exprs*)"
   "throw" "(throw expr)"
   "try"   "(try expr* catch-clause* finally-clause?)"
   "catch" "(catch classname name expr*)"
   "monitor-enter" "Avoid!"
   "monitor-exit"  "Avoid!"})

(defn present-item [item]
  (str (:name item) " [" (:ns item) "]"))

(defn make-var-super-map [var-maps]
  (into {}
        (for [var-map var-maps]
          [[(:ns var-map) (:name var-map)] var-map])))

(defn classpath-to-jars [project-path classpath]
  (apply concat
    (for [item classpath]
      (cond (.endsWith item "*") (jar-files (apply str (butlast item)))
            (.endsWith item ".jar") (list (File. item))
            :else (jar-files item)))))

(defn get-sources-from-jars [project-path classpath]
   (->> (classpath-to-jars project-path classpath)
       (mapcat clj-sources-from-jar)
       merge
       vals))

(defn get-sources-from-clj-files [classpath]
  (map slurp
       (apply concat
              (for [item classpath]
                (let [item-file (File. item)]
                  (when (.isDirectory item-file)
                    (filter #(.endsWith (.getName %) ".clj")
                            (file-seq item-file))))))))

(defn get-var-maps [project-path classpath]
  (make-var-super-map
      (mapcat analyze-clojure-source
              (concat
                (get-sources-from-jars project-path classpath)
                (get-sources-from-clj-files classpath)))))

(def non-token-chars [\; \~ \@ \( \) \[ \] \{ \} \  \. \newline \/ \" \'])

(defn current-ns-form [app]
  (let [tabbed-panel (app :buffer-tabbed-panel)
        current-rta (select (current-tab tabbed-panel) [:#editor])]
    (-> current-rta .getText read-string)))

(defn ns-available-names [app]
  (try 
    (parse-ns-form (current-ns-form app))
    (catch IllegalArgumentException e)
    (finally {})))

(defn matching-vars [app token]
  (into {} (filter #(= token (second (first %)))
                   (-> app :repl deref :var-maps deref))))

(defn var-from-token [app current-ns token]
  (when token
    (if (.contains token "/")
      (vec (.split token "/"))
      (or ((ns-available-names app) token)
          [current-ns token]))))

(defn find-form-string [text pos]
  (let [[left right] (find-enclosing-brackets text pos)]
    (when (> (.length text) left)
      (.substring text (inc left)))))

(defn head-token [form-string]
  (when form-string
    (second
      (re-find #"(.*?)[\s|\)|$]"
               (str (.trim form-string) " ")))))
               
(defn token-from-caret-pos [text pos]
  (head-token (find-form-string text pos)))

(defn arglist-from-var-map [m]
  (or
    (when-let [args (:arglists m)]
      (str (-> m :ns) "/" (:name m) ": " args))
    ""))

(defn arglist-from-token [app ns token]
  (or (special-forms token)
      (-> app :repl deref :var-maps
          deref (get (var-from-token app ns token))
          arglist-from-var-map)))

(defn arglist-from-caret-pos [app ns text pos]
  (let [token (token-from-caret-pos text pos)]
    (arglist-from-token app ns token)))

