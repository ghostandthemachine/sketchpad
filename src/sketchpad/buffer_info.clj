(ns sketchpad.buffer-info
  (:import (org.fife.ui.rsyntaxtextarea RSyntaxTextAreaEditorKit)
           (org.fife.ui.rtextarea RTextAreaEditorKit)
           (java.awt.event ActionEvent)))

(defn buffer-cursor-point [rta]
  (.getCaretPosition rta))

(defn buffer-cursor-pos [rta]
  (let [rta-doc (.getDocument rta)
        root (.getDefaultRootElement rta-doc)
        caret (.getCaret rta)
        dot (.getCaretPosition rta)
        line (.getElementIndex root dot)
        elem (.getElement root line)
        start (.getStartOffset elem)
        col (- dot start)]
    [col line]))

(defn buffer-cursor-pos! [rsta]
  (let [rsta-doc (.getDocument rsta)
        root (.getDefaultRootElement rsta-doc)
        caret (.getCaret rsta)
        dot (.getCaretPosition rsta)
        line (.getElementIndex root dot)
        elem (.getElement root line)
        start (.getStartOffset elem)
        col (- dot start)]
    (println (str "dot: \t" (.toString dot)))
    (println (str "start: \t" start))
    (println (str "col: \t" col))
    (println (str "line: \t" line))))

(defn buffer-move-pos-by-char [rta offset]
  (.setCaretPosition rta (+ (buffer-cursor-point rta) offset)))

(defn buffer-goto-next-char [rta]
  (buffer-move-pos-by-char rta 1))

(defn buffer-goto-prev-char [rta]
  (buffer-move-pos-by-char rta (- 0 1)))


;DefaultEditorKit/nextWordAction)
;(.put
;(javax.swing.KeyStroke/getKeyStroke java.awt.event.KeyEvent/VK_RIGHT
;(bit-or alt shift))
;DefaultEditorKit/selectionNextWordAction)
