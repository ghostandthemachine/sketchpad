(ns sketchpad.menu.edit 
  (:use [seesaw core keystroke])
  (:require [sketchpad.rsyntaxtextarea :as rsyntaxtextarea]
            [sketchpad.rtextarea :as rtext]
            [sketchpad.tab-manager :as tab-manager]))

(defonce edit-menu-item-state 
  { :undo (atom false)
    :redo (atom false)
    :cut (atom false)
    :copy (atom false)
    :paste (atom false)})

(defn copy [rta]
"Copy the text currently selected in the visible editor buffer to the system clipboard."
(.copy rta))

(defn cut [rta]
"Cut the text currently selected in the visible editor buffer to the system clipboard."
(.cut rta))

(defn paste [rta]
"Paste what is stored in the current clipboard to the current carret location of the active buffer."
(.paste rta))

(defn undo [rta]
"Undo the last edit action in the current buffer."
(.undoLastAction rta))

(defn redo [rta]
"Redo the last edit action in the current buffer."
(.redoLastAction rta))

(defn make-edit-menu
  [app-atom]
  (menu :text "Edit" 
            :mnemonic "E"
        :items [(menu-item :text "Undo" 
                           :mnemonic "U" 
                           :key (keystroke "meta Z")
                           :listen [:action (fn [_] (undo (tab-manager/current-text-area (@app-atom :editor-tabbed-panel))))])
                (menu-item :text "Redo" 
                           :mnemonic "Y"
                           :key (keystroke "meta shift Z") 
                           :listen [:action (fn [_] (undo (tab-manager/current-text-area (@app-atom :editor-tabbed-panel))))])
                (separator)
                (menu-item :text "Copy" 
                           :mnemonic "C" 
                           :key (keystroke "meta C") 
                           :listen [:action (fn [_] (copy (tab-manager/current-text-area (@app-atom :editor-tabbed-panel))))])
                (menu-item :text "Paste" 
                           :mnemonic "P" 
                           :key (keystroke "meta V") 
                           :listen [:action (fn [_] (paste (tab-manager/current-text-area (@app-atom :editor-tabbed-panel))))])
                (menu-item :text "Cut" 
                           :mnemonic "X" 
                           :key (keystroke "meta X") 
                           :listen [:action (fn [_] (cut (tab-manager/current-text-area (@app-atom :editor-tabbed-panel))))])]))