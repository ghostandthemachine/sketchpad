(ns sketchpad.completion-builder
  (:use [clojure.pprint])
  (:require [clojure.string :as s]
  					[clojure.repl :as repl])
  (:import (org.fife.ui.autocomplete AutoCompletion VariableCompletion ClojureFunctionCompletion ParameterizedCompletion)
           (org.fife.ui.autocomplete.DefaultCompletionProvider)
           (org.fife.ui.autocomplete.demo.CCellRenderer)
           (java.io.File)
           (java.util.Vector)))

(defn ns-publics-to-map [name-space] 
	(let [ns-pubs (ns-publics name-space)
				ns-keys (keys ns-pubs)
				ns-vars (vals ns-pubs)
				meta-data (map meta ns-vars)]
;		(reduce (fn [m vs] (assoc-in m [(str (:orig-ns vs)) (:name vs)] vs)) {} meta-data)
		[ns-keys
		 ns-vars
		 meta-data]))

;(def clj-lang-start (str (slurp "resources/clojure-header.txt")))
(def clj-lang-start "")

(def clj-lang-end
  (str "\t</keywords>\n</api>"))

(defn dbl-q
  [word]
  (str "\"" word "\""))

(def tmp (first (ns-publics-to-map 'clojure.repl)))

(defn keyword-s
  [var]
  (let [name (str (:name var))
        type "function"
        return-type " "      
        def-in (str (:ns var))]
    (str
      "<keyword name=" (dbl-q name)
      " type=" (dbl-q type)
      " returnType=" (dbl-q return-type)
      " definedIn=" (dbl-q def-in)
      ">\n")))

(defn take-str
  ([n s]
  (take-str 0 n s))
  ([start end s]
    (let [dropper (str (s/join (drop start s)))]
    (str (s/join (take (- end start) dropper))))))

(defn param-s
  [p]
  (let [param (str p)]
    (cond 
      (not= "&" param)
        (str "\t<param type=" (dbl-q "function") " name=" (dbl-q param) "/>\n")
      (= "&" param)
      (str "")
;        (str "\t<param type=" (dbl-q "function") " name=" (dbl-q "&amp;") "/>\n")
      (= "[&" (take-str 2 param))    
         (str "\t<param type=" 
              (dbl-q "function")
              " name=" 
              (str (s/join (concat "[&amp;" (take-str 3 (count param) param)))
              "/>\n")))))

(defn parse-params
  [pv]
  (let [return-string (atom "")]
    (dotimes [n (count pv)]
      (swap! return-string
        (fn [s] 
          (s/join (concat s (param-s (nth pv n)))))))
    @return-string))

(defn create-keyword-xml
  [var]  
  (let [return-string (atom "")
        arglists (into [] (:arglists var))
        num-arglists (count arglists)]
    (dotimes [n num-arglists]
      (swap! return-string
      (fn [s]
        (let [first-char (s/join (take 1 (str (:name var))))
              allowed? (not= first-char "<")]
          (if allowed?
            (concat s 
              (str
                ;; def keyword
                (keyword-s var)
                ;; convert params
                "\t<params>\n"
                (parse-params (nth arglists n)) "\n"
                "\t</params>\n"      
                ;; doc/description
                "\t<desc><![CDATA["
                (str "\t" (:doc var))
                "]]></desc>\n"   
          
                ;; wrap up keyword def
                "</keyword>\n")))))))
       (str (s/join @return-string))))

(defn create-xml-from-ns
  [ns]
  (let [return-string (atom "")
        ns-map (ns-publics-to-map ns)]
    (dotimes [n (count ns-map)]
      (swap! return-string
        (fn [s] 
          (str (s/join (concat s (create-keyword-xml (nth ns-map n))))))))
    @return-string))

(defn create-all-ns-xml
  []
  (let [rs (atom clj-lang-start)]
  (dotimes [n (count (all-ns))]
    (swap! rs
      (fn [s]
        (str (s/join (concat s (create-xml-from-ns (nth (all-ns) n))))))))
    (swap! rs
      (fn [s]
        (str (s/join (concat s clj-lang-end)))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; non xml route ;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn add-nl
  [s]
  (str (s/join (concat s (str \newline)))))

(defn add-space
  [s]
  (str (s/join (concat s (str \space)))))

(defn create-params-list
  [args]
  (let [param-list (java.util.Vector. )
  			opts? (atom false)]
    (doseq [arg args]
      (if (= "&" (str arg))
        (swap! opts? (fn [_] true))
        (swap! opts? (fn [_] false)))
	  	(if opts?
	  		;; prepend & on opt arg
	      (.add param-list
					(org.fife.ui.autocomplete.ParameterizedCompletion$Parameter. " " (str arg)))
	    	;; non opt arg
	    	(.add param-list
					(org.fife.ui.autocomplete.ParameterizedCompletion$Parameter. " " (str arg)))))
	  param-list))

;;; from clj-inspector sample
(defn var-type
  "Determing the type (var, function, macro) of a var from the metadata and
return it as a string. (Borrowed from autodoc.)"
  [v]
  (cond (:macro (meta v)) "macro"
        (= (:tag (meta v)) clojure.lang.MultiFn) "multimethod"
        (:arglists (meta v)) "function"
        :else "var"))

(defn add-function-completion
  [provider sym var]    
  (let [var-meta (meta var)
        completion-list (java.util.Vector. )
        var-type (var-type var)]
    (cond
     (= var-type "function")
      (do
        (let [completion (ClojureFunctionCompletion. provider (str (:name var-meta)) (str \space))
              arglists (into [] (:arglists var-meta))]
          (doseq [arg-list arglists]
            ;; convert params
            (.setParams completion (create-params-list arg-list))
            ;; doc/description
            (.setReturnValueDescription completion (:doc var-meta))
            (if (repl/source-fn sym)
              (.setReturnValueSourceDescription 
                  completion 
                  (s/replace
                    (repl/source-fn sym)
                    (str "\n")
                    (str "<br/>"))))
            (.setDefinedIn completion (str (:ns var-meta)))
            (if (not (.contains completion-list completion))  
              (.add completion-list completion)))
            (.addCompletions provider completion-list)))
      (= var-type "var")
        (do
          (let [var-completion (VariableCompletion. provider (str (:name var-meta)) (str \space))]
            (.setDefinedIn var-completion (str (:ns var-meta)))
            ;; doc/description
            (.addCompletion provider var-completion))))))


(defn add-completions-from-ns
  [provider ns]
    (doseq [[k v] (ns-publics ns)]
      (add-function-completion provider k v)))

(defn add-all-ns-completions
  [provider]
  (let [all-namespaces (all-ns)]
    (doseq [namespace all-namespaces]
      (add-completions-from-ns provider namespace))))

