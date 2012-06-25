(ns sketchpad.preview-manager
	(:use [sketchpad.tab-manager]
				[seesaw.core])
	(:require [seesaw.rsyntax :as rsyntax]
            [sketchpad.rtextscrollpane :as sp]
            [sketchpad.rsyntaxtextarea :as rs]
            [sketchpad.file-manager :as fm]
            [sketchpad.tab-manager :as tab]
            [sketchpad.editor-component :as ec]
            [clojure.java.io :as io])
	(:import (java.io StringReader)))

(defn preview-component []
(let [preview-rsta  (rsyntax/text-area :id    :preview-rsta	;; no syntax style is set at this point
           										  		   :class [:editor-comp :syntax-editor])
      preview-scroll-panel (sp/scroll-pane preview-rsta)]
  preview-scroll-panel))

(defn preview []
 (atom {:visible-state  false
				:state 					nil
				:preview-comp	  nil
				:preview-file-name nil
				:hidden-comp 		nil
				:hidden-tab-index nil}))

(defn make-preview [app-atom]
	(let [app @app-atom
				new-preview (preview)]
		;; create the preview component
		(swap! new-preview (fn [preview] (assoc preview :preview-comp (preview-component))))
		;; and add it to the app map
		(swap! app-atom (fn [app] (assoc app :preview new-preview)))))

(defn load-file-into-rta! [file rta]
	)

(defn preview-file-name! [app name]
	(let [preview (app :preview)]
		(swap! preview (fn [p] (assoc :preview-file-name name p)))))

;; update preview clean/dirt state
(defn set-preview-state [kw preview]
	(swap! preview (fn [p] (assoc p :state kw))))

(defn dirty! [preview]
	(set-preview-state :dirty preview))

(defn clean! [preview]
	(set-preview-state :clean preview))

(defn visible-state! [preview kw]
	(swap! preview (fn [p] (assoc p :visible-state kw))))

(defn clear-preview! [app]
	(let [preview (app :preview)
				preview-comp (preview :preview-comp)
				tab (current-tab app)]
		(remove! preview-comp tab)))

(defn set-hidden-comp [preview rsta-scroller]
	(swap! preview (fn [p] (assoc p :hideen-comp rsta-scroller))))

(defn set-global-rsta-to-editor! [app-atom]
	(let [app @app-atom
				preview (app :preview)
				original-comp (@preview :hidden-comp)]
		(swap! app-atom (fn [a] (assoc a :doc-text-area original-comp)))))


(defn remove-editor-from-container [container preview]
	;; seesaw select this tabs rsta
	(let [rsta-scroller (select container [:.rsta-scroller])]
		;; update the preview maps currently hidden component to this rsta scroller
		(swap! preview (fn [p] (assoc p :hidden-comp rsta-scroller)))
		;; remove the scroller from the tab
		(remove! container rsta-scroller)))

(defn set-blocked-tab-index! [preview index]
	(swap! preview (fn [p] (assoc p :hidden-tab-index index))))

(defn set-global-rsta-to-preview! [app-atom]
	(let [app @app-atom
				preview @(app :preview)
				preview-scroller (preview :preview-comp)]
		(swap! app-atom (fn [a] (assoc a :doc-text-area (select preview-scroller [:#preview-rsta]))))))

(defn add-preview-comp! [current-tab preview-comp]
	; (let [container (select current-tab [:.container])]
		(add! current-tab preview-comp))


(defn show-preview! 
	([app-atom file] (show-preview! app-atom file nil))
	([app-atom file container]
	(let [app @app-atom
				preview (app :preview)
				preview-comp (@preview :preview-comp)
				tabbed-panel (app :editor-tabbed-panel)
				current-tab-index (current-tab-index app)]
		(let [current-tab (current-tab app)
					tab-container (if (nil? container)
													(select current-tab [:.container])
													container)]
			; (remove-editor-from-container tab-container preview)
			(set-blocked-tab-index! preview current-tab-index)
			;; set global focussed rta to the preview
			(set-global-rsta-to-preview! app-atom)
			;; show the preview component
			(add-preview-comp! current-tab preview-comp)

			;; set the text area text from file
			(let [txt (slurp file)
            rdr (StringReader. txt)
            preview-rta (first (select preview-comp [:.syntax-editor]))]
           	(.read preview-rta rdr nil))
			
			;; init in a clean state
			(clean! preview)
			;; set visible-state
			(visible-state! preview :true)))))

(defn reset-original-comp! [app-atom]
	(let [app @app-atom
				preview (app :preview)
				tabbed-panel (app :editor-tabbed-panel)
				original-comp (@preview :hidden-comp)]
		(set-global-rsta-to-editor! app-atom)
		(add! (select (current-tab app) [:.container]) original-comp)))

(defn hide-preview! [app-atom]
	(let [app @app-atom
				preview (app :preview)
				preview-comp (@preview :preview-comp)
				tabbed-panel (app :editor-tabbed-panel)]
		;; remove preview from current container
		(clear-preview! preview)
		;; mark it clean
		(clean! preview)
		;; put original component back in this tabs container
		(reset-original-comp! app-atom)
		(visible-state! preview false)))

(defn load-preview-content! [app]
	(let [preview (app :preview)
				preview-comp (@preview :preview-rta)
				tabbed-panel (app :editor-tabbed-panel)
				current-tab-index (current-tab-index app)]
))

(defn preview-file! [app-atom file]
  (let [file-name (fm/file-name file)]
  	(if (tabs? (@app-atom :editor-tabbed-panel))
    	;; if there are tabs we can take the focused one over and display there
    	(show-preview! app-atom file)
    	;; other wise we need to create a temp container
    	(do 
    		(new-editor-tab! @app-atom (ec/make-editor-component))
    		(show-preview! app-atom file)))))















