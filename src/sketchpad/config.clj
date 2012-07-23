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

(defn whitespace-visible
"Returns whether whitespace (spaces and tabs) is visible."
[buffer pref]
  (.setWhitespaceVisible buffer pref))

(defn clear-whitespace-lines
"Returns whether or not lines containing nothing but whitespace are made into blank lines when Enter is pressed in them."
[buffer pref]
  (.setClearWhitespaceEnabled buffer pref))

(defn line-wrap
"Sets whether or not word wrap is enabled. This is overridden so that the \"current line highlight\" gets updated if it needs to be."
[buffer pref]
  (.setLineWrap buffer pref))

(defn highlight-current-line
"Sets whether or not the current line is highlighted. This method fires a property change of type RTextAreaBase.HIGHLIGHT_CURRENT_LINE_PROPERTY."
[buffer pref]
  (.setHighlightCurrentLine (cast RTextArea buffer) pref))

(defn rounded-selection-edges
"Sets whether the edges of selections are rounded in this text area. This method fires a property change of type RTextAreaBase.ROUNDED_SELECTION_PROPERTY."
[buffer pref]
  (.setRoundedSelectionEdges buffer pref))

(defn animate-bracket-matching
"Sets whether bracket matching should be animated. This fires a property change event of type ANIMATE_BRACKET_MATCHING_PROPERTY."
[buffer pref]
  (.setAnimateBracketMatching buffer pref))

(defn anti-aliasing
"Sets whether anti-aliasing is enabled in this editor. This method fires a property change event of type ANTIALIAS_PROPERTY."
[buffer pref]
  (.setAntiAliasingEnabled buffer pref))

(defn code-folding
"Returns whether code folding is enabled. Note that only cebufferin languages support code folding; those that do not will ignore this property."
[buffer pref]
  (.setCodeFoldingEnabled buffer pref))

(defn auto-indent
"Sets whether or not auto-indent is enabled. This fires a property change event of type AUTO_INDENT_PROPERTY."
[buffer pref]
  (.setAutoIndentEnabled buffer pref))

(defn bracket-matching-enabled
"Sets whether bracket matching is enabled. This fires a property change event of type BRACKET_MATCHING_PROPERTY."
[buffer pref]
  (.setBracketMatchingEnabled buffer pref))

(defn clear-white-space-lines
"Sets whether or not lines containing nothing but whitespace are made into blank lines when Enter is pressed in them. This method fires a property change event of type CLEAR_WHITESPACE_LINES_PROPERTY."
[buffer pref]
  (.setClearWhitespaceLinesEnabled buffer pref))

(defn eol-marker
"Sets whether EOL markers are visible at the end of each line. This method fires a property change of type EOL_VISIBLE_PROPERTY."
[buffer pref]
  (.setEOLMarkersVisible buffer pref))

(defn font
"Sets the font used by this text area. Note that this method does not alter the appearance of an RSyntaxTextArea since it uses different fonts for each token type."
[buffer pref]
  (.setFont buffer (font/font pref)))

(defn tab-size
""
[buffer pref]
  (.setTabSize buffer pref))

(defn hyper-links-enabled
"Sets whether hyperlinks are enabled for this text area. This method fires a property change event of type HYPERLINKS_ENABLED_PROPERTY."
[buffer pref]
  (.setHyperlinksEnabled buffer pref))

(defn mark-occurences
"Toggles whether \"mark occurrences\" is enabled. This method fires a property change event of type MARK_OCCURRENCES_PROPERTY."
[buffer pref]
  (.setMarkOccurrences buffer pref))

(defn mark-occurences-color
"Sets the \"mark occurrences\" color."
[buffer pref]
  (.setMarkOccurrencesColor buffer (c/color pref)))

(defn paint-mark-occurences-border
"Toggles whether a border should be painted around marked occurrences."
[buffer pref]
  (.setPaintMarkOccurrencesBorder buffer pref))

(defn matched-bracket-bg-color
"Sets the color used as the background for a matched bracket."
[buffer pref]
  (.setMatchedBracketBGColor buffer (apply c/color pref)))

(defn matched-bracket-border-color
"Sets the color used as the border for a matched bracket."
[buffer pref]
  (.setMatchedBracketBorderColor buffer (apply c/color pref)))

(defn tab-lines-enabled
"Toggles whether tab lines are painted. This method fires a property change event of type TAB_LINES_PROPERTY."
[buffer pref]
  (.setPaintTabLines buffer pref))

(defn tab-lines-color
"Sets the color use to paint tab lines. This method fires a property change event of type TAB_LINE_COLOR_PROPERTY."
[buffer pref]
  (.setTabLineColor buffer (apply c/color pref)))

(defn templates-enabled "Enables or disables templates. Templates are a set of \"shorthand
identifiers\" that you can configure so that you only have to type a short identifier (such as
\"forb\") to insert a larger amount of code into the document (such as:

   for (<caret>) {

   }
 
Templates are a shared resource among all instances of RSyntaxTextArea; that is, templates can only be enabled/disabled for all text areas globally, not individually, and all text areas have access of the same templates. This should not be an issue; rather, it should be beneficial as it promotes uniformity among all text areas in an application."
[buffer pref]
  (org.fife.ui.rsyntaxtextarea.RSyntaxTextArea/setTemplatesEnabled pref))

(defn close-curly-braces
"Toggles whether curly braces should be automatically closed when a newline is entered after an opening curly brace. Note that this property is only honored for languages that use curly braces to denote code blocks.
This method fires a property change event of type CLOSE_CURLY_BRACES_PROPERTY."
[buffer pref]
  (.setCloseCurlyBraces buffer pref))

(defn buffer-theme
"Set the current RSyntaxTextArea theme."
[buffer pref]
  (try
     (theme/apply! (theme/theme pref) buffer)
     (catch Exception e
       (println (str "The theme(defn  " (str pref) " does can not be found...")))))

(defn background-img
"Sets this image as the background image. This method fires a property change event of type RTextAreaBase.BACKGROUND_IMAGE_PROPERTY.
NOTE: the opaque property is set to true when the background is set to a color. When an image is used for the background (by this method), opaque is set to false. This is because we perform better when setOpaque is true, but if we use an image for the background when opaque is true, we get on-screen garbage when the user scrolls via the arrow keys. Thus we need setOpaque to be false in that case.

You never have to change the opaque property yourself; it is always done for you."
[buffer pref]
  (.setBackgroundImage buffer pref))

(def editor-pref-handlers ^{:doc "default handlers for config settings found in config/default.clj"}
  {:whitespace-visible whitespace-visible 			           
   :clear-whitespace-lines clear-whitespace-lines 		         
   :line-wrap line-wrap 					               
   :highlight-current-line highlight-current-line 		         
   :rounded-selection-edges rounded-selection-edges 		       
   :background-img background-img 				           
   :bracket-matching-enabled bracket-matching-enabled
   :animate-bracket-matching animate-bracket-matching 	         
   :anti-aliasing anti-aliasing 				             
   :code-folding code-folding 				               
   :auto-indent auto-indent 				               
   :clear-white-space-lines clear-white-space-lines            
   :eol-marker eol-marker 				                 
   :font font                               
   :tab-size tab-size 					                 
   :hyper-links-enabled hyper-links-enabled			           
   :mark-occurences mark-occurences 			             
   :mark-occurences-color mark-occurences-color 			       
   :paint-mark-occurences-border paint-mark-occurences-border       
   :matched-bracket-bg-color matched-bracket-bg-color 	         
   :matched-bracket-border-color matched-bracket-border-color 	     
   :tab-lines-enabled tab-lines-enabled 				         
   :tab-lines-color tab-lines-color 			             
   :templates-enabled templates-enabled 				         
   :close-curly-braces close-curly-braces 			           
   :buffer-theme buffer-theme})

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

(defn apply-editor-prefs! [prefs buffer]
  (doseq [[k pref] default-editor-prefs]
    ((k editor-pref-handlers) buffer pref)))

(defn apply-auto-completion-prefs! [prefs ac]
  (doseq [[k pref] default-auto-completion-prefs]
    ((k auto-completion-handlers) ac pref)))

