(ns sketchpad.editor-info
	(:use [seesaw core border color graphics]
			)
	(:import (org.fife.ui.rtextarea.RTextAreaBase)))

; (defn info-panel-bg []
; 	(seesaw.graphics/linear-gradient 
; 		:colors [(seesaw.color/color 75 75 75) (seesaw.color/color 90 90 90)] 
; 		:fractions [0 0.9]
; 		:start [0 12]
; 		:end [0 0]))

; (defn update-position-label! [rsta app-atom e]
; 	(let [lbl (select (@app-atom :editor-info) [:#editor-info-label])
; 				rsta-doc (.getDocument rsta)
; 				root (.getDefaultRootElement rsta-doc)
; 				caret (.getCaret rsta)
; 				dot (.getCaretPosition rsta)
; 				line (.getElementIndex root dot)
; 				elem (.getElement root line)
; 				start (.getStartOffset elem)
; 				col (- dot start)]
; 		(config! lbl :text (str "Line " line ", Column " col))))


; 	(defn paint-info-panel [c g]
; 		(draw g
; 			(rect 0 0 (width c) (height c))
; 			(style :foreground (color 20 20 20) :background (info-panel-bg))))

; (defn attach-caret-handler [rsta lbl]
; 	(listen rsta :caret-update (partial update-position-label! rsta lbl)))

; (defn editor-info [app-atom]
; 	(let [app @app-atom
; 				info-label (label :text "Line 0, Column 0"
; 													:border nil
; 													:foreground (color :white)
; 													:maximum-size [3000 :by 10]
; 													:minimum-size [30 :by 10]
; 													:id :editor-info-label)
; 			editor-info (toolbar 	:floatable? false
; 									:orientation :horizontal
; 									; :background config/app-color
; 										:border nil
; 										; :size [1000 :by 20]
; 										:id :editor-info
; 										; :hgap 10
; 										; :vgap 0
; 										; :paint paint-info-panel
; 										:items [info-label])]
; 		; (config! editor-info :background (info-panel-bg editor-info))
; 		(swap! app-atom (fn [a] (assoc a :editor-info editor-info)))
; 		editor-info))
