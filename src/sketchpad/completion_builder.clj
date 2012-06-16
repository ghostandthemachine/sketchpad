(ns sketchpad.completion-builder
  (:use [clojure.pprint])
  (:require [clojure.string :as s])
  (:import (org.fife.ui.autocomplete AutoCompletion ClojureFunctionCompletion ParameterizedCompletion)
           (org.fife.ui.autocomplete.DefaultCompletionProvider)
           (org.fife.ui.autocomplete.demo.CCellRenderer)
           (java.io.File)
           (java.util.Vector)))

(defn ns-publics-to-map [name-space] 
	(let [meta-data (map meta (vals (ns-publics name-space)))]
;		(reduce (fn [m vs] (assoc-in m [(str (:orig-ns vs)) (:name vs)] vs)) {} meta-data)
		meta-data))

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
  [pv]
  (let [p-list (java.util.Vector. )]
    (dotimes [n (count pv)]
      (.add p-list
		(org.fife.ui.autocomplete.ParameterizedCompletion$Parameter. " " (str (nth pv n)))))
    p-list))

(defn add-function-completion
  [provider var]  
  (let [arglists (into [] (:arglists var))
        num-arglists (count arglists)
        completion (ClojureFunctionCompletion. provider (str (:name var)) (str \space))]
     (dotimes [n num-arglists]
 	  ;; convert params
 	  (.setParams completion (create-params-list (nth arglists n)))
 	  ;; doc/description
 	  (.setReturnValueDescription completion (:doc var)))
	(.addCompletion provider completion)))
  

(defn add-completions-from-ns
  [provider ns]
  (let [ns-map (ns-publics-to-map ns)]
    (dotimes [n (count ns-map)]
      (add-function-completion provider (nth ns-map n)))))

(defn add-all-ns-completions
  [provider]
  (dotimes [n (count (all-ns))]
	(add-completions-from-ns provider (nth (all-ns) n))))

