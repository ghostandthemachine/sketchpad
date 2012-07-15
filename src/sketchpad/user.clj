(ns sketchpad.user
	(:use [sketchpad buffer-info]
				[seesaw meta])
	(:require [sketchpad.tab-manager :as tab]
			  [sketchpad.rsyntaxtextarea :as rsta]
			  [sketchpad.core :as core]
			  [sketchpad.buffer-search :as buffer-search]
			  [leiningen.core.project :as project])
	(:import (org.fife.ui.rsyntaxtextarea RSyntaxTextAreaEditorKit)
			 		(org.fife.ui.rtextarea RTextAreaEditorKit)
			 		(java.awt.event ActionEvent)))

(defn action-event [c]
	(ActionEvent. c 0 "buffer-info-action-event"))

(defn perform-action [action e rta]
	(.actionPerformedImpl action e rta))

(def app @sketchpad.core/current-app)

(defn preflect [obj]
	(clojure.pprint/pprint (clojure.reflect/reflect obj)))

(defn current-repl-rta [] (tab/current-text-area (:repl-tabbed-panel app)))
(defn current-buffer [] (tab/current-text-area (:editor-tabbed-panel app)))

(defn current-text []
	(.getText (current-buffer)))

(defn current-project [] (get-meta (current-buffer) :project))

(defn lein-project [path] (project/read (str (current-project) "/project.clj")))

(defn current-lein-project [] (lein-project (current-project)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; RTextArea Actions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn current-text-area [] (current-buffer))
(defn undo
([] (undo (current-text-area)))
([rta]
	(perform-action 
		(org.fife.ui.rtextarea.RTextAreaEditorKit$UndoAction. )
		(action-event rta) 
		rta)))
		
(defn redo
([] (redo (current-text-area)))
([rta]
	(perform-action 
		(org.fife.ui.rtextarea.RTextAreaEditorKit$RedoAction. )
		(action-event rta) 
		rta)))

(defn u 
([]
	(undo))
([rta]
	(undo rta)))

(defn r
([]
	(redo))
([rta]
	(redo rta)))

(defn beep 
([] (beep (current-text-area)))
([rta]
	(perform-action 
		(org.fife.ui.rtextarea.RTextAreaEditorKit$BeepAction.)
		(action-event rta) 
		rta)))

(defn goto-next-word 
([] (goto-next-word (current-text-area)))
([rta]
	(perform-action 
		(org.fife.ui.rtextarea.RTextAreaEditorKit$NextWordAction. "goto-next-word" false)
		(action-event rta) 
		rta)))

(defn select-next-word 
([] (select-next-word (current-text-area)))
([rta]
	(perform-action 
		(org.fife.ui.rtextarea.RTextAreaEditorKit$NextWordAction. "select-next-word" true)
		(action-event rta) 
		rta)))

(defn goto-beginning 
([] (goto-beginning (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$BeginAction. "goto-beginning" true)
	(action-event rta) 
	rta)))

(defn goto-line-beginning
([] (goto-line-beginning (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$BeginLineAction. "goto-line-beginning" false)
	(action-event rta) 
	rta)))

(defn goto-line-beginning
([] (goto-line-beginning (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$BeginLineAction. "goto-line-beginning" false)
	(action-event rta) 
	rta)))

(defn goto-line-end
([] (goto-line-end (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$EndAction. "goto-line-end" false)
	(action-event rta) 
	rta)))

(defn select-to-line-end
([] (select-to-line-end (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$EndAction. "select-to-line-beginning" true)
	(action-event rta) 
	rta)))

(defn select-to-line-beginning
([] (select-to-line-beginning (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$BeginLineAction. "select-to-line-beginning" true)
	(action-event rta) 
	rta)))

(defn delete-line
([] (delete-line (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$DeleteLineAction. )
	(action-event rta) 
	rta)))

(defn delete-next-char
([] (delete-next-char (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$DeleteNextCharAction. )
	(action-event rta) 
	rta)))

(defn delete-prev-char
([] (delete-prev-char (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$DeletePrevCharAction. )
	(action-event rta) 
	rta)))

(defn delete-prev-word-char
([] (delete-prev-word-char (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$DeletePrevWordAction. )
	(action-event rta) 
	rta)))

(defn delete-rest-of-line
([] (delete-rest-of-line (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$DeleteRestOfLineAction. )
	(action-event rta) 
	rta)))





;(defn goto-word-beginning
;([] (goto-word-beginning (current-text-area)))
;([rta]
;(perform-action 
;	(org.fife.ui.rtextarea.RTextAreaEditorKit$BeginWordAction. "goto-word-beginning" false)
;	(action-event rta) 
;	rta)))
;
;(defn select-to-word-beginning
;([] (select-to-word-beginning (current-text-area)))
;([rta]
;(perform-action 
;	(org.fife.ui.rtextarea.RTextAreaEditorKit$BeginWordAction. "select-to-word-beginning" true)
;	(action-event rta) 
;	rta)))

(defn start-macro!
([] (start-macro! (current-text-area)))
([rta] 
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$BeginRecordingMacroAction. )
	(action-event rta) 
	rta))
([rta name-str desc]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$BeginRecordingMacroAction. )
	(action-event rta) 
	rta))
([rta name-str desc accelerator]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$BeginRecordingMacroAction. name-str nil desc nil accelerator)
	(action-event rta) 
	rta)))

(defn end-macro!
([] (end-macro! (current-text-area)))
([rta] 
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$EndRecordingMacroAction. )
	(action-event rta) 
	rta))
([rta name-str desc]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$EndRecordingMacroAction. )
	(action-event rta) 
	rta))
([rta name-str desc  accelerator]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$EndRecordingMacroAction. name-str nil desc nil accelerator)
	(action-event rta) 
	rta)))

(defn play-macro
([] (play-macro (current-text-area)))
([rta] 
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$PlaybackLastMacroAction. )
	(action-event rta) 
	rta))
([rta name-str desc]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$PlaybackLastMacroAction. )
	(action-event rta) 
	rta))
([rta name-str desc  accelerator]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$PlaybackLastMacroAction. name-str nil desc nil accelerator)
	(action-event rta) 
	rta)))


(defn copy
([] (copy (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$CopyAction. )
	(action-event rta) 
	rta)))

(defn cut
([] (cut (current-text-area)))
([rta]
(perform-action 
	(org.fife.ui.rtextarea.RTextAreaEditorKit$CutAction. )
	(action-event rta) 
	rta)))

(defn increase-font-size
([] (cut (current-text-area)))
([rta]
(perform-action
	(org.fife.ui.rtextarea.RTextAreaEditorKit$IncreaseFontSizeAction. )
	(action-event rta) 
	rta)))

(defn decrease-font-size
([] (cut (current-text-area)))
([rta]
(perform-action
	(org.fife.ui.rtextarea.RTextAreaEditorKit$DecreaseFontSizeAction. )
	(action-event rta) 
	rta)))

(defn previous-occurence
([] (previous-occurence (current-text-area)))
([rta]
(perform-action
	(org.fife.ui.rtextarea.RTextAreaEditorKit$PreviousOccurrenceAction. "previous-occurence")
	(action-event rta) 
	rta)))
	
(defn next-occurence
([] (next-occurence (current-text-area)))
([rta]
(perform-action
	(org.fife.ui.rtextarea.RTextAreaEditorKit$NextOccurrenceAction. "next-occurence")
	(action-event rta) 
	rta)))
		
(defn prev-occurence
([] (prev-occurence (current-text-area)))
([rta]
(perform-action
	(org.fife.ui.rtextarea.RTextAreaEditorKit$PreviousOccurrenceAction. "next-occurence")
	(action-event rta) 
	rta)))

(defn macro 
([f] (macro f (current-text-area)))
([f rta]
	(start-macro! rta)
	(f rta)
	(end-macro! rta))
([f rta file-name] (macro f rta file-name nil))
([f rta file-name ks]
	(start-macro! rta nil nil ks)
	(f rta)
	(end-macro! rta)
	(let[m (.getCurrentMacro rta)]
		(.saveToFile m file-name)
		;; log created new macro file
	)))
	
 


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Cursor and mark position
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cursor-point
  "Return the current position of the cursor as a character count."
  ([]
  (cursor-point (current-text-area)))
  ([rta]
  (buffer-cursor-point rta)))

(defn cursor-pos
  "Returns the position of the cursor [<column> <line>]."
  ([]
  (buffer-cursor-pos (current-text-area)))
  ([rta]
  (buffer-cursor-pos rta)))

(defn set-cursor-pos!
  "Set the position of the cursor."
  [col line])

(defn current-col
  "Return the column number of the cursor in the current buffer."
  []
  (first (cursor-pos)))

(defn current-line
  "Return the line number of the cursor in the current buffer."
  []
  (second (cursor-pos)))

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
;; Shorthand. mostly for dev
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn f [s] (sketchpad.buffer-search/search (current-text-area) s))
(defn fr [s r] (sketchpad.buffer-search/search-replace (current-text-area) s r))
(defn fra [s r] (sketchpad.buffer-search/search-replace-all (current-text-area) s r))



