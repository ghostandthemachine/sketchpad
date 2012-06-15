(ns sketchpad.rtextarea
  (:import (org.fife.ui.rtextarea RTextArea RTextScrollPane)))

(defn action-properties!
"wrapper type:  :setter
        args:  [int java.lang.String char javax.swing.KeyStroke]
       flags:  :static :public
  interop fn:  .setActionProperties
 return-type:  void"
  [obj x y z w]
  (org.fife.ui.rtextarea.RTextArea/setActionProperties x y z w))

(defn end-recording-macro!
"wrapper type:  :should-have-bang
        args:  []
       flags:  :synchronized :static :public
  interop fn:  .endRecordingMacro
 return-type:  void"
  [obj]
  (org.fife.ui.rtextarea.RTextArea/endRecordingMacro))

(defn load-macro
"wrapper type:  :unknown
        args:  [org.fife.ui.rtextarea.Macro]
       flags:  :synchronized :static :public
  interop fn:  .loadMacro
 return-type:  void"
  [obj x]
  (org.fife.ui.rtextarea.RTextArea/loadMacro x))

(defn remove-all-line-highlights
"wrapper type:  :unknown
        args:  []
       flags:  :public
  interop fn:  .removeAllLineHighlights
 return-type:  void"
  [obj]
  (.removeAllLineHighlights obj))

(defn document!
"wrapper type:  :setter
        args:  [javax.swing.text.Document]
       flags:  :public
  interop fn:  .setDocument
 return-type:  void"
  [obj x]
  (.setDocument obj x))

(defn mark-all-color-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .MARK_ALL_COLOR_PROPERTY
 return-type:  "
  [obj]
  (org.fife.ui.rtextarea.RTextArea/MARK_ALL_COLOR_PROPERTY))

(defn tool-tip-text
"wrapper type:  :getter
        args:  [java.awt.event.MouseEvent]
       flags:  :public
  interop fn:  .getToolTipText
 return-type:  java.lang.String"
  [obj x]
  (.getToolTipText obj x))

(defn delete-action
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .DELETE_ACTION
 return-type:  "
  [obj]
  (org.fife.ui.rtextarea.RTextArea/DELETE_ACTION))

(defn replace-range
"wrapper type:  :unknown
        args:  [java.lang.String int int]
       flags:  :public
  interop fn:  .replaceRange
 return-type:  void"
  [obj x y z]
  (.replaceRange obj x y z))

(defn playback-last-macro!
"wrapper type:  :unknown
        args:  []
       flags:  :synchronized :public
  interop fn:  .playbackLastMacro
 return-type:  void"
  [obj]
  (.playbackLastMacro obj))

(defn overwrite-mode
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .OVERWRITE_MODE
 return-type:  "
  [obj]
  (org.fife.ui.rtextarea.RTextArea/OVERWRITE_MODE))

(defn mark-all
"wrapper type:  :unknown
        args:  [java.lang.String boolean boolean boolean]
       flags:  :public
  interop fn:  .markAll
 return-type:  int"
  [obj x y z w]
  (.markAll obj x y z w))

(defn default-mark-all-highlight-color
"wrapper type:  :getter
        args:  []
       flags:  :static :public :final
  interop fn:  .getDefaultMarkAllHighlightColor
 return-type:  java.awt.Color"
  [obj]
  (org.fife.ui.rtextarea.RTextArea/getDefaultMarkAllHighlightColor))

; (defn read
; "wrapper type:  :unknown
;         args:  [java.io.Reader java.lang.Object]
;        flags:  :public
;   interop fn:  .read
;  return-type:  void"
;   [obj x y]
;   (.read obj x y))

(defn current-macro
"wrapper type:  :getter
        args:  []
       flags:  :synchronized :static :public
  interop fn:  .getCurrentMacro
 return-type:  org.fife.ui.rtextarea.Macro"
  [obj]
  (org.fife.ui.rtextarea.RTextArea/getCurrentMacro))

(defn selected-occurrence-text
"wrapper type:  :getter
        args:  []
       flags:  :static :public
  interop fn:  .getSelectedOccurrenceText
 return-type:  java.lang.String"
  [obj]
  (org.fife.ui.rtextarea.RTextArea/getSelectedOccurrenceText))

(defn rtextarea
"wrapper type:  :constructor
        args:  [java.lang.String]
       flags:  :public
  interop fn:  Rtextarea.
 return-type:  "
  [obj x]
  (RTextArea. obj x))

(defn rtextarea
"wrapper type:  :constructor
        args:  [java.lang.String int int]
       flags:  :public
  interop fn:  Rtextarea.
 return-type:  "
  [obj x y z]
  (RTextArea. obj x y z))

(defn replace-selection
"wrapper type:  :unknown
        args:  [java.lang.String]
       flags:  :public
  interop fn:  .replaceSelection
 return-type:  void"
  [obj x]
  (.replaceSelection obj x))

(defn text-mode
"wrapper type:  :getter
        args:  []
       flags:  :public :final
  interop fn:  .getTextMode
 return-type:  int"
  [obj]
  (.getTextMode obj))

(defn mark-all-highlight-color!
"wrapper type:  :setter
        args:  [java.awt.Color]
       flags:  :public
  interop fn:  .setMarkAllHighlightColor
 return-type:  void"
  [obj x]
  (.setMarkAllHighlightColor obj x))

(defn add-line-highlight!
"wrapper type:  :should-have-bang
        args:  [int java.awt.Color]
       flags:  :public
  interop fn:  .addLineHighlight
 return-type:  java.lang.Object"
  [obj x y]
  (.addLineHighlight obj x y))

(defn rtextarea
"wrapper type:  :constructor
        args:  [javax.swing.text.AbstractDocument]
       flags:  :public
  interop fn:  Rtextarea.
 return-type:  "
  [obj x]
  (RTextArea. obj x))

(defn end-atomic-edit!
"wrapper type:  :should-have-bang
        args:  []
       flags:  :public
  interop fn:  .endAtomicEdit
 return-type:  void"
  [obj]
  (.endAtomicEdit obj))

(defn selected-occurrence-text!
"wrapper type:  :setter
        args:  [java.lang.String]
       flags:  :static :public
  interop fn:  .setSelectedOccurrenceText
 return-type:  void"
  [obj x]
  (org.fife.ui.rtextarea.RTextArea/setSelectedOccurrenceText x))

(defn discard-all-edits
"wrapper type:  :unknown
        args:  []
       flags:  :public
  interop fn:  .discardAllEdits
 return-type:  void"
  [obj]
  (.discardAllEdits obj))

(defn undo-action
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .UNDO_ACTION
 return-type:  "
  [obj]
  (org.fife.ui.rtextarea.RTextArea/UNDO_ACTION))

(defn action
"wrapper type:  :getter
        args:  [int]
       flags:  :static :public
  interop fn:  .getAction
 return-type:  org.fife.ui.rtextarea.RecordableTextAction"
  [obj x]
  (org.fife.ui.rtextarea.RTextArea/getAction x))

(defn begin-atomic-edit!
"wrapper type:  :should-have-bang
        args:  []
       flags:  :public
  interop fn:  .beginAtomicEdit
 return-type:  void"
  [obj]
  (.beginAtomicEdit obj))

(defn text-mode!
"wrapper type:  :setter
        args:  [int]
       flags:  :public
  interop fn:  .setTextMode
 return-type:  void"
  [obj x]
  (.setTextMode obj x))

(defn paste
"wrapper type:  :unknown
        args:  []
       flags:  :public
  interop fn:  .paste
 return-type:  void"
  [obj]
  (.paste obj))

(defn cut-action
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .CUT_ACTION
 return-type:  "
  [obj]
  (org.fife.ui.rtextarea.RTextArea/CUT_ACTION))

(defn caret!
"wrapper type:  :setter
        args:  [javax.swing.text.Caret]
       flags:  :public
  interop fn:  .setCaret
 return-type:  void"
  [obj x]
  (.setCaret obj x))

(defn tool-tip-supplier
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getToolTipSupplier
 return-type:  org.fife.ui.rtextarea.ToolTipSupplier"
  [obj]
  (.getToolTipSupplier obj))

(defn ui!
"wrapper type:  :setter
        args:  [javax.swing.plaf.TextUI]
       flags:  :public :final
  interop fn:  .setUI
 return-type:  void"
  [obj x]
  (.setUI obj x))

(defn select-all-action
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .SELECT_ALL_ACTION
 return-type:  "
  [obj]
  (org.fife.ui.rtextarea.RTextArea/SELECT_ALL_ACTION))

(defn recording-macro?
"wrapper type:  :boolean
        args:  []
       flags:  :synchronized :static :public
  interop fn:  .isRecordingMacro
 return-type:  boolean"
  [obj]
  (org.fife.ui.rtextarea.RTextArea/isRecordingMacro))

(defn tool-tip-supplier!
"wrapper type:  :setter
        args:  [org.fife.ui.rtextarea.ToolTipSupplier]
       flags:  :public
  interop fn:  .setToolTipSupplier
 return-type:  void"
  [obj x]
  (.setToolTipSupplier obj x))

(defn paste-action
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .PASTE_ACTION
 return-type:  "
  [obj]
  (org.fife.ui.rtextarea.RTextArea/PASTE_ACTION))

(defn insert-mode
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .INSERT_MODE
 return-type:  "
  [obj]
  (org.fife.ui.rtextarea.RTextArea/INSERT_MODE))

(defn undo-last-action
"wrapper type:  :unknown
        args:  []
       flags:  :public
  interop fn:  .undoLastAction
 return-type:  void"
  [obj]
  (.undoLastAction obj))

(defn caret-style!
"wrapper type:  :setter
        args:  [int int]
       flags:  :public
  interop fn:  .setCaretStyle
 return-type:  void"
  [obj x y]
  (.setCaretStyle obj x y))

(defn clear-mark-all-highlights
"wrapper type:  :unknown
        args:  []
       flags:  :public
  interop fn:  .clearMarkAllHighlights
 return-type:  void"
  [obj]
  (.clearMarkAllHighlights obj))

(defn rtextarea
"wrapper type:  :constructor
        args:  [int]
       flags:  :public
  interop fn:  Rtextarea.
 return-type:  "
  [obj x]
  (RTextArea. obj x))

(defn icon-group!
"wrapper type:  :setter
        args:  [org.fife.ui.rtextarea.IconGroup]
       flags:  :synchronized :static :public
  interop fn:  .setIconGroup
 return-type:  void"
  [obj x]
  (org.fife.ui.rtextarea.RTextArea/setIconGroup x))

(defn mark-all-highlight-color
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getMarkAllHighlightColor
 return-type:  java.awt.Color"
  [obj]
  (.getMarkAllHighlightColor obj))

(defn icon-group
"wrapper type:  :getter
        args:  []
       flags:  :static :public
  interop fn:  .getIconGroup
 return-type:  org.fife.ui.rtextarea.IconGroup"
  [obj]
  (org.fife.ui.rtextarea.RTextArea/getIconGroup))

(defn remove-line-highlight
"wrapper type:  :unknown
        args:  [java.lang.Object]
       flags:  :public
  interop fn:  .removeLineHighlight
 return-type:  void"
  [obj x]
  (.removeLineHighlight obj x))

(defn action-properties!
"wrapper type:  :setter
        args:  [int java.lang.String java.lang.Integer javax.swing.KeyStroke]
       flags:  :static :public
  interop fn:  .setActionProperties
 return-type:  void"
  [obj x y z w]
  (org.fife.ui.rtextarea.RTextArea/setActionProperties x y z w))

(defn popup-menu
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getPopupMenu
 return-type:  javax.swing.JPopupMenu"
  [obj]
  (.getPopupMenu obj))

(defn rounded-selection-edges?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setRoundedSelectionEdges
 return-type:  void"
  [obj x]
  (.setRoundedSelectionEdges obj x))

(defn popup-menu!
"wrapper type:  :setter
        args:  [javax.swing.JPopupMenu]
       flags:  :public
  interop fn:  .setPopupMenu
 return-type:  void"
  [obj x]
  (.setPopupMenu obj x))

(defn max-ascent
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getMaxAscent
 return-type:  int"
  [obj]
  (.getMaxAscent obj))

(defn line-highlight-manager
"wrapper type:  :getter
        args:  []
       flags:  
  interop fn:  .getLineHighlightManager
 return-type:  org.fife.ui.rtextarea.LineHighlightManager"
  [obj]
  (.getLineHighlightManager obj))

(defn can-redo?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .canRedo
 return-type:  boolean"
  [obj]
  (.canRedo obj))

(defn can-undo?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .canUndo
 return-type:  boolean"
  [obj]
  (.canUndo obj))

; (defn print
; "wrapper type:  :unknown
;         args:  [java.awt.Graphics java.awt.print.PageFormat int]
;        flags:  :public
;   interop fn:  .print
;  return-type:  int"
;   [obj x y z]
;   (.print obj x y z))

(defn rtextarea
"wrapper type:  :constructor
        args:  [int int]
       flags:  :public
  interop fn:  Rtextarea.
 return-type:  "
  [obj x y]
  (RTextArea. obj x y))

(defn rtextarea
"wrapper type:  :constructor
        args:  []
       flags:  :public
  interop fn:  Rtextarea.
 return-type:  "
  [obj]
  (RTextArea. obj))

(defn redo-action
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .REDO_ACTION
 return-type:  "
  [obj]
  (org.fife.ui.rtextarea.RTextArea/REDO_ACTION))

(defn copy-action
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .COPY_ACTION
 return-type:  "
  [obj]
  (org.fife.ui.rtextarea.RTextArea/COPY_ACTION))

(defn redo-last-action
"wrapper type:  :unknown
        args:  []
       flags:  :public
  interop fn:  .redoLastAction
 return-type:  void"
  [obj]
  (.redoLastAction obj))

(defn begin-recording-macro!
"wrapper type:  :should-have-bang
        args:  []
       flags:  :synchronized :static :public
  interop fn:  .beginRecordingMacro
 return-type:  void"
  [obj]
  (RTextArea/beginRecordingMacro))


(defn fold-indicator-enabled!
  [obj b]
  (.setFoldIndicatorEnabled obj b))



