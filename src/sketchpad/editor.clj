(ns sketchpad.editor
    (:use [seesaw core graphics color font border meta]
          [sketchpad prefs utils]
          [clojure.pprint]
          [sketchpad.vim-mode]
          [sketchpad.toggle-vim-mode-action]
          [sketchpad.tab-manager]
          [rounded-border.core])
    (:require [sketchpad.rsyntax :as rsyntax]
              [clooj.doc-browser :as db]
              [clooj.highlighting :as h]
              [clooj.brackets :as b]
              [sketchpad.help :as help]
              [clooj.search :as search]
              [sketchpad.rtextscrollpane :as sp]
              [sketchpad.rsyntaxtextarea :as rs]
              [sketchpad.rtextarea :as ta]
              [sketchpad.gutter :as gutter]
              [sketchpad.config :as config]
              [sketchpad.tab-ui :as tab])
    (:import  (java.awt.event FocusAdapter MouseAdapter KeyAdapter)
              (javax.swing.event ChangeListener)
              (javax.swing.UIManager)))

(def highlight-agent (agent nil))
(def arglist-agent (agent nil))
(def caret-position (atom nil))

; caret finding
(defn save-caret-position [app]
  (when-lets [text-area (app :doc-text-area)
              pos (get @caret-position text-area)
              file @(:current-file app)]
    (when-not (.isDirectory file)
      (let [key-str (str "caret_" (.hashCode (.getAbsolutePath file)))]
        (write-value-to-prefs clooj-prefs key-str pos)))))

(defn load-caret-position [app]
  (when-lets [text-area (app :doc-text-area)
              file @(:current-file app)]
    (when-not (.isDirectory file)
      (when-lets [key-str (str "caret_" (.hashCode (.getAbsolutePath file)))
                  pos (read-value-from-prefs clooj-prefs key-str)]
        (let [length (.. text-area getDocument getLength)
              pos2 (Math/min pos length)]
          (.setCaretPosition text-area pos2)
          (scroll-to-caret text-area))))))

(defn update-caret-position [text-comp]
  (swap! caret-position assoc text-comp (.getCaretPosition text-comp)))

(defn display-caret-position [app]
  (let [{:keys [row col]} (get-caret-coords (:doc-text-area app))]
    (.setText (:pos-label app) (str " " (inc row) "|" (inc col)))))
     
(defn help-handle-caret-move [app text-comp]
  (awt-event
    (when (@db/help-state :visible)
      (let [[start _] (db/local-token-location (get-text-str text-comp) 
                                            (.getCaretPosition text-comp))]
        (if-not (= start (@db/help-state :pos))
          (db/hide-tab-help app)
          (db/show-tab-help app text-comp identity))))))

(defn handle-caret-move [app text-comp ns]
  (update-caret-position text-comp)
  (help-handle-caret-move app text-comp)
  (send-off highlight-agent
            (fn [old-pos]
              (try
                (let [pos (@caret-position text-comp)
                      text (get-text-str text-comp)]
                  (when-not (= pos old-pos)
                    (let [enclosing-brackets (b/find-enclosing-brackets text pos)
                          bad-brackets (b/find-bad-brackets text)
                          good-enclosures (clojure.set/difference
                                            (set enclosing-brackets) (set bad-brackets))]
                      (awt-event
                        (h/highlight-brackets text-comp good-enclosures bad-brackets)))))
                (catch Throwable t (.printStackTrace t)))))
  (when ns
    (send-off arglist-agent 
              (fn [old-pos]
                (try
                  (let [pos (@caret-position text-comp)
                        text (get-text-str text-comp)]
                    (when-not (= pos old-pos)
                      (let [arglist-text (help/arglist-from-caret-pos app ns text pos)]
                        (awt-event (.setText (:arglist-label app) arglist-text)))))
                  (catch Throwable t (.printStackTrace t)))))))

;; double-click paren to select form

(defn double-click-selector [text-comp]
  (.addMouseListener text-comp
    (proxy [MouseAdapter] []
      (mouseClicked [e]
        (when (== 2 (.getClickCount e))
          (when-lets [pos (.viewToModel text-comp (.getPoint e))
                      c (.. text-comp getDocument (getText pos 1) (charAt 0))
                      pos (cond (#{\( \[ \{ \"} c) (inc pos)
                                (#{\) \] \} \"} c) pos)
                      [a b] (b/find-enclosing-brackets (get-text-str text-comp) pos)]
            (set-selection text-comp a (inc b))))))))

;; search 

(defn setup-search-text-area [app]
  (let [sta (doto (app :search-text-area)
      (.addFocusListener (proxy [FocusAdapter] [] (focusLost [_] (search/stop-find app)))))]
    (add-text-change-listener sta #(search/update-find-highlight app false))
    (attach-action-keys sta ["ENTER" #(search/highlight-step app false)]
                            ["shift ENTER" #(search/highlight-step app true)]
                            ["ESCAPE" #(search/escape-find app)])))


(defn setup-cmd-line-area [app]
  (let [sta (doto (app :editor-command-line)
      (.addFocusListener (proxy [FocusAdapter] [] (focusLost [_] (search/stop-find app)))))]
    (attach-action-keys sta ["ENTER" #(search/highlight-step app false)]
                            ["shift ENTER" #(search/highlight-step app true)]
                            ["ESCAPE" #(search/escape-find app)])))

;; view

(defn setup-text-area-font
  [app]
    (cond 
      (is-mac)
      (do 
        (config! (app :doc-text-area) 
                      :font (font :name "MENLO"
                                  :style :bold
                                  :size 14)))
      (is-win)
      (config! (app :doc-text-area) :font (font 
                             :name "COURIER-NEW"
                             :size 14))
      :else 
      (config! (app :doc-text-area) :font (font 
                             :name "MONOSPACED"
                             :size 14))))

(def key-stack (atom []))

(defn command-line-key-listener
  []
  (let [global-key-listener (proxy [KeyAdapter] []
                              (keyTyped [e]
                                (swap! key-stack (fn [s] (conj e s)))
                                (pprint @key-stack)))]))

(defn dirty-state-listener
  [app]
  (let [listener (proxy [KeyAdapter] []
                    (keyTyped [e]
                      ))]))

(defn set-text-area-preffs
  [app rta]
  (.addKeyListener rta (dirty-state-listener app))
  (.addKeyListener rta (command-line-key-listener))

  )


(defn doc-tab [title tip content]
  {:title title
   :tip tip
   :content content})


(defn put [laf k v]
  (.put laf (str "TabbedPane." k) v))


(defn editor
  [app-atom]
  (let [arglist-label         (label  :foreground     (color :blue)
                                      :id             :arglist-label
                                      :class          :arg-response)
        search-text-area      (text   :visible? 	  false
                      					      :border         (line-border :color     :grey
                            			 				  				        :thickness 1)
                      							  :id             :search-text-area
                                      :class          :search-area)
        arg-search-panel      (horizontal-panel 
                                      :items          [arglist-label search-text-area]
                                      :id             :arg-search-panel
                                      :class          :search-panel)
        pos-label             (label  :id             :pos-label
                                      :class          :label)
        position-search-panel (horizontal-panel
                                      :items          [pos-label 
                                                     [:fill-h 10]
                                                       arg-search-panel
                                                      :fill-h]
                                      :maximum-size   [2000 :by 15]
                                      :id             :position-search-panel
                                      :class          :search-panel)
        editor-helpers-panel  (vertical-panel       
                                      :items [position-search-panel])
        doc-text-area         (rsyntax/text-area    
                              :syntax         :clojure)
        editor-tabbed-panel   (tabbed-panel :placement :top
                                            :overflow :wrap
                                            :background (color :black)
                                            :border (empty-border :thickness 0))
        doc-text-panel        (vertical-panel  
                                      :items         [editor-tabbed-panel 
                                                      editor-helpers-panel]
                                      :id             :doc-text-panel
                                      :class          :editor-comp)]
    ;; set tab ui
    (.setUI editor-tabbed-panel (tab/sketchpad-tab-ui))

    (config! arglist-label :background config/app-color)
    (config! arg-search-panel :background config/app-color)

    (config! arg-search-panel :background config/app-color)
    (config! arglist-label :background config/app-color)

    (swap! app-atom conj (gen-map
                            arglist-label
                            search-text-area
                            arg-search-panel
                            pos-label
                            position-search-panel 
                            editor-tabbed-panel 
                            editor-helpers-panel))
    doc-text-panel))

