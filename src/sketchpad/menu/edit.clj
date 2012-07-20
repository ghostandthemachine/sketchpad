(ns sketchpad.menu.edit 
  (:use [seesaw core keystroke])
  (:require [sketchpad.rsyntaxtextarea :as rsyntaxtextarea]
            [sketchpad.rtextarea :as rtext]
            [sketchpad.tab :as tab]
            [sketchpad.buffer :as buffer]
            [sketchpad.menu.menu-utils :as menu-utils]))

(defonce edit-menu-item-state 
  { :undo (atom false)
    :redo (atom false)
    :cut (atom false)
    :copy (atom false)
    :paste (atom false)})

(defn make-edit-menu-items []
  {:undo  (menu-item :text "Undo" 
                     :mnemonic "U" 
                     :key (keystroke "meta Z")
                     :listen [:action (fn [_] (buffer/undo))])
  :redo   (menu-item :text "Redo" 
                     :mnemonic "Y"
                     :key (keystroke "meta shift Z") 
                     :listen [:action (fn [_] (buffer/redo))])
  :copy   (menu-item :text "Copy" 
                     :mnemonic "C" 
                     :key (keystroke "meta C") 
                     :listen [:action (fn [_] (buffer/copy))])
  :paste  (menu-item :text "Paste" 
                     :mnemonic "P" 
                     :key (keystroke "meta V") 
                     :listen [:action (fn [_] (buffer/paste))])
  :cut    (menu-item :text "Cut" 
                     :mnemonic "X" 
                     :key (keystroke "meta X") 
                     :listen [:action (fn [_] (buffer/cut))])})

(def edit-menu-items
  [[:undo "Undo" "meta Z" buffer/undo]
   [:redo "Redo" "meta shift Z" buffer/redo]
   [:copy "Copy" "meta C" buffer/copy]
   [:copy "Paste" "meta V" buffer/paste]
   [:copy "Cut" "meta X" buffer/cut]])

(defn make-edit-menu-items []
  (menu-utils/make-menu edit-menu-items))

(defn make-edit-menu
  []
  (let [menu-items (make-edit-menu-items)]
    ; (println menu-items)
    (menu :text "Edit" 
          :mnemonic "E"
          :items [
                  ; (menu-items :undo)
                  ; (menu-items :redo)
                  ; (separator)
                  ; (menu-items :copy)
                  ; (menu-items :paste)
                  ; (separator)
                  ; (menu-items :cut)
                  ])))