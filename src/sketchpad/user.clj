(ns sketchpad.user
	(:refer-clojure :exclude [find replace])
	(:use [seesaw meta dev]
        [seesaw.core :exclude [height width]]
			  [clojure.repl]
			  [sketchpad.config.config]
        [sketchpad.tree.tree]
        [sketchpad.buffer.action]
        [sketchpad.util.brackets]
        [sketchpad.system.desktop]
				[sketchpad.auto-complete.template]
				[clojure.java.shell])
	(:require [sketchpad.util.tab :as tab]
					  [sketchpad.wrapper.rsyntaxtextarea :as rsta]
					  [sketchpad.core :as core]
            ; [sketchpad.buffer.action :as buffer]
            [sketchpad.wrapper.gutter :as gutter]
					  [sketchpad.buffer.search :as search]
            [sketchpad.project.project :as project]
            [sketchpad.project.form :as project.form]
					  [clojure.pprint :as pprint]
					  [clojure.stacktrace :as stack-trace]
					  [seesaw.dev :as seesaw.dev]
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
			
; (defn current-project []
; "return the current Leiningen project being edited in the editor component"
; 	(try
; 		(when-let [cur-project (get-meta (tab/current-text-area) :project)]
; 			cur-project)
; 		(catch java.lang.IllegalArgumentException e
; 			(println "no project open in editor buffers"))))

; (defn lein-project [path]
; "return a parsed Leiningen project.clj by path"
; 	(when-let [proj (project/read (str (current-project) "/project.clj"))]
; 		proj))

; (defn current-lein-project []
; "return the current project's parsed Leiningen project.clj"
; 	(try
; 		(when-let [lein-proj (lein-project (current-project))]
; 		lein-proj)
; 	(catch java.lang.IllegalArgumentException e
; 		(println "no project open in buffer"))))

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
          (search/search (tab/current-text-area) s)
        (or (= flag :all)(= flag :a)(= flag :start)(= flag :s))
          (search/search (tab/current-text-area) s)
        ))
    )

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

(defn current-text-area []
"Returns the current text area."
  (get-in (current-buffer) [:component :text-area]))

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
  (let [cur-buffer (current-buffer)
      current-project (project/project-from-path (:project cur-buffer))]
    current-project))

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

(defn token-list-for-line
"Returns the token list for a given line."
	[]
	(let [text-area (current-text-area)
				doc (.getDocument text-area)
				line (.getCaretLine text-area)
				token (.getTokenListForLine doc line)]
		token))

(defn current-token 
"Returns the Token object for the current token."	
	[]
	(let [text-area (current-text-area)
				doc (.getDocument text-area)
				line (.getCaretLine text-area)
				token (.getTokenListForLine doc line)
				dot (.getCaretPosition text-area)
				cur-token (org.fife.ui.rsyntaxtextarea.RSyntaxUtilities/getTokenAtOffset token dot)]
		cur-token))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;  Grep
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn grep
"Grep a path."
	([search-term] (grep search-term "projects/"))
	([search-term path]
		(doall
			(apply println (clojure.string/split (:out (sh "grep" "-nri" search-term path)) #"\n")))))
