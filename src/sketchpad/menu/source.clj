 (ns sketchpad.menu.source
  (:use [clojure.java.shell])
  (:require [seesaw.core :as seesaw]
            [seesaw.keystroke :as keystroke]
            [sketchpad.buffer.action :as buffer.action]
            [sketchpad.repl.print :as sketchpad.repl.print]
            [sketchpad.util.tab :as tab]
            [sketchpad.buffer.spell-check :as spell-check]
            [sketchpad.config.layout :as layout]
            [sketchpad.state.state :as state]))

(defn toggle-repl-if-needed
"If a given command appends a function to the REPL and the REPL is currently hidden, show it."
	[]
	(when (not @layout/show-repl)
		(layout/toggle-repl)))
   
(defn search
"Focus the editor REPL and create a search function."
([]
  (sketchpad.repl.print/append-command (str "(search \"\")") -2)
  (toggle-repl-if-needed)
  (tab/focus-application-repl)))

(defn search-replace
"Focus the editor REPL and create a search replace function."
([]
  (sketchpad.repl.print/append-command (str "(search-replace \"\")") -2)
  (toggle-repl-if-needed)
  (tab/focus-application-repl)))

(defn search-replace-all
"Focus the editor REPL and create a search replace all function."
([]
  (sketchpad.repl.print/append-command (str "(search-replace-all \"\")") -2)
  (toggle-repl-if-needed)
  (tab/focus-application-repl)))

(defn toggle-comment
"Comment out the current line."
([]
  (when (tab/tabs?)
    (buffer.action/toggle-comment))))

(defn add-spell-check
"Add a spell checker to the active buffer."
  []
  (let [text-area (tab/current-text-area)]
    (spell-check/add-english-spell-checker text-area)
    (.repaint text-area)))

(defn remove-spell-check
"Remove a spell checker to the active buffer."
  []
  (let [text-area (tab/current-text-area)]
    (spell-check/remove-english-spell-checker text-area)
    (.repaint text-area)))

(defn grep-files
"Grep the current projects or a given path."
  ([search-term] 
  	(:out  (apply sh "grep" "-nir" "-I" search-term (keys @(:projects @state/app)))))
  ([search-term & opts]
    (:out  (apply sh "grep" "-nir" "-I" search-term opts))))

(defn grep-cmd
"Focus the editor REPL and create a search function."
([]
  (sketchpad.repl.print/append-command (str "(grep \"\")") -2)
  (toggle-repl-if-needed)
  (tab/focus-application-repl)))

(defn increase-font-size
"Increase the curren buffer font size."
  []
  (when (tab/tabs?)
    (buffer.action/increase-font)))

(defn decrease-font-size
"Decrease the curren buffer font size."
  []
  (when (tab/tabs?)
    (buffer.action/decrease-font)))

(defn make-source-menu-items []
 {:search (seesaw.core/menu-item :text "Search..." 
                              :mnemonic "F" 
                              :key (keystroke/keystroke "meta F") 
                              :listen [:action (fn [_] (search))])
 :search-replace (seesaw.core/menu-item :text "Search Replace..." 
                              :mnemonic "F" 
                              :key (keystroke/keystroke "meta shift F") 
                              :listen [:action (fn [_] (search-replace))])
 :search-replace-all (seesaw.core/menu-item :text "Search Replace All..." 
                              :mnemonic "F" 
                              :key (keystroke/keystroke "meta control F") 
                              :listen [:action (fn [_] (search-replace-all))])
 :toggle-comment (seesaw.core/menu-item :text "Toggle Comment" 
                              :mnemonic "T" 
                              :key (keystroke/keystroke "meta BACK_SLASH") 
                              :listen [:action (fn [_] (toggle-comment))])
 :add-spell-check (seesaw.core/menu-item :text "Add Spell Checker" 
                              :mnemonic "S" 
                              :key (keystroke/keystroke "meta control S") 
                              :listen [:action (fn [_] (add-spell-check))])
 :remove-spell-check (seesaw.core/menu-item :text "Remove Spell Checker" 
                              :mnemonic "S" 
                              :key (keystroke/keystroke "meta alt S") 
                              :listen [:action (fn [_] (remove-spell-check))])
 :grep (seesaw.core/menu-item :text "Grep project or path..." 
                              :mnemonic "G" 
                              :key (keystroke/keystroke "meta G") 
                              :listen [:action (fn [_] (grep-cmd))])
 :increase-font-size (seesaw.core/menu-item :text "Increase font" 
                              :mnemonic "+" 
                              :key (keystroke/keystroke "meta PLUS") 
                              :listen [:action (fn [_] (increase-font-size))])
 :decrease-font-size (seesaw.core/menu-item :text "Decrease font" 
                              :mnemonic "-" 
                              :key (keystroke/keystroke "meta MINUS") 
                              :listen [:action (fn [_] (decrease-font-size))])
 })

(defn make-source-menu
  []
  (let [menu-items (make-source-menu-items)]
    (seesaw.core/menu :text "Source"
          :mnemonic "S"
          :items [
                  (menu-items :search)
                  (menu-items :search-replace)
                  (menu-items :search-replace-all)
                  (seesaw.core/separator)
                  (menu-items :grep)
                  (seesaw.core/separator)
                  (menu-items :toggle-comment)
                  (seesaw.core/separator)
                  (menu-items :increase-font-size)
                  (menu-items :decrease-font-size)
                  ; (seesaw.core/separator)
                  ; (menu-items :add-spell-check)
                  ; (menu-items :remove-spell-check)
                  ])))

                  