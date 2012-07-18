(ns sketchpad.default-mode
  ; (:use [sketchpad.edit-mode])
  (:import (java.awt Toolkit)
           (java.awt.event InputEvent KeyEvent)
           (javax.swing InputMap KeyStroke)
           (javax.swing.text DefaultEditorKit)
           (org.fife.ui.rtextarea RTextAreaEditorKit)
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
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_ESCAPE
                                            0)
        "toggle-vim-mode")
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_LEFT
                                            default-modifier)
        DefaultEditorKit/beginLineAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_LEFT
                                            (bit-or default-modifier shift))
        DefaultEditorKit/selectionBeginLineAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_UP
                                            default-modifier)
        DefaultEditorKit/beginAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_UP
                                            (bit-or default-modifier shift))
        DefaultEditorKit/selectionBeginAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_RIGHT
                                            default-modifier)
        DefaultEditorKit/endLineAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_RIGHT
                                            (bit-or default-modifier shift))
        DefaultEditorKit/selectionEndLineAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DOWN
                                            default-modifier)
        DefaultEditorKit/endAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DOWN
                                            (bit-or default-modifier shift))
        DefaultEditorKit/selectionEndAction)

      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_LEFT
                                            0)
        DefaultEditorKit/backwardAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_LEFT
                                            shift)
        DefaultEditorKit/selectionBackwardAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_LEFT
                                            alt)
        DefaultEditorKit/previousWordAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_LEFT
                                            (bit-or alt shift))
        DefaultEditorKit/selectionPreviousWordAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DOWN
                                            0)
        DefaultEditorKit/downAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DOWN
                                            shift)
        DefaultEditorKit/selectionDownAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DOWN
                                            (bit-or default-modifier alt))
        RTextAreaEditorKit/rtaScrollDownAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DOWN
                                            alt)
        RTextAreaEditorKit/rtaLineDownAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_RIGHT
                                            0)
        DefaultEditorKit/forwardAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_RIGHT
                                            shift)
        DefaultEditorKit/selectionForwardAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_RIGHT
                                            alt)
        DefaultEditorKit/nextWordAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_RIGHT
                                            (bit-or alt shift))
        DefaultEditorKit/selectionNextWordAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_UP
                                            0)
        DefaultEditorKit/upAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_UP
                                            shift)
        DefaultEditorKit/selectionUpAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_UP
                                            (bit-or default-modifier alt))
        RTextAreaEditorKit/rtaScrollUpAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_UP
                                            alt)
        RTextAreaEditorKit/rtaLineUpAction)

      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_UP
                                            0)
        DefaultEditorKit/pageUpAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_UP
                                            shift)
        RTextAreaEditorKit/rtaSelectionPageUpAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_UP
                                            (bit-or default-modifier shift))
        RTextAreaEditorKit/rtaSelectionPageLeftAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_DOWN
                                            0)
        DefaultEditorKit/pageDownAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_DOWN
                                            shift)
        RTextAreaEditorKit/rtaSelectionPageDownAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_DOWN
                                            (bit-or default-modifier shift))
        RTextAreaEditorKit/rtaSelectionPageRightAction)

      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_CUT
                                            0)
        DefaultEditorKit/cutAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_COPY
                                            0)
        DefaultEditorKit/copyAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PASTE
                                            0)
        DefaultEditorKit/pasteAction)

      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_X
                                            default-modifier)
        DefaultEditorKit/cutAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_C
                                            default-modifier)
        DefaultEditorKit/copyAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_V
                                            default-modifier)
        DefaultEditorKit/pasteAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DELETE
                                            0)
        DefaultEditorKit/deleteNextCharAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DELETE
                                            shift)
        DefaultEditorKit/cutAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DELETE
                                            default-modifier)
        RTextAreaEditorKit/rtaDeleteRestOfLineAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_INSERT
                                            0)
        RTextAreaEditorKit/rtaToggleTextModeAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_INSERT
                                            shift)
        DefaultEditorKit/pasteAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_INSERT
                                            default-modifier)
        DefaultEditorKit/copyAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_A
                                            default-modifier)
        DefaultEditorKit/selectAllAction)

      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_D
                                            default-modifier)
        RTextAreaEditorKit/rtaDeleteLineAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_J
                                            default-modifier)
        RTextAreaEditorKit/rtaJoinLinesAction)

      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_BACK_SPACE
                                            shift)
        DefaultEditorKit/deletePrevCharAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_BACK_SPACE
                                            default-modifier)
        RTextAreaEditorKit/rtaDeletePrevWordAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_TAB
                                            0)
        DefaultEditorKit/insertTabAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_ENTER
                                            0)
        DefaultEditorKit/insertBreakAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_ENTER
                                            shift)
        DefaultEditorKit/insertBreakAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_TAB
                                            0)
        RTextAreaEditorKit/rtaDumbCompleteWordAction)

      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_Z
                                            default-modifier)
        RTextAreaEditorKit/rtaUndoAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_Z
                                            (bit-or default-modifier shift))
        RTextAreaEditorKit/rtaRedoAction)

      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_F2
                                            0)
        RTextAreaEditorKit/rtaNextBookmarkAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_F2
                                            shift)
        RTextAreaEditorKit/rtaPrevBookmarkAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_F2
                                            default-modifier)
        RTextAreaEditorKit/rtaToggleBookmarkAction)

      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_K
                                            (bit-or default-modifier shift))
        RTextAreaEditorKit/rtaPrevOccurrenceAction)
      (.put
        (javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_K
                                            default-modifier)
        RTextAreaEditorKit/rtaNextOccurrenceAction)
      )
    input-map))

