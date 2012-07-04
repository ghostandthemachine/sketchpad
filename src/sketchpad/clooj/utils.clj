; Copyright (c) 2011, Arthur Edelstein
; All rights reserved.
; Eclipse Public License 1.0
; arthuredelstein@gmail.com

(ns sketchpad.utils
  (:require [clojure.string :as string :only (join split)]
            [sketchpad.file-manager :as fm]
            [sketchpad.preview-manager :as preview])
	(:import (java.util UUID)
           (java.awt FileDialog Point Window)
           (java.awt.event ActionListener MouseAdapter)
           (java.util.prefs Preferences)
           (java.security MessageDigest)
           (java.io ByteArrayInputStream ByteArrayOutputStream
                    File FilenameFilter StringReader
                    ObjectInputStream ObjectOutputStream
                    OutputStream Writer PrintStream)
           (javax.swing AbstractAction JButton JFileChooser JMenu JMenuBar JMenuItem BorderFactory
                        JOptionPane JSplitPane KeyStroke SpringLayout SwingUtilities)
           (javax.swing.event CaretListener DocumentListener UndoableEditListener)
           (javax.swing.undo UndoManager)
           (org.fife.ui.rsyntaxtextarea RSyntaxDocument))
  (:use [sketchpad.prefs]
        [seesaw.core :only (config config!)]))

;; general
(def no-project-txt
    "\n Welcome to clooj, a lightweight IDE for clojure\n
     To start coding, you can either\n
       a. create a new project
            (select the Project > New... menu), or
       b. open an existing project
            (select the Project > Open... menu)\n
     and then either\n
       a. create a new file
            (select the File > New menu), or
       b. open an existing file
            (click on it in the tree at left).")
       
(def no-file-txt
    "To edit source code you need to either: <br>
     &nbsp;1. create a new file 
     (select menu <b>File > New...</b>)<br>
     &nbsp;2. edit an existing file by selecting one at left.</html>")

(defmacro do-when [f & args]
  (let [args_ args]
    `(when (and ~@args_)
       (~f ~@args_))))

(defmacro when-lets [bindings & body]
  (assert (vector? bindings))
  (let [n (count bindings)]
    (assert (zero? (mod n 2)))
    (assert (<= 2 n))
  (if (= 2 n)
    `(when-let ~bindings ~@body)
    (let [[a b] (map vec (split-at 2 bindings))]     
      `(when-let ~a (when-lets ~b ~@body))))))

(defn count-while [pred coll]
  (count (take-while pred coll)))

(defn remove-nth [s n]
  (lazy-cat (take n s) (drop (inc n) s)))

(defmacro awt-event [& body]
  `(SwingUtilities/invokeLater
     (fn [] (try ~@body
                 (catch Throwable t# (.printStackTrace t#))))))

(defmacro gen-map [& args]
  (let [kw (map keyword args)]
    (zipmap kw args)))

;; identify OS

(defn get-os []
  (.. System (getProperty "os.name") toLowerCase))

(def is-win
  (memoize #(not (neg? (.indexOf (get-os) "win")))))

(def is-mac
  (memoize #(not (neg? (.indexOf (get-os) "mac")))))

(def is-unix
  (memoize #(not (and (neg? (.indexOf (get-os) "nix"))
                      (neg? (.indexOf (get-os) "nux"))))))

;; text components

(defn get-line-text [text-pane line]
  (let [start (.getLineStartOffset text-pane line)
        length (- (.getLineEndOffset text-pane line) start)]
    (.. text-pane getDocument (getText start length))))

(defn append-text [text-pane text]
  (when-let [doc (.getDocument text-pane)]
    (.insertString doc (.getLength doc) text nil)))

(defn get-coords [text-comp offset]
  (let [row (.getLineOfOffset text-comp offset)
        col (- offset (.getLineStartOffset text-comp row))]
    {:row row :col col}))

(defn get-caret-coords [text-comp]
  (get-coords text-comp (.getCaretPosition text-comp)))

(defn add-text-change-listener [text-comp f]
  "Executes f whenever text is changed in text component."
  (.addDocumentListener
    (.getDocument text-comp)
    (reify DocumentListener
      (insertUpdate [this evt] (f))
      (removeUpdate [this evt] (f))
      (changedUpdate [this evt]))))

(defn remove-text-change-listeners [text-comp]
  (let [d (.getDocument text-comp)]
    (doseq [l (.getDocumentListeners d)]
      (.removeDocumentListener d l))))
                               
(defn get-text-str [text-comp]
  (let [doc (.getDocument text-comp)]
    (.getText doc 0 (.getLength doc))))


(defn get-file-ns [text]
  (try
    (when-let [sexpr (read-string text)]
      (when (= 'ns (first sexpr))
        (str (second sexpr))))
    (catch Exception e)))

(defn add-caret-listener [text-comp f]
  (.addCaretListener text-comp
                     (reify CaretListener (caretUpdate [this evt] (f)))))

;; highlighting

(defn activate-caret-highlighter [handler app k]
  (when-let [text-comp (app k)]
    (let [f #(handler app text-comp (get-file-ns (config (app k) :text)))]
      (add-caret-listener text-comp f)
      (add-text-change-listener text-comp f))))

(defn set-selection [text-comp start end]
  (doto text-comp (.setSelectionStart start) (.setSelectionEnd end)))

(defn scroll-to-pos [text-area offset]
  (let [r (.modelToView text-area offset)
        v (.getParent text-area)
        l (.. v getViewSize height)
        h (.. v getViewRect height)]
    (when r
      (.setViewPosition v
                        (Point. 0 (min (- l h) (max 0 (- (.y r) (/ h 2)))))))))

(defn scroll-to-line [text-comp line]
    (let [text (.getText text-comp)
          pos (inc (.length (string/join "\n" (take (dec line) (string/split text #"\n")))))]
      (.setCaretPosition text-comp pos)
      (scroll-to-pos text-comp pos)))

(defn scroll-to-caret [text-comp]
  (scroll-to-pos text-comp (.getCaretPosition text-comp)))

(defn focus-in-text-component [text-comp]
  (.requestFocusInWindow text-comp)
  (scroll-to-caret text-comp))

(defn get-selected-lines [text-comp]
  (let [row1 (.getLineOfOffset text-comp (.getSelectionStart text-comp))
        row2 (inc (.getLineOfOffset text-comp (.getSelectionEnd text-comp)))]
    (doall (range row1 row2))))

(defn get-selected-line-starts [text-comp]
  (map #(.getLineStartOffset text-comp %)
       (reverse (get-selected-lines text-comp))))

(defn insert-in-selected-row-headers [text-comp txt]
  (awt-event
    (let [starts (get-selected-line-starts text-comp)
          document (.getDocument text-comp)]
      (dorun (map #(.insertString document % txt nil) starts)))))

(defn remove-from-selected-row-headers [text-comp txt]
  (awt-event
    (let [len (count txt)
          document (.getDocument text-comp)]
      (doseq [start (get-selected-line-starts text-comp)]
        (when (= (.getText (.getDocument text-comp) start len) txt)
          (.remove document start len))))))
  
(defn comment-out [text-comp]
  (insert-in-selected-row-headers text-comp ";"))

(defn uncomment-out [text-comp]
  (remove-from-selected-row-headers text-comp ";"))
    
(defn indent [text-comp]
  (when (.isFocusOwner text-comp)
    (insert-in-selected-row-headers text-comp " ")))

(defn unindent [text-comp]
  (when (.isFocusOwner text-comp)
    (remove-from-selected-row-headers text-comp " ")))

;; other gui

(defn make-split-pane [comp1 comp2 horizontal divider-size resize-weight]
  (doto (JSplitPane. (if horizontal JSplitPane/HORIZONTAL_SPLIT 
                                    JSplitPane/VERTICAL_SPLIT)
                     true comp1 comp2)
        (.setResizeWeight resize-weight)
        (.setOneTouchExpandable false)
        (.setBorder (BorderFactory/createEmptyBorder))
        (.setDividerSize divider-size)))

;; keys

(defn get-keystroke [key-shortcut]
  (KeyStroke/getKeyStroke
    (-> key-shortcut
      (.replace "cmd1" (if (is-mac) "meta" "ctrl"))
      (.replace "cmd2" (if (is-mac) "ctrl" "alt")))))

;; actions

(defn attach-child-action-key
  "Maps an input-key on a swing component to an action,
  such that action-fn is executed when pred function is
  true, but the parent (default) action when pred returns
  false."
  [component input-key pred action-fn]
  (let [im (.getInputMap component)
        am (.getActionMap component)
        input-event (get-keystroke input-key)
        parent-action (if-let [tag (.get im input-event)]
                        (.get am tag))
        child-action
          (proxy [AbstractAction] []
            (actionPerformed [e]
              (if (pred)
                (action-fn)
                (when parent-action
                  (.actionPerformed parent-action e)))))
        uuid (.. UUID randomUUID toString)]
    (.put im input-event uuid)
    (.put am uuid child-action)))


(defn attach-child-action-keys [comp & items]
  (doall (map #(apply attach-child-action-key comp %) items)))

(defn attach-action-key
  "Maps an input-key on a swing component to an action-fn."
  [component input-key action-fn]
  (attach-child-action-key component input-key
                           (constantly true) action-fn))

(defn attach-action-keys
  "Maps input keys to action-fns."
  [comp & items]
  (doall (map #(apply attach-action-key comp %) items)))
  
;; buttons
 
(defn create-button [text fn]
  (doto (JButton. text)
    (.addActionListener
      (reify ActionListener
        (actionPerformed [_ _] (fn))))))

;; menus

(defn add-menu-item
  ([menu item-name key-mnemonic key-accelerator response-fn]
    (let [menu-item (JMenuItem. item-name)]  
      (when key-accelerator
        (.setAccelerator menu-item (get-keystroke key-accelerator)))
      (when (and (not (is-mac)) key-mnemonic)
        (.setMnemonic menu-item (.getKeyCode (get-keystroke key-mnemonic))))
      (.addActionListener menu-item
                          (reify ActionListener
                            (actionPerformed [this action-event]
                                             (response-fn))))
      (.add menu menu-item)))
  ([menu item]
    (condp = item
      :sep (.addSeparator menu))))
  
(defn add-menu
  "Each item-tuple is a vector containing a
  menu item's text, mnemonic key, accelerator key, and the function
  it executes."
  [menu-bar title key-mnemonic & item-tuples]
  (let [menu (JMenu. title)]
    (when (and (not (is-mac)) key-mnemonic)
      (.setMnemonic menu (.getKeyCode (get-keystroke key-mnemonic))))
    (doall (map #(apply add-menu-item menu %) item-tuples))
    (.add menu-bar menu)
    menu))

;; mouse

(defn on-click [comp num-clicks fun]
  (.addMouseListener comp
    (proxy [MouseAdapter] []
      (mouseClicked [event]
        (when (== num-clicks (.getClickCount event))
          (.consume event)
          (fun))))))

;; undoability

(defn make-undoable [text-area]
  (let [undoMgr (UndoManager.)]
    (.setLimit undoMgr 1000)
    (.. text-area getDocument (addUndoableEditListener
        (reify UndoableEditListener
          (undoableEditHappened [this evt] (.addEdit undoMgr (.getEdit evt))))))
    (attach-action-keys text-area
      ["cmd1 Z" #(if (.canUndo undoMgr) (.undo undoMgr))]
      ["cmd1 shift Z" #(if (.canRedo undoMgr) (.redo undoMgr))])))


;; file handling

(defn choose-file [parent title suffix load]
  (let [dialog
    (doto (FileDialog. parent title
            (if load FileDialog/LOAD FileDialog/SAVE))
      (.setFilenameFilter
        (reify FilenameFilter
          (accept [this _ name] (. name endsWith suffix))))
      (.setVisible true))
    d (.getDirectory dialog)
    n (.getFile dialog)]
    (if (and d n)
      (File. d n))))

(defn choose-directory [parent title]
  (if (is-mac)
    (let [dirs-on #(System/setProperty
                     "apple.awt.fileDialogForDirectories" (str %))]
      (dirs-on true)
        (let [dir (choose-file parent title "" true)]
          (dirs-on false)
          dir))
    (let [fc (JFileChooser.)
          last-open-dir (read-value-from-prefs clooj-prefs "last-open-dir")]
      (doto fc (.setFileSelectionMode JFileChooser/DIRECTORIES_ONLY)
               (.setDialogTitle title)
               (.setCurrentDirectory (if last-open-dir (File. last-open-dir) nil)))
       (if (= JFileChooser/APPROVE_OPTION (.showOpenDialog fc parent))
         (.getSelectedFile fc)))))
 
(defn get-directories [path]
  (filter #(and (.isDirectory %)
                (not (.startsWith (.getName %) ".")))
          (.listFiles path)))

;; tree seq on widgets (awt or swing)

(defn widget-seq [^java.awt.Component comp]
  (tree-seq #(instance? java.awt.Container %)
            #(seq (.getComponents %))
            comp))

;; saving and restoring window shape in preferences

(defn get-shape [components]
  (for [comp components]
    (condp instance? comp
      Window
        [:window {:x (.getX comp) :y (.getY comp)
                  :w (.getWidth comp) :h (.getHeight comp)}]
      JSplitPane
        [:split-pane {:location (.getDividerLocation comp)}]
      nil)))

(defn watch-shape [components fun]
  (doseq [comp components]
    (condp instance? comp
      Window
        (.addComponentListener comp
          (proxy [java.awt.event.ComponentAdapter] []
            (componentMoved [_] (fun))
            (componentResized [_] (fun))))
      JSplitPane
        (.addPropertyChangeListener comp JSplitPane/DIVIDER_LOCATION_PROPERTY
          (proxy [java.beans.PropertyChangeListener] []
            (propertyChange [_] (fun))))
      nil)))

(defn set-shape [components shape-data]
  (loop [comps components shapes shape-data]
    (let [comp (first comps)
          shape (first shapes)]
      (try
        (when shape
          (condp = (first shape)
            :window
            (let [{:keys [x y w h]} (second shape)]
              (.setBounds comp x y w h))
            :split-pane
            (.setDividerLocation comp (:location (second shape)))
            nil))
        (catch Exception e nil)))
    (when (next comps)
      (recur (next comps) (next shapes)))))

(defn save-shape [prefs name components]
  (write-value-to-prefs prefs name (get-shape components)))

(defn restore-shape [prefs name components]
  (try
    (set-shape components (read-value-from-prefs prefs name))
    (catch Exception e)))
    
(defn confirmed? [question title]
  (= JOptionPane/YES_OPTION
     (JOptionPane/showConfirmDialog
       nil question title  JOptionPane/YES_NO_OPTION)))

(defn persist-window-shape [prefs name ^java.awt.Window window]
  (let [components (widget-seq window)
        shape-persister (agent nil)]
    (restore-shape prefs name components)
    (watch-shape components
                 #(send-off shape-persister
                            (fn [old-shape]
                              (let [shape (get-shape components)]
                                (when (not= old-shape shape)
                                  (write-value-to-prefs prefs name shape))
                                shape))))))

(defn sha1-str [obj]
   (let [bytes (.getBytes (with-out-str (pr obj)))] 
     (String. (.digest (MessageDigest/getInstance "MD") bytes))))

;; streams and writers
 
(defn printstream-to-writer [writer]
  (->
    (proxy [OutputStream] []
      (write
        ([^bytes bs offset length]
          (.write writer
                  (.toCharArray (String. ^bytes bs "utf-8"))
                  offset length))
        ([b]
          (.write writer b)))
      (flush [] (.flush writer))
      (close [] (.close writer)))
    (PrintStream. true)))

;; temp files

(def temp-file-manager (agent 0))

(def changing-file (atom false))

(defn get-temp-file [^File orig]
  (when orig
    (File. (str (.getAbsolutePath orig) "~"))))

(defn dump-temp-doc [app orig-f txt]
  (try 
    (when orig-f
      (let [orig (.getAbsolutePath orig-f)
            f (.getAbsolutePath (get-temp-file orig-f))]
        (spit f txt)
        (awt-event (.updateUI (app :docs-tree)))))
       (catch Exception e nil)))

(defn update-temp [app caret-position-atom]
  (let [text-comp (app :doc-text-area)
        txt (get-text-str text-comp)
        f @(app :current-file)]
    (send-off temp-file-manager
              (fn [old-pos]
                (try
                  (when-let [pos (get @caret-position-atom text-comp)]
                    (when-not (= old-pos pos)
                      (dump-temp-doc app f txt))
                    pos)
                     (catch Throwable t (awt-event (.printStackTrace t))))))))
  
(defn setup-temp-writer [app]
  (let [text-comp (:doc-text-area app)]
    (add-text-change-listener text-comp
      #(when-not @changing-file
         ((app :update-caret-position) text-comp)
         (update-temp app)))))

(defn restart-doc [app ^File file]
  (send-off temp-file-manager
            (let [f @(:current-file app)
                  txt (get-text-str (:doc-text-area app))]
              (let [temp-file (get-temp-file f)]
                (fn [_] (when (and f temp-file (.exists temp-file))
                          (dump-temp-doc app f txt))
                  0))))
  (await temp-file-manager)
  (let [frame (app :frame)
        text-area (app :doc-text-area)
        temp-file (get-temp-file file)
        file-to-open (if (and temp-file (.exists temp-file)) temp-file file)
        ; doc-label (app :doc-label)
        ]
    ;(remove-text-change-listeners text-area)
    (reset! changing-file true)
    ((app :save-caret-position) app)
    (.. text-area getHighlighter removeAllHighlights)
    (if (and file-to-open (.exists file-to-open) (.isFile file-to-open))
      (do (let [txt (slurp file-to-open)
                rdr (StringReader. txt)]
            (.read text-area rdr nil))
          ; (.setText doc-label (str "Source Editor \u2014 " (.getName file)))
          (config! text-area :editable?  true)
          (if (.endsWith (.getName file-to-open) ".clj")
            (config! text-area :syntax  :clojure)
            (config! text-area :syntax  :none)))
      (do (.setText text-area no-project-txt)
          ; (.setText doc-label (str "Source Editor (No file selected)"))
          (.setEditable text-area false)))
    ((app :update-caret-position) text-area)
    ((app :setup-autoindent) text-area)
    (reset! (app :current-file) file)
    ((app :switch-repl) app (first ((app :get-selected-projects) app)))))

