(ns sketchpad.repl-tab-builder
  (:import (java.awt.event KeyEvent)
           (java.io File StringReader BufferedWriter OutputStreamWriter FileOutputStream)
           (javax.swing JButton JOptionPane JWindow ImageIcon)
           (javax.swing.event DocumentListener))
  (:use [sketchpad styles buffer-edit option-windows repl tab repl-communication repl-button-tab prefs]
    [sketchpad.repl component]
        [clojure pprint]
        [clooj.brackets :only (find-line-group find-enclosing-brackets)]
        [seesaw meta core border color])
  (:require [clojure.string :as string]
            [sketchpad.project :as project]
            [sketchpad.state :as state]))

; (defn add-repl-mouse-handlers
; "Takes a leinengin project, the parent tabbed panel, the repl buffer, and a function which takes 2 args: the lein project for this repl and the repl-buffer."
; [lein-project btn repl-tabbed-panel repl-buffer shut-down-fn]
;   (listen btn
;           :mouse-clicked (fn [e] (let [yes-no-option (close-repl-dialogue)]
;                                    (if (= yes-no-option 0)
;                                      (do
;                                         (apply shut-down-fn lein-project repl-buffer)
;                                         (remove-tab! repl-tabbed-panel repl-buffer)))))))

; (defn repl-key-handler [buffer]
;   (let [ta-in buffer
;         editor-repl-history (get-meta buffer :repl-history)
;         repl (get-meta :repl buffer)
;         get-caret-pos #(.getCaretPosition ta-in)
;         ready #(let [caret-pos (get-caret-pos)
;                      txt (.getText ta-in)
;                      trim-txt (string/trim txt)]
;                  (and
;                    (pos? (.length trim-txt))
;                    (<= (.length trim-txt)
;                        caret-pos)
;                    (= -1 (first (find-enclosing-brackets
;                                   txt
;                                   caret-pos)))))
;         submit #(when-let [txt (get-last-cmd buffer)]
;                   (let [pos (editor-repl-history :pos)]
;                     (if (correct-expression? txt)
;                       (do 
;                         (send-to-lein-repl buffer txt)
;                         (swap! pos (fn [p] 0))))))
;         at-top #(zero? (.getLineOfOffset ta-in (get-caret-pos)))
;         at-bottom #(= (.getLineOfOffset ta-in (get-caret-pos))
;                       (.getLineOfOffset ta-in (.. ta-in getText length)))
;         prev-hist #(update-repl-history-display-position ta-in :dec)
;         next-hist #(update-repl-history-display-position ta-in :inc)]
;     (attach-child-action-keys ta-in ["ENTER" ready submit])
;     (attach-action-keys ta-in ["control UP" prev-hist]
;                               ["control DOWN" next-hist])))

; (defn init-new-repl-tab 
; "Initialize REPL component handlers and add the component to the REPL tabbed panel component."
; [repl]
;   (let [repl-tabbed-panel (@state/app :repl-tabbed-panel)
;         repl-component (:container repl)
;         repl-text-area (:text-area repl)]
;     (add-tab! repl-tabbed-panel repl-tab-component)
;     (show-tab! repl-tabbed-panel (index-of-component repl-tabbed-panel repl-component))
;     (focus-buffer repl-tabbed-panel repl-text-area)))

; (defn new-repl-tab!
; "Builds a new REPL component for a given project."
; [project]
;   (init-new-repl-tab (repl proejct)))




; (defn new-repl-tab!
;   [buffer]
;   (let [app @app
;         project (get-meta buffer :lein-project)
;         cur-project-path (project/current-project)
;         project-map (app :projects)
;         tabbed-panel (app :repl-tabbed-panel)
;         repl-component (make-repl-component project)
;         rsta (select repl-component [:#editor])
;         tab-title (str "REPL# " (+ (tab-count tabbed-panel) 1))
;         cur-buffer (current-buffer (app :editor-tabbed-panel))
;         cur-proj (get-meta cur-buffer :project)]
;     (add-tab! tabbed-panel tab-title repl-component)
;     ; (project/add-repl-to-project! rsta)
;     (let [project (@project-map cur-project-path)
;           index-of-new-tab (index-of tabbed-panel tab-title)
;           project-color (project/get-project-theme-color (:id project))
;           tab (repl-button-tab app tabbed-panel index-of-new-tab project-color)
;           close-button (select tab [:#close-button])
;           tab-label (first (select tab [:.tab-label]))
;           tab-color (atom base-color)]
;       (put-meta! rsta :tab tab)
;       (add-repl-mouse-handlers tabbed-panel rsta close-button repl-component tab-color)
;       (.setTabComponentAt tabbed-panel index-of-new-tab tab)
;       (show-tab! tabbed-panel index-of-new-tab))))


