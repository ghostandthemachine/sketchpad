(ns overtone-sketchpad.popup
  (:use [clojure.pprint]
        [seesaw.core :only (separator popup menu radio-menu-item label config! config)])
  (:require 
        [overtone-sketchpad.rsyntaxtextarea :as cr]
        [overtone-sketchpad.rtextscrollpane :as sp]
        [overtone-sketchpad.rtextarea :as rt])
  (:import (javax.swing.JPanel)))

(def key-binding-mode (atom :none))

(defn editor-key-binding-mode! 
	"Set the current key binding edit mode.\n
	modes: none"
	[comp mode]
	(if (not (contains? [:vi :vim :emacs :default :eclipse] mode))
		(println "Error setting editor key binding mode. " (str mode) " is not an option.")
		(do 
			(pprint "Current Ediotr mode: " mode)
			(swap! key-binding-mode mode))))

(def default-bindings 
	{:copy \C
	 :paste \p
	 :cut \X
	 :undo \Z
	 :redo \Y})

(defn gutter-popup 
	[rscroll-pane]
	(let [textarea (sp/text-area rscroll-pane)]
		(.setComponentPopupMenu 
				(sp/gutter rscroll-pane)
					 (popup 
					 	:items [(menu    
					 				:text "Edit Mode"
									:items [(radio-menu-item 
												:text 	"Default"
												:listen [:action #(editor-key-binding-mode! textarea :default)])
											(radio-menu-item 
												:text 	"Vi"
												:listen [:action #(editor-key-binding-mode! textarea :vi)])
											(radio-menu-item 
												:text 	"Emacs"
												:listen [:action #(editor-key-binding-mode! textarea :emacs)])
											(radio-menu-item 
												:text 	"Eclipse"
												:listen [:action #(editor-key-binding-mode! textarea :default)])])]))))


