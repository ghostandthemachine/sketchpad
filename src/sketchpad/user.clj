(ns sketchpad.user
	(:refer-clojure :exclude [find replace])
	(:use [seesaw meta dev]
        [seesaw.core :exclude [height width]]
	      [clojure.repl]
   	    [clojure.java.shell]
	      [sketchpad.config.config]
        [sketchpad.tree.tree]
        [sketchpad.buffer.action]
        [sketchpad.util.brackets]
        [sketchpad.buffer.token]
        [sketchpad.system.desktop]
	      [sketchpad.auto-complete.template])
	(:require [sketchpad.util.tab :as tab]
            [sketchpad.repl.info-utils :as repl.info-utils]
			      [sketchpad.wrapper.rsyntaxtextarea :as rsta]
			      [sketchpad.core :as core]
	          [sketchpad.wrapper.gutter :as gutter]
		        [sketchpad.buffer.search :as search]
	          [sketchpad.project.project :as project]
	          [sketchpad.tree.utils :as tree.utils]
	          [sketchpad.project.form :as project.form]
			      [clojure.pprint :as pprint]
			      [clojure.stacktrace :as stack-trace]
			      [leiningen.new :as lein-new]
			      [seesaw.dev :as seesaw.dev]
			      [clojure.java.io :as io]
            [sketchpad.buffer.grep :as buffer.grep]
            [clojure.string :as string])
	(:import 	(org.fife.ui.rsyntaxtextarea RSyntaxTextAreaEditorKit)
			 		(org.fife.ui.rtextarea RTextAreaEditorKit)
			 		(org.fife.ui.rsyntaxtextarea.RSyntaxUtilities)
			 		(org.fife.ui.rsyntaxtextarea.RSyntaxTextArea)
			 		(java.awt.event ActionEvent)))

(def app sketchpad.state.state/app)

(defn projects [] (:projects @app))

(defn pp [& args]
	(pprint/pprint args))

(defn stack-trace []
	(stack-trace/print-stack-trace *e))

(defn st []
	(stack-trace))

(defn preflect [obj]
	(clojure.pprint/pprint (clojure.reflect/reflect obj)))

(defn current-text []
"return the text from the current buffer component"
	(try
		(.getText (tab/current-text-area))
		(catch java.lang.IllegalArgumentException e
			(println "no buffer open editor"))))

(defn repls []
  (mapcat :repls @(:projects @app)))

(defn buffers []
  (mapcat :buffers @(:projects @app)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Cursor and mark position
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cursor-point
  "Return the current position of the cursor as a character count."
([]
  (cursor-point (tab/current-text-area)))
([buffer]
  (buffer-cursor-point buffer)))

(defn cursor-pos
  "Returns the position of the cursor [<column> <line>]."
([]
  (buffer-cursor-pos (tab/current-text-area)))
([buffer]
  (buffer-cursor-pos buffer)))

(defn set-cursor-pos!
  "Set the position of the cursor."
  [col line])

(defn current-col
  "Return the column number of the cursor in the current buffer."
([]
  (current-col (tab/current-text-area)))
([buffer]
  (first (cursor-pos buffer))))

(defn current-line
  "Return the line number of the cursor in the current buffer."
([]
  (current-line (tab/current-text-area)))
([buffer]
  (second (cursor-pos buffer))))
  
;(defn goto-pattern
;  [regex-pattern]
;  (search (str regex-pattern)))

(defn goto-next-char
  []
  (buffer-goto-next-char (tab/current-text-area)))

(defn goto-prev-char
  []
  (buffer-goto-prev-char (tab/current-text-area)))

(defn goto-nth-char
  [n]
  (buffer-goto-prev-char (tab/current-text-area) n))

(defn goto-prev-word
  [])

(defn goto-next-line
  [])

(defn goto-prev-line
  [])

(defn goto-next-paragraph
  [])

(defn goto-prev-paragraph
  [])


(defn push-mark
  "Push the current position onto the mark stack."
  [])

(defn pop-mark
  "Pop the last position off the mark stack and cursor to it."
  [])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Manipulate buffer text
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn buffer-insert-string
  "Insert a string into the given buffer."
  [buf col line txt]
  )

; These two fns below can use buffer-insert-string...

(defn buffer-append
  "Append text to the specified buffer."
  [buf txt]
  )

(defn prepend-append
  "Prepend text to the specified buffer."
  [buf txt]
  )

(defn get-line
  "Get a line of text from the current buffer."
  ([] (get-line (current-line)))
  ([line]
   ))

(defn set-line!
  "Set the current line of text."
  ([txt] (set-line! (current-line) txt))
  ([line txt]))

(defn indent-line!
  ([])
  ([line]))

(defn next-non-blank-line
  "Move cursor to the next non-blank line."
  [])

(defn prev-non-blank-line
  "Move cursor to the previous non-blank line."
  [])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Search and replace
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn search 
"Search for a word in the current buffer from the current caret position."
  ([s] (search s :default))
  ([s flag]
    (cond 
      (= flag :default)
        (do
        	(search/search (tab/current-text-area) s)
        	(.requestFocus (tab/current-text-area)))
      (or (= flag :all)(= flag :a)(= flag :start)(= flag :s))
        (do
        	(search/search (tab/current-text-area) s)
        	(.requestFocus (tab/current-text-area))))))

(defn search-replace
"Search for a word in the current buffer from the current caret position and replace the next occurence of it."
 [s r]
 (search/search-replace (tab/current-text-area) s r))

(defn search-replace-all
"Search for a word in the current buffer from the current caret position and replace all occurences of it."
 [s r]
 (search/search-replace-all (tab/current-text-area) s r))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; File and directory operations
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; Can probably just import some file util namespace from clojure.file that will provide the necessary functions (like checking if a path is a file or directory, getting and setting permissions, mkdir, move, rename, delete, etc...

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Repl functions (so scripts can manipulate the repl)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-repl-line
  "Returns the text on the current line of the repl."
  [])

(defn get-repl-col
  "Returns the current column number of the repl cursor."
  [])

(defn set-repl-line
  "Set the text on the current repl line."
  [txt])

;; Interactive
; I'm not sure the best way to do it, but it will probably be necessary for some commands and scripts
; to ask the user for input.  Maybe it should bring the application-repl to the foreground,
; and then modify the prompt to ask for user input?


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Popup window
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; Create popup at current cursor location (or given location)
; set, add, remove contents of popup
; move, delete popup

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Seesaw helpers
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn show-opts [c]
"Show seesaw widget optoins for the given component"
	(seesaw.dev/show-options c))

(defn show-evs [c]
"Show seesaw widget optoins for the given component"
	(seesaw.dev/show-events c))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Shorthand. mostly for dev
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn f [s] (search/search (tab/current-text-area) s))
(defn fr [s r] (search/search-replace (tab/current-text-area) s r))
(defn fra [s r] (search/search-replace-all (tab/current-text-area) s r))

(defn mark-all
"Mark all occurences of a given string in the active buffer."
([str-to-mark]
  (mark-all (tab/current-text-area) str-to-mark))
([buffer str-to-mark]
  (invoke-later
    (.markAll buffer str-to-mark false false false))))

(defn clear-marks
"Clear the current marks."
  ([]
    (clear-marks (tab/current-text-area)))
  ([buffer]
    (invoke-later
     (.clearMarkAllHighlights buffer))))

(defn mark
"Mark all occurences of a given string in the active buffer. Takes a string to mark or no args to clear."
  ([] (clear-marks))
  ([str-to-mark]
  (mark-all str-to-mark)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Buffer
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn current-buffer
"Returns the map of the current buffer if one is open."
  []
  (tab/current-buffer))

(defn current-repl
"Returns the map of the current project repl if one is open."
  []
  (tab/current-repl))

(defn current-text-area []
"Returns the current text area."
  (tab/current-text-area))

(defn current-form []
  (let [current-text-area (current-text-area)
        text (config current-text-area :text)
        current-point (cursor-point)
        form-location (find-enclosing-brackets text current-point)]
    (.substring text (first form-location) (second form-location))))

(defn vec-string [text-area [left right]]
  (.substring text-area left right))

(defn current-group-form []
  (let [brackets (find-line-group (current-text-area))]
    (.substring (config (current-text-area) :text) (first brackets) (second brackets))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Project
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-project
"Create a new Leiningen project. With no project opts map a creation form will open"
  ([]
    (project.form/create-new-project))
  ([project-map]
    (project.form/create-new-project project-map)))


(defn current-project
"Returns the current SketchPad project."
  []
  (tab/current-project))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; REPL
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Bookmarks
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn scroller
"Returns the buffers parent RScrollPanel."
[buffer]
  (get-in buffer [:component :scroller]))

(defn buffer-gutter
"Returns the buffers Gutter component."
[buffer]
  (.getGutter (scroller buffer)))

(defn gutter 
"Returns the gutter from a given buffer."
[buffer]
  (let [scroller (get-in buffer [:component :scroller])
        gutter (.getGutter scroller)]
    gutter))

(defn grep
"Grep the current projects or a given the given paths."
  ([search-term] (buffer.grep/grep-files search-term))
  ([search-term & args] (buffer.grep/grep-files search-term args)))


(defn app-repl
"Returns the map representing the SketchPad application REPL."
  []
  (:application-repl @app))

(defn app-repl-text-area
"Returns the text area for the SketchPad application REPL."
  []
  (get-in (app-repl) [:component :text-area]))

(defn read-or-nil [rdr]
  (try (read rdr) (catch RuntimeException e nil)))

(defn make-forms-seq
  "Construct a lazy sequence of clojure forms from input f. F can be anything that can be coerced to a reader"
  [f]
  (letfn [(forms-seq [rdr]
            (let [form (read-or-nil rdr)]
              (if (nil? form) []
                  (lazy-seq (cons form (forms-seq rdr))))))]
    (forms-seq (java.io.PushbackReader. (io/reader f)))))

(defn add-app-repl []
	(doseq [project (vals @(:projects @app))]
		(when (= (get-in project [:lein-project :name]) "sketchpad")
			(let [app-repl-uuid (get-in @app [:application-repl :uuid])
				app-repl (:application-repl @app)]
			  (swap! (:repls project) assoc app-repl-uuid app-repl)
			  (reset! (:last-focused-repl project) app-repl-uuid)))))

(do (add-app-repl))

(defn theme
  ([theme-name] (theme (current-text-area) theme-name))
  ([text-area theme-name]
  (invoke-later
    (text-area-theme text-area (str theme-name ".xml")))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;  System commands
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn ls
"System ls call."
	[& opts]
	 (:out (apply sh "ls" opts)))

(defn lein-new
"Use lein-new to create a new Leiningen project from a lein template."
	([template project-name] (lein-new template project-path (name project-name)))
	([template project-path project-name]
	(let [project-name-str (name project-name)
			new-project-abs-path (str project-path project-name)]
		(if-let [new-project-dir (clojure.java.io/file new-project-abs-path)]
			(when (.mkdir new-project-dir)
        (let[abs-path (.getAbsolutePath new-project-dir)]
          (println abs-path)
  				(lein-new/new "--to-dir" abs-path (name template) (name project-name))
  				(project/add-project abs-path)
  				(invoke-later
  				  (tree.utils/update-tree))))
      (println new-project-abs-path " could not be createad...")))))

(defn lein-new-template
  ([template-name] (lein-new-template (name template-name) (str template-name "-template")))
  ([template-name template-dir]
  (let [project-name-str (name template-name)
        template-path (str (name project-name-str) "-template")
        new-project-abs-path (str project-path template-path)]
        (println new-project-abs-path)
    (if-let [new-project-dir (clojure.java.io/file new-project-abs-path)]
      (do
        (println new-project-dir)
        (repl.info-utils/post-msg new-project-dir)
        (when (.mkdir new-project-dir)
          (let[abs-path (.getAbsolutePath new-project-dir)]
            (lein-new/new "template" (name project-name-str) "--to-dir" new-project-abs-path)
            (project/add-project abs-path)
            (invoke-later
              (tree.utils/update-tree)))))
      (println new-project-abs-path " could not be createad..."))
    )))