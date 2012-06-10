(ns sketchpad.edit-mode
	(:use [sketchpad.rsyntaxtextarea]
        [clojure.pprint])
  (:import (java.awt Toolkit)
           (javax.swing UIManager ActionMap InputMap)
           (org.fife.ui.rtextarea RecordableTextAction )
           (org.fife.ui.rsyntaxtextarea RSyntaxTextAreaDefaultInputMap)))

;; edit modes [:default :vim]
(def editor-mode (atom :default))

(comment 
  (require :reload 'sketchpad.edit-mode)
  )

(defn- get-default-modifier
  []
  (.getMenuShortcutKeyMask (Toolkit/getDefaultToolkit)))

(defonce default-modifier (get-default-modifier))

(defn edit-mode!
  [kw rta input-map]
  ; (pprint (.. rta getInputMap allKeys))
  (let [default-input-map-name "RTextAreaUI.inputMap"
        default-action-map-name "RTextAreaUI.actionMap"
        action-map (cast ActionMap (UIManager/get default-action-map-name))
        rta-input-map (cast InputMap (UIManager/get default-input-map-name))]
    (cond 
      (= kw :default)
        (do 
    			(swap! editor-mode (fn [_] :default))
        	(set-input-map! rta input-map)
        	(println "edit mode to default"))
      (= kw :vim)
      	(do
      		(swap! editor-mode (fn [_] :vim))
      		(set-input-map! rta input-map)
          (println "edit mode to vim"))
      :else
        (do
    			(swap! editor-mode (fn [_] :default))
        	(set-input-map! rta input-map)
        	(println (name kw) "is not a valid edit mode. Setting edit mode to default")))))

