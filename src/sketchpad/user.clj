(ns sketchpad.user
	(:refer-clojure :exclude [find replace])
	(:use [sketchpad tab]
				[seesaw meta dev core]
			  [clojure.repl]
			  [sketchpad.config]
        [sketchpad.tree.tree]
			  [sketchpad.buffer.action])
	(:require [sketchpad.tab :as tab]
					  [sketchpad.rsyntaxtextarea :as rsta]
					  [sketchpad.core :as core]
					  [sketchpad.buffer.search :as search]
            [sketchpad.project.project :as project]
					  [clojure.pprint :as pprint]
					  [clojure.stacktrace :as stack-trace]
					  [seesaw.dev :as seesaw.dev]
					  [sketchpad.repl-communication :as repl-communication])
	(:import 	(org.fife.ui.rsyntaxtextarea RSyntaxTextAreaEditorKit)
			 		(org.fife.ui.rtextarea RTextAreaEditorKit)
			 		(org.fife.ui.rsyntaxtextarea.RSyntaxTextArea)
			 		(java.awt.event ActionEvent)))

(def app sketchpad.state/app)

(def projects (:projects @app))

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
		(.getText (current-text-area))
		(catch java.lang.IllegalArgumentException e
			(println "no buffer open editor"))))
			
; (defn current-project []
; "return the current Leiningen project being edited in the editor component"
; 	(try
; 		(when-let [cur-project (get-meta (current-text-area) :project)]
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
  (cursor-point (current-text-area)))
([buffer]
  (buffer-cursor-point buffer)))

(defn cursor-pos
  "Returns the position of the cursor [<column> <line>]."
([]
  (buffer-cursor-pos (current-text-area)))
([buffer]
  (buffer-cursor-pos buffer)))

(defn set-cursor-pos!
  "Set the position of the cursor."
  [col line])

(defn current-col
  "Return the column number of the cursor in the current buffer."
([]
  (current-col (current-text-area)))
([buffer]
  (first (cursor-pos buffer))))

(defn current-line
  "Return the line number of the cursor in the current buffer."
([]
  (current-line (current-text-area)))
([buffer]
  (second (cursor-pos buffer))))
  
;(defn goto-pattern
;  [regex-pattern]
;  (search (str regex-pattern)))

(defn goto-next-char
  []
  (buffer-goto-next-char (current-text-area)))

(defn goto-prev-char
  []
  (buffer-goto-prev-char (current-text-area)))

(defn goto-nth-char
  [n]
  (buffer-goto-prev-char (current-text-area) n))

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
  ([line txt]
   ))

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
  "Find a regexp pattern in the current buffer."
  [regexp]
  )

(defn search-and-replace
  [])

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
; to ask the user for input.  Maybe it should bring the editor-repl to the foreground,
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
"show seesaw widget optoins for the given component"
	(seesaw.dev/show-options c))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Shorthand. mostly for dev
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn f [s] (search/search (current-text-area) s))
(defn fr [s r] (search/search-replace (current-text-area) s r))
(defn fra [s r] (search/search-replace-all (current-text-area) s r))

(defn mark-occurrences
([b]
  (mark-occurrences (current-text-area) b))
([buffer b]
  (.clearMarkAllHighlights buffer)
  (.setMarkOccurrences buffer b)))

(defn mark-all
([str-to-mark]
  (mark-all (current-text-area) str-to-mark))
([buffer str-to-mark]
  (.markAll buffer str-to-mark false false false)))

(defn clear-marks
([]
  (clear-marks (current-text-area)))
([buffer]
  (.clearMarkAllHighlights buffer)
  (.repaint buffer)))

(defn buffer-scroller
"Returns the buffers parent RScrollPanel."
[buffer]
  (get-meta buffer :scroller))

(defn buffer-gutter
"Returns the buffers Gutter component."
[buffer]
  (.getGutter (buffer-scroller buffer)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Bookmarks
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn gutter 
"Returns the gutter from a given buffer."
[buffer]
  (let [scroller (get-meta buffer :scroller)
        gutter (.getGutter scroller)]
    gutter))

(defn show-bookmarks! 
"Show the bookmark panel."
([] (show-bookmarks! (current-text-area)))
([buffer]
  (.setBookmarkingEnabled (gutter buffer) true)))

(defn hide-bookmarks!
"Hide the bookmark panel."
([] (hide-bookmarks! (current-text-area)))
([buffer]
  (.setBookmarkingEnabled (gutter buffer) false)))

(defn bookmark-line
"Bookmark a then line of the given buffer."
[buffer line]
  (.toggleBookmark (gutter buffer) line))

(defmulti foo class)

(defmethod foo org.fife.ui.rsyntaxtextarea.RSyntaxTextArea [buffer] 
  (bookmark-line buffer (current-line)))

(defmethod foo java.lang.Long [line]
  (bookmark-line (current-text-area) line))

(defmethod foo clojure.lang.PersistentVector [[buffer line]] 
  (bookmark-line buffer line))