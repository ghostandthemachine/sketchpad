(ns clooj.doc-browser

    (:use     [seesaw core graphics color border]
              [clooj.highlighting]
              [clooj.help]
              [clooj.utils]
              [sketchpad.layout-config]
              [clojure.repl :only (source-fn)]
              [clooj.collaj :only (raw-data)])

    (:require [clooj.rsyntax :as rsyntax]
              [cemerick.pomegranate.aether :as aether])

    (:import  (java.io LineNumberReader InputStreamReader PushbackReader)
              (clojure.lang RT Reflector)
              (java.awt Point)           
              (javax.swing DefaultListCellRenderer ListSelectionModel)
              (javax.swing.event ListSelectionListener)
              (java.util Vector)
              (java.lang.reflect Modifier)
              (java.net URLEncoder)))

;; tab help
; (defonce help-state (atom {:visible false :token nil :pos nil}))
(defn url-encode
  "URL-encode a string."
  [s]
  (URLEncoder/encode s "UTF-8"))
  
(defn raw-data
  "Get a clojure data collection of raw search
   results from collaj.net"
  [terms]
  (read-string (slurp (str "http://collaj.net/data/"
                           (url-encode terms)))))

(defn var-map [v]
  (when-let [m (meta v)]
    (let [ns (:ns m)]
      (-> m
          (select-keys [:doc :ns :name :arglists])
          (assoc :source (binding [*ns* ns]
                           (source-fn (symbol (str ns "/" name)))))))))

(defn var-help [var-map]
  (let [{:keys [doc ns name arglists source]} var-map]
    (str name
         (if ns (str " [" ns "]") "") "\n"
         arglists
         "\n\n"
         (if doc
           (str "Documentation:\n" doc)
           "No documentation found.")
         "\n\n"
         (if source
           (str "Source:\n"
                (if doc
                  (.replace source doc "...docs...")
                  source))
           "No source found."))))

(defn create-param-list
  ([method-or-constructor static]
    (str " (["
         (let [type-names (map #(.getSimpleName %)
                               (.getParameterTypes method-or-constructor))
               param-names (if static type-names (cons "this" type-names))]
           (apply str (interpose " " param-names)))
         "])"))
  ([method-or-constructor]
    (create-param-list method-or-constructor true)))

(defn constructor-help [constructor]
  (str (.. constructor getDeclaringClass getSimpleName) "."
       (create-param-list constructor)))

(defn method-help [method]
  (let [stat (Modifier/isStatic (.getModifiers method))]
    (str
      (if stat
        (str (.. method getDeclaringClass getSimpleName)
             "/" (.getName method))
        (str "." (.getName method)))
     (create-param-list method stat)
      " --> " (.getName (.getReturnType method)))))

(defn field-help [field]
  (let [c (.. field getDeclaringClass getSimpleName)]
  (str
    (if (Modifier/isStatic (.getModifiers field))
      (str (.. field getDeclaringClass getSimpleName)
           "/" (.getName field)
           (when (Modifier/isFinal (.getModifiers field))
             (str " --> " (.. field (get nil) toString))))
      (str "." (.getName field) " --> " (.getName (.getType field)))))))

(defn class-help [c]
  (apply str
         (concat
           [(present-item c) "\n  java class"]
           ["\n\nCONSTRUCTORS\n"]
           (interpose "\n"
                      (sort
                        (for [constructor (.getConstructors c)]
                          (constructor-help constructor))))
           ["\n\nMETHODS\n"]
           (interpose "\n"
                      (sort
                        (for [method (.getMethods c)]
                          (method-help method))))
           ["\n\nFIELDS\n"]
           (interpose "\n"
                      (sort
                        (for [field (.getFields c)]
                          (field-help field)))))))

(defn item-help [item]
  (cond (map? item) (var-help item)
        (class? item) (class-help item)))    

(defn set-first-component [split-pane comp]
  (let [loc (.getDividerLocation split-pane)]
    (.setTopComponent split-pane comp)
    (.setDividerLocation split-pane loc)))

(defn clock-num [i n]
  (if (zero? n)
    0
    (cond (< i 0) (dec n)
          (>= i n) 0
          :else i)))

(defn list-size [list]
  (-> list .getModel .getSize))

(defn advance-help-list [app token index-change-fn]
  (let [help-list (app :completion-list)
        token-pat1 (re-pattern (str "(?i)\\A\\Q" token "\\E"))
        token-pat2 (re-pattern (str "(?i)\\Q" token "\\E"))]
    (if (not= token (@help-state :token))
      (do
        (swap! help-state assoc :token token)
        (.setListData help-list (Vector.))
        (when-let [items (-> app :repl deref :var-maps deref vals)]
          (let [best (sort-by #(.toLowerCase (:name %))
                              (filter
                                #(re-find token-pat1 (:name %))
                                items))
                others (sort-by #(.toLowerCase (:name %))
                                (filter 
                                  #(re-find token-pat2 (.substring (:name %) 1))
                                  items))
                collaj-items (or (raw-data token))]
            (.setListData help-list
                          (Vector. (concat best others collaj-items)))
            (.setSelectedIndex help-list 0)
                   )))
      (let [n (list-size help-list)]
        (when (pos? n)
          (.setSelectedIndex help-list
                             (clock-num
                               (index-change-fn
                                    (.getSelectedIndex help-list))
                               n)))))
    (when (pos? (list-size help-list))
      (set-first-component (app :repl-split-pane)
                           (app :help-text-scroll-pane))
      (set-first-component (app :doc-split-pane)
                           (app :completion-panel))
      ; (.setText (app :repl-label) "Documentation")
      (.ensureIndexIsVisible help-list
                             (.getSelectedIndex help-list)))))
  
(defn get-list-item [app]
  (-> app :completion-list .getSelectedValue))

(defn get-list-artifact [app]
  (binding [*read-eval* false] 
    (read-string (:artifact (get-list-item app)))))

(defn get-list-token [app]
  (let [val (get-list-item app)]
    (str (:ns val) "/" (:name val))))

(defn local-token-location [text pos]
  (let [n (.length text)
        pos (-> pos (Math/max 0) (Math/min n))]
    [(loop [p (dec pos)]
       (if (or (neg? p)
               (some #{(.charAt text p)} non-token-chars))
         (inc p)
         (recur (dec p))))
     (loop [p pos]
       (if (or (>= p n)
               (some #{(.charAt text p)} non-token-chars))
         p
         (recur (inc p))))]))

(defn show-help-text [app choice]
  (let [help-text (or (when choice (item-help choice)) "")]
    (.setText (app :help-text-area) help-text))
  (-> app :help-text-scroll-pane .getViewport
      (.setViewPosition (Point. (int 0) (int 0)))))

(defn show-tab-help [app text-comp index-change-fn]
      (println "add file tree panel for help")
    (if (not @show-file-tree)
    (do 
      (println "add file tree panel for help")
      (toggle-file-tree-panel app :help)))
  (awt-event
    (let [text (get-text-str text-comp)
          pos (.getCaretPosition text-comp)
          [start stop] (local-token-location text pos)]
      (when-let [token (.substring text start stop)]
        (swap! help-state assoc :pos start :visible true)
        (advance-help-list app token index-change-fn)))))

(defn hide-tab-help [app]
  (awt-event
    (when (@help-state :visible)
      (set-first-component (app :repl-split-pane)
                           (app :repl-out-scroll-pane))
      (set-first-component (app :doc-split-pane)
                           (app :docs-tree-panel))
      ; (.setText (app :repl-label) "Clojure REPL output")
      )
    (swap! help-state assoc :visible false :pos nil)
    (if (not @show-file-tree)
      (toggle-file-tree-panel app :help))))

(defn update-ns-form [app]
  (current-ns-form app))

(defn load-dependencies [app artifact]
  (println "Loading " artifact)
  (let [deps (cemerick.pomegranate.aether/resolve-dependencies
               :coordinates [artifact]
               :repositories {"clojars" "http://clojars.org/repo"})]
    (aether/dependency-files deps)))
  
(defn update-token [app text-comp new-token]
  (awt-event
    (let [[start stop] (local-token-location
                         (get-text-str text-comp)
                         (.getCaretPosition text-comp))
          len (- stop start)]
      (when (and (not (empty? new-token)) (-> app :completion-list
                                              .getModel .getSize pos?))
        (.. text-comp getDocument
            (replace start len new-token nil))))))

(defn setup-tab-help [app text-comp]
  (attach-action-keys text-comp
    ["TAB" #(show-tab-help app text-comp inc)]
    ["shift TAB" #(show-tab-help app text-comp dec)]
    ["ESCAPE" #(hide-tab-help app)])
  (attach-child-action-keys text-comp
    ["ENTER" #(@help-state :visible)
             #(do (hide-tab-help app)
                    (load-dependencies app (get-list-artifact app))
                    (update-token app text-comp (get-list-token app)))]))

(defn find-focused-text-pane [app]
  (let [t1 (app :doc-text-area)
        t2 (app :repl-in-text-area)]
    (cond (.hasFocus t1) t1
          (.hasFocus t2) t2)))

(defn setup-completion-list [l app]
  (doto l
    (.setCellRenderer
      (proxy [DefaultListCellRenderer] []
        (getListCellRendererComponent [list item index isSelected cellHasFocus]
          (doto (proxy-super getListCellRendererComponent list item index isSelected cellHasFocus)
            (.setText (present-item item)))))) 
    (.addListSelectionListener
      (reify ListSelectionListener
        (valueChanged [_ e]
          (when-not (.getValueIsAdjusting e)
            (.ensureIndexIsVisible l (.getSelectedIndex l))
            (show-help-text app (.getSelectedValue l))))))
    (on-click 2 #(when-let [text-pane (find-focused-text-pane app)]
                        (update-token app text-pane)))))


;; view

(defn doc-nav
  [app-atom]
  (let [completion-label (label       :text           "Name search"
                                      :id             :doc-nav-label
                                      :class          :doc-nav-comp)
        completion-list (listbox      :border         (compound-border "Doc List")
                                      :id             :doc-nav-list
                                      :class          :doc-nav-comp)
        completion-scroll-pane (scrollable            completion-list
                                      :id             :doc-nav-scrollable
                                      :class          :doc-nav-comp)
        completion-panel (vertical-panel 
                                      :items          [completion-label 
                                                      completion-scroll-pane]
                                      :id             :doc-nav-panel
                                      :class          :doc-nav-comp)]
    (swap! app-atom conj (gen-map
                            completion-label
                            completion-list
                            completion-scroll-pane
                            completion-panel))

    completion-panel))

(defn doc-view
  [app-atom]
  (let [help-text-area (rsyntax/text-area  
                                      :wrap-lines?    true
                                      :editable?      false
                                      :background     (color 0xFF 0xFF 0xE8)
                                      :border         (line-border 
                                                          :color (color "#FFFFE8" 0)
                                                          :thickness 10) 
                                      :syntax         :clojure
                                      :id             :doc-view-text-area
                                      :class          :doc-view-comp)
        help-text-scroll-pane (scrollable             help-text-area
                                      :id             :doc-view-scrollable
                                      :class          :doc-view-comp)]
    (swap! app-atom conj (gen-map
                            help-text-area
                            help-text-scroll-pane))  
    help-text-scroll-pane))