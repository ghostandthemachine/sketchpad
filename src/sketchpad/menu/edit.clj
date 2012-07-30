(ns sketchpad.menu.edit 
  (:use [seesaw core keystroke])
  (:require [sketchpad.rsyntaxtextarea :as rsyntaxtextarea]
            [sketchpad.rtextarea :as rtext]
            [sketchpad.tab :as tab]
            [sketchpad.buffer.action :as buffer.action]
            [sketchpad.menu.menu-utils :as menu-utils]))

(defn undo
"undo last recordable action"
  []
  (buffer.action/undo))

(defn redo
"redo last recordable action"
  []
  (buffer.action/redo))

(defn copy
"copy the current selection"
  []
  (buffer.action/copy))

(defn cut
"cut the current selection"
  []
  (buffer.action/cut))

(defn paste
"Paste the current selection"
  []
  (buffer.action/paste))

(defn make-edit-menu-items []
  {:undo  (menu-item :text "Undo" 
                     :mnemonic "U" 
                     :key (keystroke "meta Z")
                     :listen [:action (fn [_] (undo))])
  :redo   (menu-item :text "Redo" 
                     :mnemonic "Y"
                     :key (keystroke "meta shift Z") 
                     :listen [:action (fn [_] (redo))])
  :copy   (menu-item :text "Copy" 
                     :mnemonic "C" 
                     :key (keystroke "meta C") 
                     :listen [:action (fn [_] (copy))])
  :paste  (menu-item :text "Paste" 
                     :mnemonic "P" 
                     :key (keystroke "meta V") 
                     :listen [:action (fn [_] (paste))])
  :cut    (menu-item :text "Cut" 
                     :mnemonic "X" 
                     :key (keystroke "meta X") 
                     :listen [:action (fn [_] (cut))])})

(def edit-menu-items
  [[:undo "Undo" "meta Z" undo]
   [:redo "Redo" "meta shift Z" redo]
   [:copy "Copy" "meta C" copy]
   [:copy "Paste" "meta V" paste]
   [:copy "Cut" "meta X" cut]])

;(defn make-edit-menu-items []
;   (menu-utils/make-menu edit-menu-items))

(defn make-edit-menu
  []
  (let [menu-items (make-edit-menu-items)]
    (menu :text "Edit" 
          :mnemonic "E"
          :items [
                  (menu-items :undo)
                  (menu-items :redo)
                  (separator)
                  (menu-items :copy)
                  (menu-items :paste)
                  (separator)
                  (menu-items :cut)])))