(ns sketchpad.input.default
  (:require [sketchpad.config.config :as config]
            [seesaw.keystroke :as keystroke])
  (:import (java.awt Toolkit)
           (java.awt.event InputEvent KeyEvent)
           (javax.swing InputMap KeyStroke)
           (javax.swing.text DefaultEditorKit)
           (org.fife.ui.rtextarea RTextAreaEditorKit)
           (org.fife.ui.rsyntaxtextarea RSyntaxTextAreaEditorKit)
           (org.fife.ui.rsyntaxtextarea RSyntaxTextAreaDefaultInputMap)))

(defn default-input-map
  []
  "Extend the Swing InputMap class and implement key mappings"
  (let [alt (InputEvent/ALT_MASK)
        shift (InputEvent/SHIFT_MASK)
        input-map (RSyntaxTextAreaDefaultInputMap. )
        tool-kit (java.awt.Toolkit/getDefaultToolkit)
        default-modifier (.getMenuShortcutKeyMask tool-kit)]

    (doto input-map
      (.put
        (keystroke/keystroke (:begin-line config/default-buffer-key-bindings))
        DefaultEditorKit/beginLineAction)

      (.put
        (keystroke/keystroke (:selection-begin-line config/default-buffer-key-bindings))
        DefaultEditorKit/selectionBeginLineAction)

      (.put
        (keystroke/keystroke (:begin config/default-buffer-key-bindings))
        DefaultEditorKit/beginAction)

      (.put
        (keystroke/keystroke (:selection-begin config/default-buffer-key-bindings))
        DefaultEditorKit/selectionBeginAction)

      (.put
        (keystroke/keystroke (:end-line config/default-buffer-key-bindings))
        DefaultEditorKit/endLineAction)

      (.put
        (keystroke/keystroke (:selection-end-line config/default-buffer-key-bindings))
        DefaultEditorKit/selectionEndLineAction)

      (.put
        (keystroke/keystroke (:end config/default-buffer-key-bindings))
        DefaultEditorKit/endAction)

      (.put
        (keystroke/keystroke (:selection-end config/default-buffer-key-bindings))
        DefaultEditorKit/selectionEndAction)

      (.put
        (keystroke/keystroke (:backward config/default-buffer-key-bindings))
        DefaultEditorKit/backwardAction)

      (.put
        (keystroke/keystroke (:selection-backward config/default-buffer-key-bindings))
        DefaultEditorKit/selectionBackwardAction)

      (.put
        (keystroke/keystroke (:previous-word config/default-buffer-key-bindings))
        DefaultEditorKit/previousWordAction)

      (.put
        (keystroke/keystroke (:selection-previous-word config/default-buffer-key-bindings))
        DefaultEditorKit/selectionPreviousWordAction)

      (.put
        (keystroke/keystroke (:down config/default-buffer-key-bindings))
        DefaultEditorKit/downAction)

      (.put
        (keystroke/keystroke (:selection-down config/default-buffer-key-bindings))
        DefaultEditorKit/selectionDownAction)

      (.put
        (keystroke/keystroke (:scroll-down config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaScrollDownAction)

      (.put
        (keystroke/keystroke (:scroll-up config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaScrollUpAction)

      (.put
        (keystroke/keystroke (:linedown config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaLineDownAction)

      (.put
        (keystroke/keystroke (:forward config/default-buffer-key-bindings))
        DefaultEditorKit/forwardAction)

      (.put
        (keystroke/keystroke (:selection-forward config/default-buffer-key-bindings))
        DefaultEditorKit/selectionForwardAction)

      (.put
        (keystroke/keystroke (:next-word config/default-buffer-key-bindings))
        DefaultEditorKit/nextWordAction)

      (.put
        (keystroke/keystroke (:selection-next-word config/default-buffer-key-bindings))
        DefaultEditorKit/selectionNextWordAction)
      
      (.put
        (keystroke/keystroke (:up config/default-buffer-key-bindings))
        DefaultEditorKit/upAction)

      (.put
        (keystroke/keystroke (:selection-up config/default-buffer-key-bindings))
        DefaultEditorKit/selectionUpAction)

      (.put
        (keystroke/keystroke (:line-up config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaLineUpAction)
      
      (.put
        (keystroke/keystroke (:page-up config/default-buffer-key-bindings))
        DefaultEditorKit/pageUpAction)
        
      (.put
        (keystroke/keystroke (:selection-page-up config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaSelectionPageUpAction)
      
      (.put
        (keystroke/keystroke (:selection-page-left config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaSelectionPageLeftAction)

      (.put
        (keystroke/keystroke (:page-down config/default-buffer-key-bindings))
        DefaultEditorKit/pageDownAction)
      
      (.put
        (keystroke/keystroke (:selection-page-down config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaSelectionPageDownAction)
        
      (.put
        (keystroke/keystroke (:selection-page-right config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaSelectionPageRightAction)
      
      (.put
        (keystroke/keystroke (:key-cut config/default-buffer-key-bindings))
        DefaultEditorKit/cutAction)
      
      (.put
        (keystroke/keystroke (:key-copy config/default-buffer-key-bindings))
        DefaultEditorKit/copyAction)
      
      (.put
        (keystroke/keystroke (:key-paste config/default-buffer-key-bindings))
        DefaultEditorKit/pasteAction)

      (.put
        (keystroke/keystroke (:delete-cut config/default-buffer-key-bindings))
        DefaultEditorKit/cutAction)

      (.put
        (keystroke/keystroke (:insert-copy config/default-buffer-key-bindings))
        DefaultEditorKit/copyAction)

      (.put
        (keystroke/keystroke (:insert-paste config/default-buffer-key-bindings))
        DefaultEditorKit/pasteAction)

      (.put
        (keystroke/keystroke (:cut config/default-buffer-key-bindings))
        DefaultEditorKit/cutAction)

      (.put
        (keystroke/keystroke (:paste config/default-buffer-key-bindings))
        DefaultEditorKit/pasteAction)

      (.put
        (keystroke/keystroke (:copy config/default-buffer-key-bindings))
        DefaultEditorKit/copyAction)

      (.put
        (keystroke/keystroke (:delete-next-char config/default-buffer-key-bindings))
        DefaultEditorKit/deleteNextCharAction)

      (.put
        (keystroke/keystroke (:delete-rest-of-line config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaDeleteRestOfLineAction)

      (.put
        (keystroke/keystroke (:toggle-text-mode config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaToggleTextModeAction)

      (.put
        (keystroke/keystroke (:select-all config/default-buffer-key-bindings))
        DefaultEditorKit/selectAllAction)

      (.put
        (keystroke/keystroke (:delete-line config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaDeleteLineAction)

      (.put
        (keystroke/keystroke (:join-lines config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaJoinLinesAction)

      (.put
        (keystroke/keystroke (:delete-prev-char config/default-buffer-key-bindings))
        DefaultEditorKit/deletePrevCharAction)

      (.put
        (keystroke/keystroke (:delete-prev-word config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaDeletePrevWordAction)

      (.put
        (keystroke/keystroke (:insert-tab config/default-buffer-key-bindings))
        DefaultEditorKit/insertTabAction)

      (.put
        (keystroke/keystroke (:insert-break config/default-buffer-key-bindings))
        DefaultEditorKit/insertBreakAction)

      (.put
        (keystroke/keystroke (:shift-insert-break config/default-buffer-key-bindings))
        DefaultEditorKit/insertBreakAction)

      (.put
        (keystroke/keystroke (:dumb-complete-word config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaDumbCompleteWordAction)

      (.put
        (keystroke/keystroke (:undo config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaUndoAction)

      (.put
        (keystroke/keystroke (:redo config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaRedoAction)
      
      (.put
        (keystroke/keystroke (:next-bookmark config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaNextBookmarkAction)

      (.put
        (keystroke/keystroke (:prev-bookmark config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaPrevBookmarkAction)

      (.put
        (keystroke/keystroke (:toggle-bookmark config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaToggleBookmarkAction)

      (.put
        (keystroke/keystroke (:prev-occurrence config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaPrevOccurrenceAction)

      (.put
        (keystroke/keystroke (:next-occurrence config/default-buffer-key-bindings))
        RTextAreaEditorKit/rtaNextOccurrenceAction))

    input-map))