(ns sketchpad.buffer.action
	(:use 		[sketchpad.wrapper.search-context]
            [seesaw.core :only [invoke-later]])
  	(:import 	(java.util UUID)
              (org.fife.ui.rsyntaxtextarea RSyntaxTextAreaEditorKit)
           		(org.fife.ui.rtextarea RTextAreaEditorKit)
           		(java.awt.event ActionEvent))
  	(:require 	[clojure.string :as string]
  				[sketchpad.config.app :as app]
          [sketchpad.util.tab :as tab]
          [sketchpad.state.state :as state]
          [sketchpad.util.utils :as utils]
          [seesaw.core :as seesaw]))

(defn repl-panel []
  (@state/app :repl-tabbed-editor))

(defn action-event [c]
  (ActionEvent. c 0 (.. UUID randomUUID toString)))

(defn perform-action [action e rta]
  (invoke-later
    (.actionPerformedImpl action e rta)
    (when-not (nil? (repl-panel))
      (.grabFocus (repl-panel)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; RTextArea Actions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn undo
"undo last recordable action"
([] (undo (tab/current-text-area)))
([rta]
  (perform-action 
    (org.fife.ui.rtextarea.RTextAreaEditorKit$UndoAction.)
    (action-event rta) 
    rta)))
    
(defn redo
"redo last recordable action"
([] (redo (tab/current-text-area)))
([rta]
  (perform-action 
    (org.fife.ui.rtextarea.RTextAreaEditorKit$RedoAction.)
    (action-event rta) 
    rta)))

(defn u 
"undo last recordable action"
([]
  (undo))
([rta]
  (undo rta)))

(defn r
"redo last recordable action"
([]
  (redo))
([rta]
  (redo rta)))

(defn beep 
"trigger system beep tone"
([] (beep (tab/current-text-area)))
([rta]
  (perform-action 
    (org.fife.ui.rtextarea.RTextAreaEditorKit$BeepAction.)
    (action-event rta) 
    rta)))

(defn goto-next-word
"move caret to next word"
([] (goto-next-word (tab/current-text-area)))
([rta]
  (perform-action 
    (org.fife.ui.rtextarea.RTextAreaEditorKit$NextWordAction. "goto-next-word" false)
    (action-event rta) 
    rta)))

(defn select-next-word 
"select to next word"
([] (select-next-word (tab/current-text-area)))
([rta]
  (perform-action 
    (org.fife.ui.rtextarea.RTextAreaEditorKit$NextWordAction. "select-next-word" true)
    (action-event rta) 
    rta)))

(defn toggle-comment
"Toggle comment for the current buffer line."
([] (toggle-comment (tab/current-text-area)))
([rta]
  (perform-action 
    (org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit$ToggleCommentAction. )
    (action-event rta) 
    rta)))

(defn goto-beginning 
"move caret to begining of the current buffer"
([] (goto-beginning (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$BeginAction. "goto-beginning" true)
  (action-event rta) 
  rta)))

(defn goto-line-beginning
"select to begining of the current line"
([] (goto-line-beginning (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$BeginLineAction. "goto-line-beginning" false)
  (action-event rta) 
  rta)))

(defn goto-line-beginning
"move caret to begining of the current line"
([] (goto-line-beginning (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$BeginLineAction. "goto-line-beginning" false)
  (action-event rta) 
  rta)))

(defn goto-line-end
"move caret to ending of the current line"
([] (goto-line-end (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$EndAction. "goto-line-end" false)
  (action-event rta) 
  rta)))

(defn select-to-line-end
"select from current caret position to the end of the current line"
([] (select-to-line-end (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$EndAction. "select-to-line-beginning" true)
  (action-event rta) 
  rta)))

(defn select-to-line-beginning
"select from current caret position to the end of the current line"
([] (select-to-line-beginning (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$BeginLineAction. "select-to-line-beginning" true)
  (action-event rta) 
  rta)))

(defn delete-line
"delete the current line"
([] (delete-line (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$DeleteLineAction.)
  (action-event rta) 
  rta)))

(defn delete-next-char
"delete the next char after the caret"
([] (delete-next-char (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$DeleteNextCharAction.)
  (action-event rta) 
  rta)))

(defn delete-prev-char
"delete the previous char before the caret"
([] (delete-prev-char (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$DeletePrevCharAction.)
  (action-event rta) 
  rta)))

(defn delete-prev-word-char
"delete the previous word before the caret"
([] (delete-prev-word-char (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$DeletePrevWordAction.)
  (action-event rta) 
  rta)))

(defn delete-rest-of-line
"delete the rest of the line after the caret"
([] (delete-rest-of-line (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$DeleteRestOfLineAction.)
  (action-event rta) 
  rta)))

(defn start-macro!
"Start recording a macro. Any recordable text actions called between start-macro! and end-macro! will be included."
([] (start-macro! (tab/current-text-area)))
([rta] 
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$BeginRecordingMacroAction.)
  (action-event rta) 
  rta))
([rta name-str desc]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$BeginRecordingMacroAction.)
  (action-event rta) 
  rta))
([rta name-str desc accelerator]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$BeginRecordingMacroAction. name-str nil desc nil accelerator)
  (action-event rta) 
  rta)))

(defn end-macro!
"End recording a macro. Any recordable text actions called between start-macro! and end-macro! will be included."
([] (end-macro! (tab/current-text-area)))
([rta] 
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$EndRecordingMacroAction.)
  (action-event rta) 
  rta))
([rta name-str desc]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$EndRecordingMacroAction.)
  (action-event rta) 
  rta))
([rta name-str desc  accelerator]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$EndRecordingMacroAction. name-str nil desc nil accelerator)
  (action-event rta) 
  rta)))

(defn play-macro
"Playback last macro"
([] (play-macro (tab/current-text-area)))
([rta] 
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$PlaybackLastMacroAction.)
  (action-event rta) 
  rta))
([rta name-str desc]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$PlaybackLastMacroAction.)
  (action-event rta) 
  rta))
([rta name-str desc  accelerator]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$PlaybackLastMacroAction. name-str nil desc nil accelerator)
  (action-event rta) 
  rta)))

(defn copy
"copy the current selection"
([] (copy (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$CopyAction.)
  (action-event rta) 
  rta)))

(defn cut
"cut the current selection"
([] (cut (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$CutAction.)
  (action-event rta) 
  rta)))

(defn paste
"Paste the current selection"
([] (paste (tab/current-text-area)))
([rta]
(perform-action 
  (org.fife.ui.rtextarea.RTextAreaEditorKit$PasteAction.)
  (action-event rta) 
  rta)))

(defn increase-font-size
([] (cut (tab/current-text-area)))
([rta]
(perform-action
  (org.fife.ui.rtextarea.RTextAreaEditorKit$IncreaseFontSizeAction.)
  (action-event rta) 
  rta)))

(defn decrease-font-size
([] (cut (tab/current-text-area)))
([rta]
(perform-action
  (org.fife.ui.rtextarea.RTextAreaEditorKit$DecreaseFontSizeAction.)
  (action-event rta) 
  rta)))

(defn previous-occurence
([] (previous-occurence (tab/current-text-area)))
([rta]
(perform-action
  (org.fife.ui.rtextarea.RTextAreaEditorKit$PreviousOccurrenceAction. "previous-occurence")
  (action-event rta) 
  rta)))
  
(defn next-occurence
([] (next-occurence (tab/current-text-area)))
([rta]
(perform-action
  (org.fife.ui.rtextarea.RTextAreaEditorKit$NextOccurrenceAction. "next-occurence")
  (action-event rta) 
  rta)))

(defn macro 
([f] (macro f (tab/current-text-area)))
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


(defn buffer-cursor-point [rta]
  (.getCaretPosition rta))

(defn get-last-cmd [text-area]
  (let [text (seesaw/config text-area :text)]
    ; (println (string/trim (last (string/split text #"=>"))))
     (string/trim (last (string/split text #"=>")))))

(defn buffer-cursor-pos 
([] (buffer-cursor-pos ))
([rta]
(let [rta-doc (.getDocument rta)
    root (.getDefaultRootElement rta-doc)
    caret (.getCaret rta)
    dot (.getCaretPosition rta)
    line (.getElementIndex root dot)
    elem (.getElement root line)
    start (.getStartOffset elem)
    col (- dot start)]
[col line])))

(defn buffer-cursor-pos! [rsta]
  (let [rsta-doc (.getDocument rsta)
        root (.getDefaultRootElement rsta-doc)
        caret (.getCaret rsta)
        dot (.getCaretPosition rsta)
        line (.getElementIndex root dot)
        elem (.getElement root line)
        start (.getStartOffset elem)
        col (- dot start)]
    (println (str "dot: \t" (.toString dot)))
    (println (str "start: \t" start))
    (println (str "col: \t" col))
    (println (str "line: \t" line))))

(defn buffer-move-pos-by-char [rta offset]
  (invoke-later
    (.setCaretPosition rta (+ (buffer-cursor-point rta) offset))))

(defn buffer-goto-next-char [rta]
  (buffer-move-pos-by-char rta 1))

(defn buffer-goto-prev-char [rta]
  (buffer-move-pos-by-char rta (- 0 1)))

(defn trim-enclosing-char [s cl cr]
  (let [drop-first (string/replace s cl "")
        drop-last (string/replace drop-first cr "")]
    drop-last))

(defn trim-enclosing [s]
  (.substring s 1 (- (count s) 1)))

(defn trim-parens [s]
  (trim-enclosing-char s "(" ")"))

(defn trim-brackets [s]
  (trim-enclosing-char s "[" "]"))

(defn append-text [text-pane text]
  (invoke-later
    (when-let [doc (.getDocument text-pane)]
      (try
        (.append text-pane text)
        (.discardAllEdits text-pane)
        (.setCaretPosition text-pane (.getLastVisibleOffset text-pane))
      (catch Throwable e
        (println "append-text ERROR")
        (append-text text-pane "=> "))))))

(defn append-text-update [rsta s]
  (try
      (append-text rsta (str s))
    (catch java.lang.NullPointerException e)))

(defn trim-enclosing-char [s cl cr]
  (let [drop-first (string/replace s cl "")
        drop-last (string/replace drop-first cr "")]
    drop-last))

(defn trim-parens [s]
  (trim-enclosing-char s "(" ")"))

(defn trim-brackets [s]
  (trim-enclosing-char s "[" "]"))