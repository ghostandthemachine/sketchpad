 (ns sketchpad.menu.source
   (:require [seesaw.core :as seesaw]
             [seesaw.keystroke :as keystroke]
             [sketchpad.buffer.action :as buffer.action]
             [sketchpad.repl.print :as sketchpad.repl.print]
             [sketchpad.tab :as tab]))


   
(defn search
"Focus the editor REPL and create a search function."
([]
  (sketchpad.repl.print/append-command (str "(search \"\")") -2)
  (tab/focus-editor-repl)))

(defn search-replace
"Focus the editor REPL and create a search replace function."
([]
  (sketchpad.repl.print/append-command (str "(search-replace \"\")") -2)
  (tab/focus-editor-repl)))

(defn search-replace-all
"Focus the editor REPL and create a search replace all function."
([]
  (sketchpad.repl.print/append-command (str "(search-replace-all \"\")") -2)
  (tab/focus-editor-repl)))


(defn make-source-menu-items []
 {:find (seesaw.core/menu-item :text "Find..." 
                              :mnemonic "F" 
                              :key (keystroke/keystroke "meta F") 
                              :listen [:action (fn [_] (search))])
 :find-replace (seesaw.core/menu-item :text "Find Replace..." 
                              :mnemonic "F" 
                              :key (keystroke/keystroke "meta shift F") 
                              :listen [:action (fn [_] (search-replace))])
 :find-replace-all (seesaw.core/menu-item :text "Find Replace All..." 
                              :mnemonic "F" 
                              :key (keystroke/keystroke "meta control F") 
                              :listen [:action (fn [_] (search-replace-all))])})

(defn make-source-menu
  []
  (let [menu-items (make-source-menu-items)]
    (seesaw.core/menu :text "Source"
          :mnemonic "S"
          :items [
                  (menu-items :find)
                  (menu-items :find-replace)
                  (menu-items :find-replace-all)])))