;; LOAD ME WITH
;; (load-file "/home/john/hobby-code/require-all-snippet.clj")

;; This file conditions a repl in various ways that I do all the time.

;; Firstly we want to 'require' all the namespaces on the classpath
;; This ensures that find-doc and the like will work
(require 'clojure.contrib.find-namespaces)

;; Some namespaces may fail to load, so catch any exceptions thrown
(defn- require-may-fail [ns]
  (try

   (print "Attempting to require " ns ": ")
   (require ns)
   (println "success")
   (catch Exception e (println "couldn't require " ns "\nException\n" e "\n\n"))))


;; Generally we'd want clojure.*, clojure.contrib.*, and any project-specific namespaces
(defn require-all-namespaces-starting-with [strng]
  (doall (map require-may-fail 
              (filter #(. (str %) startsWith strng) 
                      (clojure.contrib.find-namespaces/find-namespaces-on-classpath)))))


;; The functions in these namespaces are so useful at the REPL that I want them 'use'd.
;; I.e. I want to be able to type 'source' rather than 'clojure.contrib.repl-utils/source'
(use 'clojure.contrib.repl-utils)
(use 'clojure.inspector)
(use 'clojure.contrib.pprint)
(use 'clojure.contrib.repl-utils)
(use 'clojure.contrib.trace)

;; It drives me up the wall that it's (doc re-pattern) but (find-doc "re-pattern").
;; Can use macros so that (fd re-pattern) (fd "re-pattern") and (fd 're-pattern) all mean the same thing
(defn- stringify [x]
  (println "stringify given" (str x))
  (let [s  (cond (string? x) x
                 (symbol? x) (str x)
                 (and (list? x) (= (first x) 'quote)) (str (second x))
                 :else (str x)) ]
    (println (str "translating to: \"" s "\""))
    s))



;; Sometimes I like to ask which public functions a namespace provides.
(defn- ns-publics-list [ns] (#(list (ns-name %) (map first (ns-publics %))) ns))
;; And occasionally which functions it pulls in (with refer or use)
(defn- ns-refers-list  [ns] (#(list (ns-name %) (map first (ns-refers %))) ns))


;; Nice pretty-printed versions of these functions, accepting strings, symbols or quoted symbol
(defmacro list-publics     
  ([]   `(pprint (ns-publics-list *ns*)))
  ([symbol-or-string] `(pprint (ns-publics-list (find-ns (symbol (stringify '~symbol-or-string)))))))

(defmacro list-refers
  ([]   `(pprint (ns-refers-list *ns*)))
  ([symbol-or-string] `(pprint (ns-refers-list (find-ns (symbol (stringify '~symbol-or-string)))))))

;; List all the namespaces
(defn list-all-ns [] (pprint (map ns-name (all-ns))))

;; List all public functions in all namespaces!
(defn list-publics-all-ns [] (pprint (map #(list (ns-name %) (map first (ns-publics %))) (all-ns))))

;; With all the namespaces loaded, find-doc can be overwhelming.
;; This is like find-doc, but just gives the associated names.

(defn- find-doc-names
  "Prints the name of any var whose documentation or name contains a match for re-string-or-pattern"
  [re-string-or-pattern]
    (let [re  (re-pattern re-string-or-pattern)]
      (doseq [ns (all-ns)
              v (sort-by (comp :name meta) (vals (ns-interns ns)))
              :when (and (:doc ^v)
                         (or (re-find (re-matcher re (:doc ^v)))
                             (re-find (re-matcher re (str (:name ^v))))))]
               (print v "\n"))))





;;find symbol or string in docs 
(defmacro fd [symbol-or-string] `(find-doc (stringify '~symbol-or-string)))

(defmacro fdn [symbol-or-string] `(find-doc-names (stringify '~symbol-or-string)))



;;debugging macro                                try: (* 2 (dbg (* 3 4)))
(defmacro dbg [x] `(let [x# ~x] (do (println '~x "->" x#) x#))) 

;;and pretty-printing version 
(defmacro ppdbg [x]`(let [x# ~x] (do (println "--")(pprint '~x)(println "->")(pprint x#) (println "--") x#))) 


;;and one for running tests 
(defmacro run-test [fn] `(test (resolve '~fn)))


;; Sometimes it's nice to check the classpath
(defn- get-classpath []
   (sort (map (memfn getPath) 
              (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader))))))

(defn print-classpath []
  (clojure.contrib.pprint/pprint (get-classpath)))

(defn get-current-directory []
  (. (java.io.File. ".") getCanonicalPath))


;;require everything from clojure and clojure.contrib, so that find-doc can find it
(require-all-namespaces-starting-with "clojure")

;;print the classpath
(println "Classpath:")
(print-classpath)

(println "Current Directory" (get-current-directory))

;;print the public functions in the current namespace
(println "Current Namespace")
(list-publics)


;;hint on how to require project specific namespaces
(println "to require all namespaces starting with example:")
(println "(require-all-namespaces-starting-with \"example\")")