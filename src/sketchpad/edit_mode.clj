(ns sketchpad.edit-mode
  (:use [sketchpad.rsyntaxtextarea]
        [clojure.pprint]
        [sketchpad.default-mode])
  (:import (java.awt Toolkit)
           (javax.swing.text JTextComponent)
           (javax.swing UIManager ActionMap InputMap SwingUtilities JComponent)
           (org.fife.ui.rtextarea RecordableTextAction RTextAreaEditorKit)
           (org.fife.ui.rsyntaxtextarea RSyntaxTextAreaDefaultInputMap)))

(def editor-mode (atom :default))

(defn- get-default-modifier
  []
  (.getMenuShortcutKeyMask (Toolkit/getDefaultToolkit)))

(defonce default-modifier (get-default-modifier))

(def RTEXTAREA_KEYMAP_NAME "RTextAreaKeymap")

(defn create-keymap
  []
  (let [m (atom (JTextComponent/getKeymap RTEXTAREA_KEYMAP_NAME))]
    (let [parent (JTextComponent/getKeymap JTextComponent/DEFAULT_KEYMAP)]
      (swap! m (fn [_] (JTextComponent/addKeymap RTEXTAREA_KEYMAP_NAME parent)))
      )@m))

(defn edit-mode!
  [kw rta input-map]
  (let [default-input-map-name "RTextAreaUI.inputMap"
        default-action-map-name "RTextAreaUI.actionMap"
        action-map (cast ActionMap (UIManager/get default-action-map-name))
        rta-input-map (default-input-map)]
    (cond
      (= kw :default)
      (do
        (swap! editor-mode (fn [_] :default))
        (.setKeymap rta (create-keymap))
        (set-input-map! rta (input-map rta))
        (println "edit mode to default"))
      (= kw :vim)
      (do
        (swap! editor-mode (fn [_] :vim))
        (set-input-map! rta input-map)
        (println "edit mode to vim")))))
