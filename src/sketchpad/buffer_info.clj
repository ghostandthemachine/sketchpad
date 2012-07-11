(ns sketchpad.buffer-info)
 
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
 		(buffer-move-pos-by-char rta (neg 1)))