(ns sketchpad.config.config
  (:use [clojure.pprint]
        [clojure.java.io])
  (:require [seesaw.color :as c]
            [seesaw.keystroke :as key]
            [seesaw.border :as border]
            [seesaw.font :as font]
            [seesaw.core :as seesaw]
            [sketchpad.wrapper.theme :as theme]
            [sketchpad.state.state :as state]
            [sketchpad.config.prefs :as sketchpad-prefs])
  (:import [org.fife.ui.rtextarea RTextArea]
           [java.io FileNotFoundException]
           [java.awt.Image]
           [java.awt Dimension]
           [java.awt.image.BufferedImage]
           [javax.imageio ImageIO]
           [java.io File]
           [org.apache.commons.io FileUtils]))

(defn exists? [path]
  (.exists (java.io.File. path)))

(defonce sketchpad-resources-url (str (System/getProperty "user.home") "/.sketchpad"))

(defn add-defaults-if-needed
"If the .sketchpad/ directory does not exist, create it with defaults from resources."
  []
  (when-not (exists? (str sketchpad-resources-url "/default.clj"))
    (let [src-url (clojure.java.io/resource "default.clj")
          dst-dir (java.io.File. sketchpad-resources-url)
          dst-file (java.io.File. (str sketchpad-resources-url "/default.clj"))]
      (.mkdirs dst-dir)
      (spit (str sketchpad-resources-url "/default.clj") (slurp src-url)))))

(defn add-theme-if-needed
  [theme-name]
  (when-not (exists? (str sketchpad-resources-url (str "/" theme-name ".xml")))
    (let [src-url (clojure.java.io/resource (str theme-name ".xml"))
          dst-dir (java.io.File. sketchpad-resources-url)
          dst-file (java.io.File. (str sketchpad-resources-url (str "/" theme-name ".xml")))]
      (spit (str sketchpad-resources-url (str "/" theme-name ".xml")) (slurp src-url)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;  Load the config files
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(do
  (add-defaults-if-needed)
  (add-theme-if-needed "sublime")
  (add-theme-if-needed "dark")
  (add-theme-if-needed "default")
  (add-theme-if-needed "vs")
  (add-theme-if-needed "eclipse"))

(load-string (slurp (str sketchpad-resources-url "/default.clj")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;  Config functions
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def app-color (c/color 39 40 34))
(def file-tree-bg (c/color 230 230 230))
(def file-tree-fg (c/color 130 130 130))
(def doc-title-color (c/color 165 165 164))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Text Area 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn whitespace-visible
"Returns whether whitespace (spaces and tabs) is visible."
[text-area pref]
  (seesaw/invoke-later (.setWhitespaceVisible text-area pref)))

(defn clear-whitespace-lines
"Returns whether or not lines containing nothing but whitespace are made into blank lines when Enter is pressed in them."
[text-area pref]
  (seesaw/invoke-later (.setClearWhitespaceEnabled text-area pref)))

(defn line-wrap
"Sets whether or not word wrap is enabled. This is overridden so that the \"current line highlight\" gets updated if it needs to be."
[text-area pref]
  (seesaw/invoke-later (.setLineWrap text-area pref)))

(defn highlight-current-line
"Sets whether or not the current line is highlighted. This method fires a property change of type RTextAreaBase.HIGHLIGHT_CURRENT_LINE_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setHighlightCurrentLine (cast RTextArea text-area) pref)))

(defn rounded-selection-edges
"Sets whether the edges of selections are rounded in this text area. This method fires a property change of type RTextAreaBase.ROUNDED_SELECTION_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setRoundedSelectionEdges text-area pref)))

(defn animate-bracket-matching
"Sets whether bracket matching should be animated. This fires a property change event of type ANIMATE_BRACKET_MATCHING_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setAnimateBracketMatching text-area pref)))

(defn anti-aliasing
"Sets whether anti-aliasing is enabled in this editor. This method fires a property change event of type ANTIALIAS_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setAntiAliasingEnabled text-area pref)))

(defn code-folding
"Returns whether code folding is enabled. Note that only cetext-areain languages support code folding; those that do not will ignore this property."
[text-area pref]
  (seesaw/invoke-later (.setCodeFoldingEnabled text-area pref)))

(defn auto-indent
"Sets whether or not auto-indent is enabled. This fires a property change event of type AUTO_INDENT_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setAutoIndentEnabled text-area pref)))

(defn bracket-matching-enabled
"Sets whether bracket matching is enabled. This fires a property change event of type BRACKET_MATCHING_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setBracketMatchingEnabled text-area pref)))

(defn clear-white-space-lines
"Sets whether or not lines containing nothing but whitespace are made into blank lines when Enter is pressed in them. This method fires a property change event of type CLEAR_WHITESPACE_LINES_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setClearWhitespaceLinesEnabled text-area pref)))

(defn eol-marker
"Sets whether EOL markers are visible at the end of each line. This method fires a property change of type EOL_VISIBLE_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setEOLMarkersVisible text-area pref)))

(defn font
"Sets the font used by this text area. Note that this method does not alter the appearance of an RSyntaxTextArea since it uses different fonts for each token type."
[text-area pref]
  (seesaw/invoke-later (.setFont text-area (font/font pref))))

(defn tab-size
"Set num spaces per tab."
[text-area pref]
  (seesaw/invoke-later (.setTabSize text-area pref)))

(defn hyperlinks-enabled
"Sets whether hyperlinks are enabled for this text area. This method fires a property change event of type HYPERLINKS_ENABLED_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setHyperlinksEnabled text-area pref)))

(defn mark-occurences
"Toggles whether \"mark occurrences\" is enabled. This method fires a property change event of type MARK_OCCURRENCES_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setMarkOccurrences text-area pref)))

(defn mark-occurences-color
"Sets the \"mark occurrences\" color."
[text-area pref]
  (seesaw/invoke-later (.setMarkOccurrencesColor text-area (c/color pref))))

(defn paint-mark-occurences-border
"Toggles whether a border should be painted around marked occurrences."
[text-area pref]
  (seesaw/invoke-later (.setPaintMarkOccurrencesBorder text-area pref)))

(defn matched-bracket-bg-color
"Sets the color used as the background for a matched bracket."
[text-area pref]
  (seesaw/invoke-later (.setMatchedBracketBGColor text-area (apply c/color pref))))

(defn matched-bracket-border-color
"Sets the color used as the border for a matched bracket."
[text-area pref]
  (seesaw/invoke-later (.setMatchedBracketBorderColor text-area (apply c/color pref))))

(defn tab-lines-enabled
"Toggles whether tab lines are painted. This method fires a property change event of type TAB_LINES_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setPaintTabLines text-area pref)))

(defn tab-lines-color
"Sets the color use to paint tab lines. This method fires a property change event of type TAB_LINE_COLOR_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setTabLineColor text-area (apply c/color pref))))

(defn templates-enabled
"Enables or disables templates. Templates are a set of \"shorthand
identifiers\" that you can configure so that you only have to type a short identifier (such as
\"forb\") to insert a larger amount of code into the document (such as:

   for (<caret>) {

   }
 
Templates are a shared resource among all instances of RSyntaxTextArea; that is, templates can only be enabled/disabled for all text areas globally, not individually, and all text areas have access of the same templates. This should not be an issue; rather, it should be beneficial as it promotes uniformity among all text areas in an application."
[text-area pref]
  (org.fife.ui.rsyntaxtextarea.RSyntaxTextArea/setTemplatesEnabled pref))

(defn close-curly-braces
"Toggles whether curly braces should be automatically closed when a newline is entered after an opening curly brace. Note that this property is only honored for languages that use curly braces to denote code blocks.
This method fires a property change event of type CLOSE_CURLY_BRACES_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setCloseCurlyBraces text-area pref)))

(defn text-area-theme
"Set the current RSyntaxTextArea theme."
[text-area pref]
(try
   (theme/apply! (theme/theme (str sketchpad-resources-url "/" pref)) text-area)
   (catch Exception e
     (println (str "The theme " pref " can not be found...")))))

(defn background-img
"Sets this image as the background image. This method fires a property change event of type RTextAreaBase.BACKGROUND_IMAGE_PROPERTY.
NOTE: the opaque property is set to true when the background is set to a color. When an image is used for the background (by this method), opaque is set to false. This is because we perform better when setOpaque is true, but if we use an image for the background when opaque is true, we get on-screen garbage when the user scrolls via the arrow keys. Thus we need setOpaque to be false in that case.
You never have to change the opaque property yourself; it is always done for you."
[text-area pref]
  (seesaw/invoke-later (.setBackgroundImage text-area (ImageIO/read (File. pref)))))

(defn current-line-highlight
"Sets whether or not the current line is highlighted. This method fires a property change of type RTextAreaBase.HIGHLIGHT_CURRENT_LINE_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setHighlightCurrentLine text-area pref)))

(defn current-line-highlight-color
"Sets the color to use to highlight the current line. Note that if highlighting the current line is turned off, you will not be able to see this highlight. This method fires a property change of type RTextAreaBase.CURRENT_LINE_HIGHLIGHT_COLOR_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setCurrentLineHighlightColor text-area (apply c/color pref))))

(defn current-line-highlight-fade
"Sets whether the current line highlight should have a \"fade\" effect. This method fires a property change event of type CURRENT_LINE_HIGHLIGHT_FADE_PROPERTY."
[text-area pref]
  (seesaw/invoke-later (.setFadeCurrentLineHighlight text-area pref)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SketchPad
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn show-tabs!
([]
"Show the current buffer tabs."
  (show-tabs! true))
([bool]
"Show or hide the buffer tabs."
  `(seesaw/config! (seesaw/select (:buffer-tabbed-panel @state/app) [:.button-tab]) :visible? ~bool)
  (do (swap! sketchpad.config.prefs/show-tabs? (fn [_] bool)))))

(defn hide-tabs! []
"Hide the current buffer tabs."
  (show-tabs! false))

(def sketchpad-pref-handlers
  {:show-tabs? show-tabs!})

(defn vertical-scroll-bar 
"Set whether or not to display the vertical scroll bar component."
[scroll-pane bool]
  (when-not bool
    (seesaw/invoke-later (.setPreferredSize
      (.getVerticalScrollBar scroll-pane) (Dimension. 0 0)))))

(defn horizontal-scroll-bar 
"Set whether or not to display the horizontal scroll bar component."
[scroll-pane bool]
  (when-not bool
    (seesaw/invoke-later (.setPreferredSize
      (.getHorizontalScrollBar scroll-pane) (Dimension. 0 0)))))

(defn fold-indicator-enabled
"Enable and disable the fold indicators."
  [scroller pref] (seesaw/invoke-later (.setFoldIndicatorEnabled scroller pref)))

(defn line-numbers-enabled
"Enable and disable line numbers."
  [scroller pref] (seesaw/invoke-later (.setLineNumbersEnabled scroller pref)))

(defn scroller-border-enabled
"Enable and disable scroller border."
  [scroller pref] (if (not pref) (seesaw/invoke-later (.setBorder scroller (border/empty-border :thickness 0)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; File Tree
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn tree-vertical-scroll-bar 
"Set whether or not to display the vertical scroll bar component."
[scroll-pane bool]
  (when-not bool
    (seesaw/invoke-later (.setPreferredSize
      (.getVerticalScrollBar scroll-pane) (Dimension. 0 0)))))

(defn tree-horizontal-scroll-bar 
"Set whether or not to display the horizontal scroll bar component."
[scroll-pane bool]
  (when-not bool
    (seesaw/invoke-later (.setPreferredSize
      (.getHorizontalScrollBar scroll-pane) (Dimension. 0 0)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Gutter
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn border-color
"Set Gutter border color."
  [gutter pref]
  (seesaw/invoke-later (.setBorderColor gutter (apply c/color pref))))

(defn bookmarking-enabled
"Enabled book marking."
  [gutter pref]
  (seesaw/invoke-later (.setBookmarkingEnabled gutter pref)))

(defn fold-indicator-enabled
"Enabled code folding indicators."
  [gutter pref]
  (seesaw/invoke-later (.setFoldIndicatorEnabled gutter pref)))

(defn line-number-color
"Set line number color"
  [gutter pref]
  (seesaw/invoke-later (.setLineNumberColor gutter (apply c/color pref))))

(defn line-number-font
"Set the line number font."
  [gutter pref]
  (seesaw/invoke-later (.setLineNumberFont gutter (font/font pref))))

(defn line-number-start-index
"Set starting number of line numbering"
  [gutter pref]
  (seesaw/invoke-later (.setLineNumberingStartIndex gutter pref)))
(defn active-range-color
"Set the active range color."
  [gutter pref]
  (seesaw/invoke-later (.setActiveLineRangeColor gutter (apply c/color pref))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Auto Completion
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn auto-complete
"Enable Auto Completion."
  [ac pref]
  (seesaw/invoke-later (.setAutoCompleteEnabled ac pref)))

(defn auto-activation
"Enable Auto Completion auto activation."
  [ac pref]
  (seesaw/invoke-later (.setAutoActivationEnabled ac pref)))

(defn auto-activation-delay
"Set delay time for auto activation."
  [ac pref]
  (seesaw/invoke-later (.setAutoActivationDelay ac pref)))

(defn auto-complete-single-choice
"Set Auto Completion to have a single choice."
  [ac pref]
  (seesaw/invoke-later (.setAutoCompleteSingleChoices ac pref)))

(defn show-description-window
"Enabled displaying Auto Completion description window."
  [ac pref]
  (seesaw/invoke-later (.setShowDescWindow ac pref)))

(defn description-window-size
"Set description window size."
  [ac pref]
  (seesaw/invoke-later (.setDescriptionWindowSize ac (first pref) (second pref))))

(defn choices-window-size
"Set choices window size."
  [ac pref]
  (seesaw/invoke-later (.setChoicesWindowSize ac (first pref) (second pref))))

(defn parameter-assistance
"Enable parameter assistance."
  [ac pref]
  (seesaw/invoke-later (.setParameterAssistanceEnabled ac pref)))

(defn trigger-key
"Set Auto Completion trigger key."
  [ac pref]
  (seesaw/invoke-later (.setTriggerKey ac (key/keystroke pref))))


(defn spell-checker-underline-color
"Set the color of the spell checker squiggly line."
	[sc pref]
	(seesaw/invoke-later (.setSquiggleUnderlineColor sc (c/color pref))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Handler Maps
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def editor-pref-handlers
^{:doc "default handlers for config settings found in config/default.clj"}
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
   :hyperlinks-enabled hyperlinks-enabled                
   :mark-occurences mark-occurences                    
   :mark-occurences-color mark-occurences-color              
   :paint-mark-occurences-border paint-mark-occurences-border       
   :matched-bracket-bg-color matched-bracket-bg-color            
   :matched-bracket-border-color matched-bracket-border-color        
   :tab-lines-enabled tab-lines-enabled                  
   :tab-lines-color tab-lines-color                    
   :templates-enabled templates-enabled                  
   :close-curly-braces close-curly-braces                  
   :buffer-theme text-area-theme
   :current-line-highlight current-line-highlight
   :current-line-highlight-fade current-line-highlight-fade
   :current-line-highlight-color current-line-highlight-color})

(def buffer-scroller-pref-handlers
  {:vertical-scroll-bar vertical-scroll-bar
   :horizontal-scroll-bar horizontal-scroll-bar
   :fold-indicator-enabled fold-indicator-enabled 
   :line-numbers-enabled  line-numbers-enabled
   :scroller-border-enabled scroller-border-enabled})

(def repl-pref-handlers
^{:doc "default handlers for config settings found in config/default.clj"}
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
   :hyperlinks-enabled hyperlinks-enabled                
   :mark-occurences mark-occurences                    
   :mark-occurences-color mark-occurences-color              
   :paint-mark-occurences-border paint-mark-occurences-border       
   :matched-bracket-bg-color matched-bracket-bg-color            
   :matched-bracket-border-color matched-bracket-border-color        
   :tab-lines-enabled tab-lines-enabled                  
   :tab-lines-color tab-lines-color                    
   :templates-enabled templates-enabled                  
   :close-curly-braces close-curly-braces                  
   :buffer-theme text-area-theme
   :current-line-highlight current-line-highlight
   :current-line-highlight-fade current-line-highlight-fade
   :current-line-highlight-color current-line-highlight-color})

(def repl-scroller-pref-handlers
  {:vertical-scroll-bar vertical-scroll-bar
   :horizontal-scroll-bar horizontal-scroll-bar
   :scroller-border-enabled scroller-border-enabled})

(def repl-response-timeout (default-repl-prefs :response-timeout))

(def gutter-pref-handlers
  {:border-color border-color
   :bookmarking-enabled bookmarking-enabled
   :fold-indicator-enabled fold-indicator-enabled
   :line-number-color line-number-color
   :line-number-font line-number-font
   :line-number-start-index line-number-start-index
   :active-range-color active-range-color})

(def auto-completion-handlers
  {:auto-complete auto-complete
   :auto-activation auto-activation
   :auto-activation-delay auto-activation-delay
   :auto-complete-single-choice auto-complete-single-choice
   :show-description-window show-description-window
   :description-window-size description-window-size
   :choices-window-size choices-window-size
   :parameter-assistance parameter-assistance
   :trigger-key trigger-key})

(def file-tree-scroller-pref-handlers
  {:vertical-scroll-bar tree-vertical-scroll-bar
   :horizontal-scroll-bar tree-horizontal-scroll-bar})

(def spell-checker-pref-handlers
	{:underline-color spell-checker-underline-color})

(def fuzzy-file-type-exclusions ["dll" "class" "Ds_Store" "jpeg" "png" "tiff" "psd" "swf" "so" "scx" "pdf" "svg" "jnilib" "dylib"])
(def fuzzy-file-type-inclusions [ "markdown" "md" "c" "java" "txt" "tex" "clj" "html" "xml" "js" "rb" "py" "sh"])
(def fuzzy-buffer-pref-handlers 
	{:buffer-theme text-area-theme
	 :font font})

(def info-font (seesaw.font/font "MENLO-12"))

(def fuzzy-buffer-settings default-fuzzy-buffer-prefs)
	 
(def file-tree-pref-handlers
  {})

(def project-path project-dir-path)

(def project-theme-colors
  [(c/color :white) (c/color :orange) (c/color :green) (c/color :yellow) (c/color :blue) (c/color :red) (c/color :purple) (c/color :pink)])

(defn apply-buffer-scroller-prefs! [scroller]
  (doseq [[k pref] default-buffer-scroller-prefs]
    ((k buffer-scroller-pref-handlers) scroller pref)))

(defn apply-fuzzy-buffer-prefs! [text-area]
  (doseq [[k pref] default-fuzzy-buffer-prefs]
    ((k fuzzy-buffer-pref-handlers) text-area pref)))

(defn apply-gutter-prefs! [gutter]
  (doseq [[k pref] default-gutter-prefs]
    ((k gutter-pref-handlers) gutter pref)))

(defn apply-editor-prefs! [text-area]
  (doseq [[k pref] default-editor-prefs]
    ((k editor-pref-handlers) text-area pref)))

(defn apply-repl-prefs! [text-area]
  (doseq [[k pref] default-editor-prefs]
    ((k repl-pref-handlers) text-area pref)))

(defn apply-auto-completion-prefs! [ac]
  (doseq [[k pref] default-auto-completion-prefs]
    ((k auto-completion-handlers) ac pref)))

(defn apply-file-tree-prefs! [component]
  (doseq [[k pref] default-file-tree-prefs]
    ((k file-tree-pref-handlers) component pref)))

(defn apply-file-tree-scroller-prefs! [component]
  (doseq [[k pref] default-file-scroller-tree-prefs]
    ((k file-tree-scroller-pref-handlers) component pref)))

(defn apply-sketchpad-prefs! []
  (doseq [[k pref] default-sketchpad-prefs]
    ((k sketchpad-pref-handlers) pref)))

(defn apply-spell-checker-prefs! [spell-checker]
  (doseq [[k pref] default-spell-checker-prefs]
    ((k spell-checker-pref-handlers) spell-checker pref)))