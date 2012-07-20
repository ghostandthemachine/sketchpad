(ns sketchpad.file
  (:require [seesaw.bind :as bind]
            [sketchpad.project :as project]
            [sketchpad.utils :as utils]
            [clojure.string :as string])
  (:use [seesaw core dev meta]
        [sketchpad.editor-component])
  (:import (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
           (java.awt.event.KeyEvent)
           (javax.swing  JOptionPane)))

(def app sketchpad.state/app)

(defn ends-with? [file-name ext]
  (.endsWith file-name ext))

(defn file-name [file]
  (.getName file))

(defn file-type [file-name]
  ; (let [file-name (file-name file)]
    (cond
      (ends-with? file-name ".clj")
      :clojure
      (ends-with? file-name ".rb")
      :ruby
      (ends-with? file-name ".c")
      :c
      (ends-with? file-name ".tex")
      :latex
      (or
        (ends-with? file-name ".cpp")
        (ends-with? file-name ".h"))
      :cplusplus
      (ends-with? file-name ".sh")
      :unix-shell
      (ends-with? file-name ".html")
      :html
      (ends-with? file-name ".xml")
      :xml
      (ends-with? file-name ".bb")
      :bbcode
      (ends-with? file-name ".py")
      :python
      (ends-with? file-name ".scala")
      :scala
      (ends-with? file-name ".jsp")
      :jsp
      (ends-with? file-name ".php")
      :php
      (ends-with? file-name ".groovy")
      :groovy
      (ends-with? file-name ".mxml")
      :mxml
      (ends-with? file-name ".cs")
      :csharp
      (ends-with? file-name ".sass")
      :sas
      (ends-with? file-name ".css")
      :css
      (ends-with? file-name ".dtd")
      :dtd
      (ends-with? file-name ".perl")
      :perl
      (or
        (ends-with? file-name ".f")
        (ends-with? file-name ".f90")
        (ends-with? file-name ".f95")
        (ends-with? file-name ".f03"))
      :fortran
      (or
        (ends-with? file-name ".dcl")
        (ends-with? file-name ".dcu")
        (ends-with? file-name ".dcr")
        (ends-with? file-name ".dcpil")
        (ends-with? file-name ".dfm")
        (ends-with? file-name ".dof")
        (ends-with? file-name ".dpc")
        (ends-with? file-name ".dpk"))
      :delphi
      (ends-with? file-name ".sql")
      :sql
      (ends-with? file-name ".java")
      :java
      (ends-with? file-name ".liso")
      :list))

(defn file-suffix
  [^File f]
  (let [name (.getName f)
        last-dot (.lastIndexOf name ".")
        suffix (.substring name (inc last-dot))]
    suffix))

(defn text-file?
  [f]
  (and
    (not (some #{(file-suffix f)}
               ["jar" "class" "dll" "jpg" "png" "bmp"]))
    (not (.isDirectory f))))

(defn save-file
[rsta file] 
  (try
      (with-open [writer (BufferedWriter.
                           (OutputStreamWriter.
                             (FileOutputStream. file)
                             "UTF-8"))]
        (.write rsta writer)
      true)
    (catch Exception e
      (do
        (println e)
        (JOptionPane/showMessageDialog
          nil "Unable to save file."
          "Oops" JOptionPane/ERROR_MESSAGE)))))


(defn set-global-rsta!
  [app-atom comp]
  (let [rsta (first (select comp [:.syntax-editor]))]
    (swap! app-atom (fn [app] (assoc app :doc-text-area rsta)))))

(defn get-file-state-by-tab-index [app i]
  (@(get-meta (select (.getComponentAt (app :editor-tabbed-pane) i) [:#editor]) :state) :clean))



(defn new-file! []
  (try
    (when-let [new-file (utils/choose-file (@app :frame) "New file" (project/current-project app) false)]
      (utils/awt-event
        (let [path (.getAbsolutePath new-file)]
          (spit path "")
          (println "create new file " path)))
        new-file)
      (catch Exception e (do (JOptionPane/showMessageDialog nil
                               "Unable to create file."
                               "Oops" JOptionPane/ERROR_MESSAGE)
                           (.printStackTrace e)))))

(defn save-file-as []
  (try
    (when-let [new-file (utils/choose-file (@app :frame) "Save file as" (project/current-project) false)]
      (utils/awt-event
        (let [path (.getAbsolutePath new-file)]
          (spit path "")))
        new-file)
      (catch Exception e (do (JOptionPane/showMessageDialog nil
                               "Unable to create file."
                               "Oops" JOptionPane/ERROR_MESSAGE)
                           (.printStackTrace e)))))






