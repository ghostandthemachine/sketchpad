(ns sketchpad.editor.buffer
	(:use [seesaw meta])
	(:import 
           (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
           (org.fife.rsta.ac.html HtmlLanguageSupport)
           (org.fife.rsta.ac.java JavaLanguageSupport)
           (org.fife.rsta.ac.c CLanguageSupport)
           (org.fife.rsta.ac.groovy GroovyLanguageSupport)
           (org.fife.rsta.ac.js JavaScriptLanguageSupport)
           (org.fife.rsta.ac.perl PerlLanguageSupport)
           (org.fife.rsta.ac.php PhpLanguageSupport)
           (org.fife.rsta.ac.jsp JspLanguageSupport)
           (org.fife.rsta.ac.xml XmlLanguageSupport)
           (org.fife.rsta.ac.sh ShellLanguageSupport))
	(:require [sketchpad.editor.build :as editor.build]
			[sketchpad.file.file :as file]
			[seesaw.core :as seesaw]
			[sketchpad.util.tab :as tab]
			[sketchpad.project.project :as sketchpad.project]
			[sketchpad.state.state :as state]
			[sketchpad.tree.utils :as tree.utils]
			[leiningen.core.project :as lein-project]
			[sketchpad.auto-complete.auto-complete :as auto-complete]
			[clojure.string :as string]
			[seesaw.bind :as bind]
			[seesaw.core :as seesaw]))

(defn update-buffer-info-file-title [title]
	(swap! (@state/app :doc-title-atom) (fn [lbl] title)))

(defn add-auto-completion-from-type
	[buffer]
	(when @(:file buffer)
		(let [suffix (last (clojure.string/split (.getName @(:file buffer)) #"\."))]
			(cond
				(= suffix "clj")
					(auto-complete/install-auto-completion (get-in buffer [:component :text-area]))
				(= suffix "java")
					(let [java-lang-support (JavaLanguageSupport. )]
						(.install java-lang-support (get-in buffer [:component :text-area])))
				(= suffix "c")
					(let [c-lang-support (CLanguageSupport. )]
						(.install c-lang-support (get-in buffer [:component :text-area])))
				(= suffix "groovy")
					(let [groovy-lang-support (GroovyLanguageSupport. )]
						(.install groovy-lang-support (get-in buffer [:component :text-area])))
				(= suffix "js")
					(let [js-lang-support (JavaScriptLanguageSupport. )]
						(.install js-lang-support (get-in buffer [:component :text-area])))
				(= suffix "perl")
					(let [perl-lang-support (PerlLanguageSupport. )]
						(.install perl-lang-support (get-in buffer [:component :text-area])))
				(= suffix "php")
					(let [php-lang-support (PhpLanguageSupport. )]
						(.install php-lang-support (get-in buffer [:component :text-area])))
				(= suffix "jsp")
					(let [jsp-lang-support (JspLanguageSupport. )]
						(.install jsp-lang-support (get-in buffer [:component :text-area])))
				; not working correctly. There is some print out bug when looking at ac clomplets list
				; (= suffix "sh")
				; 	(let [sh-lang-support (ShellLanguageSupport. )]
				; 		(.install sh-lang-support (get-in buffer [:component :text-area])))
				(= suffix "xml")
					(let [xml-lang-support (XmlLanguageSupport. )]
						(.install xml-lang-support (get-in buffer [:component :text-area])))
				(= suffix "html")
					(let [html-lang-support (HtmlLanguageSupport. )]
						(.install html-lang-support (get-in buffer [:component :text-area])))))))

(defn init-buffer-tab-state [buffer]
	(let [text-area (:text-area buffer)]
	  (tab/focus-buffer buffer)
	  (update-buffer-info-file-title (tab/title))
	  (tab/mark-tab-clean! buffer)
	  (seesaw/invoke-later
		  (.discardAllEdits text-area)
		  (.setCaretPosition text-area 0))))

(defn selected-file-path []
  (when-let [tree-path (-> @state/app :docs-tree .getSelectionPaths first)]
    (-> tree-path .getLastPathComponent .getUserObject .getAbsolutePath)))

(defn update-buffer-label-from-file [buffer file-path]
	(let [file (File. file-path)]
		(swap! (:title buffer) (fn [_] (.getName file)))
		(seesaw/config! (get-in buffer [:tab :label]) :text (.getName file))
		(tab/buffer-title! buffer (.getName file))))

(defn update-buffer-syntax-style [buffer file-path]
	(seesaw/config! buffer :syntax (file/file-type file-path)))

(defn load-file-into-buffer [project buffer file-path]
	(when-let [txt (slurp file-path)]
		(let[rdr (StringReader. txt)
			file (File. file-path)
			text-area (:text-area buffer)]
		(.read text-area rdr nil)
		(update-buffer-syntax-style text-area file-path)
		(update-buffer-label-from-file buffer file-path)
		(swap! (:title buffer) (fn [_] (.getName file)))
		(swap! (:file buffer) (fn [_] file))
		(reset! (:new-file? buffer) false)
		(add-auto-completion-from-type buffer))))

(defn open-buffer [file-path project-path]
	(let [project (sketchpad.project/project-from-path project-path)
		  buffer (editor.build/project-buffer-tab project-path)]
		(load-file-into-buffer project buffer file-path)
		(init-buffer-tab-state buffer)
		(sketchpad.project/add-buffer-to-project project-path buffer)
		(sketchpad.project/add-buffer-to-app buffer)
		(tab/show-buffer buffer)))

(defn blank-clj-buffer!
	([] (blank-clj-buffer! nil))
	([parent-dir] 
	(let [buffer (editor.build/scratch-buffer-tab "sketchpad-tmp")]
		(init-buffer-tab-state buffer)
		(sketchpad.project/add-buffer-to-app buffer)
		(tab/show-buffer buffer))))

(defn new-project-buffer!
"Create a new buffer for a loaded project."
	[project-path]
	(let [buffer (editor.build/project-buffer-tab project-path)]
		(init-buffer-tab-state buffer)
		(sketchpad.project/add-buffer-to-project project-path buffer)
		(sketchpad.project/add-buffer-to-app buffer)
		(tab/show-buffer buffer)))
