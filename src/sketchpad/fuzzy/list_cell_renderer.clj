(ns sketchpad.fuzzy.list-cell-renderer
	(:import (javax.swing.DefaultListCellRenderer)
			(javax.swing.JPanel))
	(:use [seesaw.core]))










(defn renderer
"Returns a custom cell renderer for the fuzzy search auto complete."
	[]
	(let [main-label (label :text "" :class :main-fuzzy-cell-label)
		 sub-label (label :text "" :class :sub-fuzzy-cell-label)
		 p (vertical-panel :items [sub-label main-label])
		 r (proxy [javax.swing.DefaultListCellRenderer] []
			(getListCellRendererComponent [list value index selected? focus?]
				(config! main-label :text (str value))
				(if selected?
					(config! p :background (seesaw.color/color :pink))
					(config! p :background (seesaw.color/color :white)))
				p))]))