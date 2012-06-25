(ns sketchpad.file-manager
	(:require [seesaw.bind :as bind]
						[clojure.string :as string])
	(:use [seesaw core dev meta]
				[sketchpad.editor-component]
				[sketchpad.button-tab]
				[sketchpad.tab-manager])
	(:import (java.io StringReader File)
					 (java.awt.event.KeyEvent)))

(defn ends-with? [file-name ext]
	(.endsWith file-name ext))

(defn file-name [file]
	(.getName file))

;;; convert file extension to supported rsyntax text area symbol
(defn file-type [file]
	(let [file-name (file-name file)]
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
			:list)))

(defn file-suffix [^File f]
  (let [name (.getName f)
             last-dot (.lastIndexOf name ".")
             suffix (.substring name (inc last-dot))]
    suffix))

(defn text-file? [f]
  (not (some #{(file-suffix f)}
             ["jar" "class" "dll" "jpg" "png" "bmp"])))
			
(defn set-global-rsta! [app-atom comp]
	(let [rsta (first (select comp [:.syntax-editor]))]
		(swap! app-atom (fn [app] (assoc app :doc-text-area rsta)))))

(defn mark-tab-dirty! [tabbed-panel i]
	(title-at! tabbed-panel i (str (title-at tabbed-panel i) "*")))

(defn mark-current-tab-dirty! [tabbed-panel i]
	(mark-tab-dirty! tabbed-panel (.getSelectedIndex tabbed-panel)))

(defn new-file-tab! [app-atom file]
	(let [app @app-atom]
		(if (text-file? file)
			(if (open? app (file-name file))
				;; already open so just show it
				(show-tab! app-atom (index-of app (file-name file)) file)
				;; otherwise create a new tab and set the syntax style
				(do 
					(let [tabbed-panel (app :editor-tabbed-panel)
								container (make-editor-component)
								new-file-name (str (file-name file))
								rsta-comp (select container [:#editor])]
						;; attach the file to the component for easy saving
						(put-meta! rsta-comp :file file)
						;; set the text area text from file
						(let [txt (slurp file)
			            rdr (StringReader. txt)]
			        (.read rsta-comp rdr nil))
						;; set the new text area syntax
						(config! rsta-comp :syntax (file-type file))
						(add-tab! tabbed-panel new-file-name container)
						;; set custom tab
						(let [index-of-new-tab (index-of app new-file-name)
									tab (button-tab app tabbed-panel)
									tab-label (first (select tab [:.tab-label]))
									tab-state (get-meta rsta-comp :state)]
    					;; add state listener to this rta
					    (listen rsta-comp
					      :key-typed (fn [e]
					        (if (= :clean @tab-state)
					          (do
					          	(println (java.awt.event.KeyEvent/getKeyText (.getKeyCode e)))
					          	; (println (java.awt.event.KeyEvent/getKeyModifierText (.getModifiers e)))
					          	(mark-tab-dirty! tabbed-panel index-of-new-tab)
				              (swap! (get-meta rsta-comp :state) (fn [_] :dirty))))))

							;; set the component in the new tab
							(.setTabComponentAt tabbed-panel index-of-new-tab tab)
							;; add file and index to app map
							(swap! (@app-atom :current-files) (fn [files] (assoc files index-of-new-tab file)))
							;; bring the new tab to the front
							(show-tab! app-atom index-of-new-tab file))))))))
