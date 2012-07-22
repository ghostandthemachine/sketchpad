(ns sketchpad.config
  (:use [clojure.pprint]
        [clojure.java.io])
  (:require [seesaw.color :as c]
            [seesaw.keystroke :as key]
            [seesaw.border :as border]
            [seesaw.font :as font]
            [seesaw.core :as seesaw]
            [sketchpad.theme :as theme])
  (:import [org.fife.ui.rtextarea RTextArea]
           [java.io FileNotFoundException]
           [java.awt.Image]
           [java.awt.image.BufferedImage]
           [javax.imageio.ImageIO]))

(load-file "config/default.clj")

(def app-color (c/color 39 40 34))
(def file-tree-bg (c/color 230 230 230))
(def file-tree-fg (c/color 130 130 130))
(def doc-title-color (c/color 165 165 164))

(def editor-pref-handlers ^{:doc "default handlers for config settings found in config/default.clj"}
  {:whitespace-visible 			           (fn [rta pref] (.setWhitespaceVisible rta pref))
   :clear-whitespace-lines 		         (fn [rta pref] (.setClearWhitespaceEnabled rta pref))
   :line-wrap 					               (fn [rta pref] (.setLineWrap rta pref))
   :highlight-current-line 		         (fn [rta pref] (.setHighlightCurrentLine (cast RTextArea rta) pref))
   :rounded-selection-edges 		       (fn [rta pref] (.setRoundedSelectionEdges rta pref))
   ; :background-img 				           (fn [rta pref] (.setBackgroundImage (cast RTextArea rta) (javax.imageio.ImageIO/read (file (str pref)))))
   :animate-bracket-matching 	         (fn [rta pref] (.setAnimateBracketMatching rta pref))
   :anti-aliasing 				             (fn [rta pref] (.setAntiAliasingEnabled rta pref))
   :code-folding 				               (fn [rta pref] (.setCodeFoldingEnabled rta pref))
   :auto-indent 				               (fn [rta pref] (.setAutoIndentEnabled rta pref))
   :clear-white-space-lines            (fn [rta pref] (.setClearWhitespaceLinesEnabled rta pref))
   :eol-marker 				                 (fn [rta pref] (.setEOLMarkersVisible rta pref))
   :font                               (fn [rta pref] (.setFont rta (font/font pref)))
   :tab-size 					                 (fn [rta pref] (.setTabSize rta pref))
   :hyper-links-enabled			           (fn [rta pref] (.setHyperlinksEnabled rta pref))
   :mark-occurences 			             (fn [rta pref] (.setMarkOccurrences rta pref))
   :mark-occurences-color 			       (fn [rta pref] (.setMarkOccurrencesColor rta (c/color pref)))
   :paint-mark-occurences-border       (fn [rta pref] (.setPaintMarkOccurrencesBorder rta pref))
   :matched-bracket-bg-color 	         (fn [rta pref] (.setMatchedBracketBGColor rta (apply c/color pref)))
   :matched-bracket-border-color 	     (fn [rta pref] (.setMatchedBracketBorderColor rta (apply c/color pref)))
   :tab-lines-enabled 				         (fn [rta pref] (.setPaintTabLines rta pref))
   :tab-lines-color 			             (fn [rta pref] (.setTabLineColor rta (apply c/color pref)))
   :templates-enabled 				         (fn [rta pref] (org.fife.ui.rsyntaxtextarea.RSyntaxTextArea/setTemplatesEnabled pref))
   :close-curly-braces 			           (fn [rta pref] (.setCloseCurlyBraces rta pref))
   :rsta-theme 					               (fn [rta pref] 
                                        (try
                                         (theme/apply! (theme/theme pref) rta)
                                         (catch java.io.FileNotFoundException e
                                           (println (str "The theme: " (str pref) " does can not be found...")))))})

(def buffer-scroller-pref-handlers
  {:fold-indicator-enabled             (fn [scroller pref] (.setFoldIndicatorEnabled scroller pref))
   :line-numbers-enabled               (fn [scroller pref] (.setLineNumbersEnabled scroller pref))
   :scroller-border-enabled            (fn [scroller pref] (if (not pref) (.setBorder scroller (border/empty-border :thickness 0))))})

(def gutter-pref-handlers
  {:border-color                       (fn [gutter pref] (.setBorderColor gutter (apply c/color pref)))
   :bookmarking-enabled                (fn [gutter pref] (.setBookmarkingEnabled gutter pref))
   :fold-indicator-enabled             (fn [gutter pref] (.setFoldIndicatorEnabled gutter pref))
   :line-number-color                  (fn [gutter pref] (.setLineNumberColor gutter (apply c/color pref)))
   :line-number-font                   (fn [gutter pref] (.setLineNumberFont gutter (font/font pref)))
   :line-number-start-index            (fn [gutter pref] (.setLineNumberingStartIndex gutter pref))
   :active-range-color                 (fn [gutter pref] (.setActiveLineRangeColor gutter (apply c/color pref)))})

(def auto-completion-handlers
  {:auto-complete                      (fn [ac pref] (.setAutoCompleteEnabled ac pref))
   :auto-activation                    (fn [ac pref] (.setAutoActivationEnabled ac pref))
   :auto-activation-delay              (fn [ac pref] (.setAutoActivationDelay ac pref))
   :auto-complete-single-choice        (fn [ac pref] (.setAutoCompleteSingleChoices ac pref))
   :show-description-window            (fn [ac pref] (.setShowDescWindow ac pref))
   :description-window-size            (fn [ac pref] (.setDescriptionWindowSize ac (first pref) (second pref)))
   :choices-window-size                (fn [ac pref] (.setChoicesWindowSize ac (first pref) (second pref)))
   :parameter-assistance               (fn [ac pref] (.setParameterAssistanceEnabled ac pref))
   :trigger-key                        (fn [ac pref] (.setTriggerKey ac (key/keystroke pref)))})

(defn apply-buffer-scroller-prefs! [prefs scroller]
  (doseq [[k pref] default-buffer-scroller-prefs]
    ((k buffer-scroller-pref-handlers) scroller pref)))

(defn apply-gutter-prefs! [prefs gutter]
  (doseq [[k pref] default-gutter-prefs]
    ((k gutter-pref-handlers) gutter pref)))

(defn apply-editor-prefs! [prefs rta]
  (doseq [[k pref] default-editor-prefs]
    ((k editor-pref-handlers) rta pref)))

(defn apply-auto-completion-prefs! [prefs ac]
  (doseq [[k pref] default-auto-completion-prefs]
    ((k auto-completion-handlers) ac pref)))

