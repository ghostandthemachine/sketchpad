(ns sketchpad.buffer 
	(:use 		[sketchpad search-context])
  	(:import 	(org.fife.ui.rsyntaxtextarea RSyntaxTextAreaEditorKit)
           		(org.fife.ui.rtextarea RTextAreaEditorKit)
           		(java.awt.event ActionEvent))
  	(:require 	[clojure.string :as string]
  				[sketchpad.search-engine :as search]
  				[sketchpad.app :as app]
  				[sketchpad.state :as sketchpad.state]
          [seesaw.core :as seesaw]))

(defonce app sketchpad.state/app)

(defn buffer-cursor-point [rta]
  (.getCaretPosition rta))

(defn get-last-cmd [text-area]
  (let [text (seesaw/config text-area :text)]
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
  (.setCaretPosition rta (+ (buffer-cursor-point rta) offset)))

(defn buffer-goto-next-char [rta]
  (buffer-move-pos-by-char rta 1))

(defn buffer-goto-prev-char [rta]
  (buffer-move-pos-by-char rta (- 0 1)))

(defn trim-enclosing-char [s cl cr]
  (let [drop-first (string/replace s cl "")
        drop-last (string/replace drop-first cr "")]
    drop-last))

(defn trim-parens [s]
  (trim-enclosing-char s "(" ")"))

(defn trim-brackets [s]
  (trim-enclosing-char s "[" "]"))

(defn append-text [text-pane text]
  (when-let [doc (.getDocument text-pane)]
    (try
      (.insertString doc (.getLength doc) text nil)
      (catch java.lang.ClassCastException e ))))

(defn append-text-update [rsta s]
  (try
    (append-text rsta (str s))
    (.setCaretPosition rsta (.getLastVisibleOffset rsta))
    (catch java.lang.NullPointerException e)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Search
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn search
  [rta search-str]
  (cond
    (= java.lang.String (type search-str))
    (do
      (let [context (search-context search-str)]
        (let [finder (search/find rta context)]
          finder)))
    (= java.util.regex.Pattern (type search-str))
    (do
      (let [context (search-context (str search-str))]
        (regular-expression! context true)
        (let [finder (search/find rta context)]
          finder)))))

(defn search-replace
  [rta search-str replace-str]
  (cond
    (= java.lang.String (type search-str))
    (do
      (let [context (search-context search-str)]
        (replace-with! context replace-str)
        (let [finder (search/replace rta context)]
          finder)))
    (= java.util.regex.Pattern (type search-str))
    (let [context (search-context (str search-str))]
      (regular-expression! context true)
      (replace-with! context replace-str)
      (let [finder (search/replace rta context)]
        finder))))

(defn search-replace-all
  [rta search-str replace-str]
  (cond
    (= java.lang.String (type search-str))
    (do
      (let [context (search-context search-str)]
        (replace-with! context replace-str)
        (let [finder (search/replace-all rta context)]
          finder)))
    (= java.util.regex.Pattern (type search-str))
    (do
      (let [context (search-context (str search-str))]
        (regular-expression! context true)
        (replace-with! context replace-str)
        (let [finder (search/replace-all rta context)]
          finder)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Buffer edit
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn copy
"Copy the text currently selected in the visible editor buffer to the system clipboard."
[]
(.copy (app/buffer)))

(defn cut
"Cut the text currently selected in the visible editor buffer to the system clipboard."
[]
(.cut (app/buffer)))

(defn paste
"Paste what is stored in the current clipboard to the current carret location of the active buffer."
[]
(.paste (app/buffer)))

(defn undo
"Undo the last edit action in the current buffer."
[]
(.undoLastAction (app/buffer)))

(defn redo
"Redo the last edit action in the current buffer."
[]
(.redoLastAction (app/buffer)))


(defn trim-enclosing-char [s cl cr]
  (let [drop-first (string/replace s cl "")
        drop-last (string/replace drop-first cr "")]
    drop-last))

(defn trim-parens [s]
  (trim-enclosing-char s "(" ")"))

(defn trim-brackets [s]
  (trim-enclosing-char s "[" "]"))

(defn append-text [text-pane text]
  (when-let [doc (.getDocument text-pane)]
    (try
      (.insertString doc (.getLength doc) text nil)
      (catch java.lang.ClassCastException e ))))

(defn append-text-update [buffer s]
  (append-text buffer (str s))
  (.setCaretPosition buffer (.getLastVisibleOffset buffer)))

