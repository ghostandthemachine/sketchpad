(ns sketchpad.completion-builder
  (:use [clojure.pprint])
  (:require [clojure.string :as s]
            [clojure.repl :as repl]
            [sketchpad.lein.core.ns :as lein.core.ns])
  (:import (org.fife.ui.autocomplete AutoCompletion VariableCompletion ClojureFunctionCompletion ParameterizedCompletion)
           (org.fife.ui.autocomplete.DefaultCompletionProvider)
           (org.fife.ui.autocomplete.DefaultCompletionProvider)
           (org.fife.ui.autocomplete.demo.CCellRenderer)
           (java.io.File)
           (java.util.Vector)))

(defn ns-publics-to-map [name-space]
  (let [ns-pubs (ns-publics name-space)
        ns-keys (keys ns-pubs)
        ns-vars (vals ns-pubs)
        meta-data (map meta ns-vars)]
    [ns-keys
     ns-vars
     meta-data]))

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
        (.add param-list
              (org.fife.ui.autocomplete.ParameterizedCompletion$Parameter. " " (str arg)))
        (.add param-list
              (org.fife.ui.autocomplete.ParameterizedCompletion$Parameter. " " (str arg)))))
    param-list))

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
            (.setParams completion (create-params-list arg-list))
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

(defn build-project-completions
"Takes a Completion Provider and a class path. Returns the Completion Provider with all ns completions added."
  [provider project-path]
  [provider]
  (let [all-namespaces (lein.core.ns/namespaces-in-dir project-path)]
    (doseq [namespace all-namespaces]
      (add-completions-from-ns provider namespace))
    provider))

