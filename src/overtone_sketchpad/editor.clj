(ns clooj.editor
    (:use [seesaw core graphics color font border]
          [clooj utils])
    (:require [clooj.rsyntax :as rsyntax]
              [clooj.doc-browser :as db]
              [clooj.highlighting :as h]
              [clooj.brackets :as b]
              [clooj.help :as help]
              [clooj.search :as search])
    (:import  (org.fife.ui.rtextarea RTextScrollPane)
              (java.awt.event FocusAdapter MouseAdapter)))

(def highlight-agent (agent nil))
(def arglist-agent (agent nil))
(def caret-position (atom nil))

;; caret finding
(defn save-caret-position [app]
  (when-lets [text-area (app :doc-text-area)
              pos (get @caret-position text-area)
              file @(:file app)]
    (when-not (.isDirectory file)
      (let [key-str (str "caret_" (.hashCode (.getAbsolutePath file)))]
        (write-value-to-prefs clooj-prefs key-str pos)))))

(defn load-caret-position [app]
  (when-lets [text-area (app :doc-text-area)
              file @(:file app)]
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

;; view

(defn editor
  [app-atom]
  (let [arglist-label         (label  :foreground     (color :blue)
                                      :id             :arglist-label
                                      :class          :arg-response)
        search-text-area      (text   :visible? 	  false
                      							  :border         (line-border 
                            							  						    :color     :grey
                            							  						    :thickness 1)
                      							  :id             :search-text-area
                                      :class          :search-area)
        arg-search-panel      (horizontal-panel 
                                      :items          [arglist-label search-text-area]
                                      :id             :arg-search-panel
                                      :class          :search-panel)
        pos-label             (label  :id             :pos-label
                                      :class          :pos-label)
        position-search-panel (horizontal-panel 
                                      :items          [pos-label 
                                                     [:fill-h 10]
                                                       arg-search-panel
                                                      :fill-h]
                                      :maximum-size   [2000 :by 15]
                                      :id             :position-search-panel
                                      :class          :search-panel)
        doc-label             (label  :text           "Source Editor"
                                      :id             :doc-label
                                      :class          :editor-comp)
        doc-label-panel       (horizontal-panel       
                                      :items          [doc-label
                                                       :fill-h]
                                      :id             :doc-label-panel
                                      :class          :editor-comp)
        doc-text-area         (rsyntax/text-area    
                                      :wrap-lines?    false
                                      :syntax         :clojure
                                      :id             :doc-text-area
                                      :class          [:editor-comp :syntax-editor])
        doc-scroll-pane       (RTextScrollPane. doc-text-area)
        doc-text-panel        (vertical-panel       
                                      :items         [doc-label-panel 
                                                      doc-scroll-pane 
                                                      position-search-panel]
                                      :id             :doc-text-panel
                                      :class          :editor-comp)]

    (.setCodeFoldingEnabled doc-text-area true)
    (.setAntiAliasingEnabled doc-text-area true)
    (.setFoldIndicatorEnabled doc-scroll-pane true)

        ;; fonts
    (cond 
      (is-mac)
      (config! doc-text-area :font (font 
                             :name "COURIER-NEW"
                             :size 12))
      (is-win)
      (config! doc-text-area :font (font 
                             :name "COURIER-NEW"
                             :size 12))
      :else 
      (config! doc-text-area :font (font 
                             :name "MONOSPACED"
                             :size 12)))

    (swap! app-atom conj (gen-map
                            arglist-label
                            search-text-area
                            arg-search-panel
                            pos-label
                            position-search-panel 
                            doc-label
                            doc-text-area
                            doc-scroll-pane
                            doc-text-panel))
    doc-text-panel))
