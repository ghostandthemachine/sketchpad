(ns sketchpad.default-mode
	(:use [sketchpad.edit-mode])
	(:import (java.awt Toolkit)
			 (java.awt.event InputEvent KeyEvent)
			 (javax.swing InputMap KeyStroke)
			 (javax.swing.text DefaultEditorKit)
			 (org.fife.ui.rtextarea RTextAreaEditorKit)
			 (org.fife.ui.rsyntaxtextarea RSyntaxTextAreaDefaultInputMap)))

(defn default-input-map
	[]
	"Extend the Swing InputMap class and implement key mappings"
	(println "Create default input map")
	(let [alt (InputEvent/ALT_MASK)
		  shift (InputEvent/SHIFT_MASK)
		  input-map (RSyntaxTextAreaDefaultInputMap. )]
		(doto input-map
			; toggle vim mode
			(.put
				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_ESCAPE
				(int 0))
				 "toggle-vim-mode"))
	input-map))

; (defn default-input-map
; 	[]
; 	"Extend the Swing InputMap class and implement key mappings"
; 	(println "Create default input map")
; 	(let [alt (InputEvent/ALT_MASK)
; 		  shift (InputEvent/SHIFT_MASK)
; 		  input-map (proxy [javax.swing.InputMap] [])]
; 		(doto input-map
; 			;; beginAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_HOME
; 				(int default-modifier))	 	    
; 				RTextAreaEditorKit/beginAction)
; 			;; selectionBeginAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_HOME
; 				(int default-modifier)) 				 	    
; 				RTextAreaEditorKit/selectionBeginAction)
; 			;; beginLineAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_O
; 				(int shift))		            			 	    
; 				RTextAreaEditorKit/beginLineAction)
; 			;; selectionBeginLineAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_HOME
; 				(int shift)) 		        		 	        
; 				RTextAreaEditorKit/selectionBeginLineAction)
; 			;; endLineAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_END
; 				(int 0)) 				   			 	        
; 				RTextAreaEditorKit/endLineAction)
; 			;; selectionEndLineAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_END
; 				(int shift)) 		 				 	        
; 				RTextAreaEditorKit/selectionEndLineAction)
; 			;; endAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_END
; 				(int default-modifier)) 				 	    
; 				RTextAreaEditorKit/endAction)
; 			;; selectionEndAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_END
; 				(int default-modifier)) 				 	    
; 				RTextAreaEditorKit/selectionEndAction)

; 			;; backwardAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_LEFT
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/backwardAction)
; 			;; selectionBackwardAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_LEFT
; 				(int shift))				             	    
; 				RTextAreaEditorKit/selectionBackwardAction)
; 			;; previousWordAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_LEFT
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/previousWordAction)
; 			;; selectionPreviousWordAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_LEFT
; 				(int (bit-or default-modifier shift)))   	
; 				RTextAreaEditorKit/selectionPreviousWordAction)
; 			;; downAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DOWN
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/downAction)
; 			;; selectionDownAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DOWN
; 				(int shift))				             	    
; 				RTextAreaEditorKit/selectionDownAction)
; 			;; rtaScrollDownAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DOWN
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/rtaScrollDownAction)
; 			;; rtaLineDownAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DOWN
; 				(int alt))				             	            
; 				RTextAreaEditorKit/rtaLineDownAction)
; 			;; forwardAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_RIGHT
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/forwardAction)
; 			;; selectionForwardAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_RIGHT
; 				(int shift))				             	    
; 				RTextAreaEditorKit/selectionForwardAction)
; 			;; nextWordAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_B
; 				(int 0))				 	    
; 				RTextAreaEditorKit/nextWordAction)
; 			;; selectionNextWordAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_RIGHT
; 				(int (bit-or default-modifier shift)))   	
; 				RTextAreaEditorKit/selectionNextWordAction)
; 			;; upAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_UP
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/upAction)
; 			;; selectionUpAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_UP
; 				(int shift))				             	    
; 				RTextAreaEditorKit/selectionUpAction)
; 			;; rtaScrollUpAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_UP
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/rtaScrollUpAction)
; 			;; rtaLineUpAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_UP
; 				(int alt))				             	            
; 				RTextAreaEditorKit/rtaLineUpAction)
; 			;; upAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_UP
; 				(int 0))				                 		
; 				DefaultEditorKit/upAction)
; 			;; rtaSelectionPageUpAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_UP
; 				(int shift))				             	    
; 				RTextAreaEditorKit/rtaSelectionPageUpAction)
; 			;; rtaSelectionPageLeftAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_UP
; 				(int (bit-or default-modifier shift)))      	
; 				RTextAreaEditorKit/rtaSelectionPageLeftAction)
; 			;; pageDownAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_DOWN
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/pageDownAction)
; 			;; rtaSelectionPageDownAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_DOWN
; 				(int shift))				             	    
; 				RTextAreaEditorKit/rtaSelectionPageDownAction)
; 			;; rtaSelectionPageRightAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PAGE_DOWN
; 				(int (bit-or default-modifier shift)))     	
; 				RTextAreaEditorKit/rtaSelectionPageRightAction)

; 			;; cutAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_CUT
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/cutAction)
; 			;; copyAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_COPY
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/copyAction)
; 			;; pasteAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_PASTE
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/pasteAction)

; 			;; cutAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_X
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/cutAction)
; 			;; copyAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_C
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/copyAction)
; 			;; pasteAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_P
; 				(int 0))				 	    
; 				RTextAreaEditorKit/pasteAction)
; 			;; deleteNextCharAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_X
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/deleteNextCharAction)
; 			;; cutAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DELETE
; 				(int shift))				             	    
; 				RTextAreaEditorKit/cutAction)
; 			;; rtaDeleteRestOfLineAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_DELETE
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/rtaDeleteRestOfLineAction)
; 			;; rtaToggleTextModeAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_INSERT
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/rtaToggleTextModeAction)
; 			;; pasteAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_INSERT
; 				(int shift))				             	    
; 				RTextAreaEditorKit/pasteAction)
; 			;; copyAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_INSERT
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/copyAction)
; 			;; selectAllAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_A
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/selectAllAction)

; 			;; rtaDeleteLineAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_D
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/rtaDeleteLineAction)
; 			;; rtaJoinLinesAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_J
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/rtaJoinLinesAction)

; 			;; deletePrevCharAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_X
; 				(int shift))				             	    
; 				RTextAreaEditorKit/deletePrevCharAction)
; 			;; rtaDeletePrevWordAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_BACK_SPACE
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/rtaDeletePrevWordAction)
; 			;; insertTabAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_TAB
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/insertTabAction)
; 			;; insertBreakAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_ENTER
; 				(int 0))				                 	    
; 				RTextAreaEditorKit/insertBreakAction)
; 			;; insertBreakAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_ENTER
; 				(int shift))				             	    
; 				RTextAreaEditorKit/insertBreakAction)
; 			;; rtaDumbCompleteWordAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_ENTER
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/rtaDumbCompleteWordAction)

; 			;; rtaUndoAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_U
; 				(int 0))				 	    
; 				RTextAreaEditorKit/rtaUndoAction)
; 			;; rtaRedoAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_R
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/rtaRedoAction)

; 			;; rtaNextBookmarkAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_F2
; 				(int 0))       	    
; 				RTextAreaEditorKit/rtaNextBookmarkAction)
; 			;; rtaPrevBookmarkAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_F2
; 				(int shift))				             	    
; 				RTextAreaEditorKit/rtaPrevBookmarkAction)
; 			;; rtaToggleBookmarkAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_F2
; 				(int default-modifier))				 	    
; 				RTextAreaEditorKit/rtaToggleBookmarkAction)

; 			;; rtaPrevOccurrenceAction
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_K
; 				(int (bit-or default-modifier shift)))
; 				 RTextAreaEditorKit/rtaPrevOccurrenceAction)
; 			;; rtaNextOccurrenceAction)
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_K
; 				(int default-modifier))
; 				RTextAreaEditorKit/rtaNextOccurrenceAction)

; 			; toggle vim mode
; 			(.put
; 				(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_ESCAPE
; 				(int 0))
; 				 "toggle-vim-mode")

; 			)
; 	input-map))


