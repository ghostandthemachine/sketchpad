(ns sketchpad.rsyntaxtextarea
  (:import (org.fife.ui.rsyntaxtextarea RSyntaxTextArea)
           (javax.swing JComponent)))

(defn background-for-token-type
"wrapper type:  :getter
        args:  [int]
       flags:  :public
  interop fn:  .getBackgroundForTokenType
 return-type:  java.awt.Color"
  [obj x]
  (.getBackgroundForTokenType obj x))

(defn matched-bracket-border-color
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getMatchedBracketBorderColor
 return-type:  java.awt.Color"
  [obj]
  (.getMatchedBracketBorderColor obj))

(defn visible-whitespace-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .VISIBLE_WHITESPACE_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/VISIBLE_WHITESPACE_PROPERTY))

(defn document!
"wrapper type:  :setter
        args:  [javax.swing.text.Document]
       flags:  :public
  interop fn:  .setDocument
 return-type:  void"
  [obj x]
  (.setDocument obj x))

(defn syntax-editing-style!
"wrapper type:  :setter
        args:  [java.lang.String]
       flags:  :public
  interop fn:  .setSyntaxEditingStyle
 return-type:  void"
  [obj x]
  (.setSyntaxEditingStyle obj x))

(defn close-markup-tags-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .CLOSE_MARKUP_TAGS_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/CLOSE_MARKUP_TAGS_PROPERTY))

(defn template-directory!
"wrapper type:  :setter
        args:  [java.lang.String]
       flags:  :synchronized :static :public
  interop fn:  .setTemplateDirectory
 return-type:  boolean"
  [obj x]
  (RSyntaxTextArea/setTemplateDirectory x))

(defn remove-hyperlink-listener
"wrapper type:  :unknown
        args:  [javax.swing.event.HyperlinkListener]
       flags:  :public
  interop fn:  .removeHyperlinkListener
 return-type:  void"
  [obj x]
  (.removeHyperlinkListener obj x))

(defn syntax-scheme!
"wrapper type:  :setter
        args:  [org.fife.ui.rsyntaxtextarea.SyntaxScheme]
       flags:  :public
  interop fn:  .setSyntaxScheme
 return-type:  void"
  [obj x]
  (.setSyntaxScheme obj x))

(defn restore-default-syntax-scheme
"wrapper type:  :unknown
        args:  []
       flags:  :public
  interop fn:  .restoreDefaultSyntaxScheme
 return-type:  void"
  [obj]
  (.restoreDefaultSyntaxScheme obj))

(defn fractional-font-metrics-enabled?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .getFractionalFontMetricsEnabled
 return-type:  boolean"
  [obj]
  (.getFractionalFontMetricsEnabled obj))

(defn parser
"wrapper type:  :getter
        args:  [int]
       flags:  :public
  interop fn:  .getParser
 return-type:  org.fife.ui.rsyntaxtextarea.parser.Parser"
  [obj x]
  (.getParser obj x))

(defn hyperlink-foreground
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getHyperlinkForeground
 return-type:  java.awt.Color"
  [obj]
  (.getHyperlinkForeground obj))

(defn remove-active-line-range-listener
"wrapper type:  :unknown
        args:  [org.fife.ui.rsyntaxtextarea.ActiveLineRangeListener]
       flags:  :public
  interop fn:  .removeActiveLineRangeListener
 return-type:  void"
  [obj x]
  (.removeActiveLineRangeListener obj x))

(defn tool-tip-text
"wrapper type:  :getter
        args:  [java.awt.event.MouseEvent]
       flags:  :public
  interop fn:  .getToolTipText
 return-type:  java.lang.String"
  [obj x]
  (.getToolTipText obj x))

(defn paint-tab-lines?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .getPaintTabLines
 return-type:  boolean"
  [obj]
  (.getPaintTabLines obj))

(defn paint-mark-occurrences-border?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setPaintMarkOccurrencesBorder
 return-type:  void"
  [obj x]
  (.setPaintMarkOccurrencesBorder obj x))

(defn token-list-for-line
"wrapper type:  :getter
        args:  [int]
       flags:  :public
  interop fn:  .getTokenListForLine
 return-type:  org.fife.ui.rsyntaxtextarea.Token"
  [obj x]
  (.getTokenListForLine obj x))

(defn should-indent-next-line?
"wrapper type:  :boolean
        args:  [int]
       flags:  :public
  interop fn:  .getShouldIndentNextLine
 return-type:  boolean"
  [obj x]
  (.getShouldIndentNextLine obj x))

(defn marked-occurrences-changed-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .MARKED_OCCURRENCES_CHANGED_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/MARKED_OCCURRENCES_CHANGED_PROPERTY))

(defn close-markup-tags?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setCloseMarkupTags
 return-type:  void"
  [obj x]
  (.setCloseMarkupTags obj x))

(defn close-curly-braces?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setCloseCurlyBraces
 return-type:  void"
  [obj x]
  (.setCloseCurlyBraces obj x))

(defn match-rectangle
"wrapper type:  :getter
        args:  []
       flags:  :public :final
  interop fn:  .getMatchRectangle
 return-type:  java.awt.Rectangle"
  [obj]
  (.getMatchRectangle obj))

(defn whitespace-visible?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .isWhitespaceVisible
 return-type:  boolean"
  [obj]
  (.isWhitespaceVisible obj))

(defn close-curly-braces?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .getCloseCurlyBraces
 return-type:  boolean"
  [obj]
  (.getCloseCurlyBraces obj))

(defn syntax-style-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .SYNTAX_STYLE_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/SYNTAX_STYLE_PROPERTY))

(defn rsyntaxtextarea
"wrapper type:  :constructor
        args:  [java.lang.String]
       flags:  :public
  interop fn:  Rsyntaxtextarea.
 return-type:  "
  [obj x]
  (RSyntaxTextArea. x))

(defn rsyntaxtextarea
"wrapper type:  :constructor
        args:  [java.lang.String int int]
       flags:  :public
  interop fn:  Rsyntaxtextarea.
 return-type:  "
  [obj x y z]
  (RSyntaxTextArea. x y z))

(defn syntax-scheme-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .SYNTAX_SCHEME_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/SYNTAX_SCHEME_PROPERTY))

(defn parser-notices-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .PARSER_NOTICES_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/PARSER_NOTICES_PROPERTY))

(defn mark-occurrences-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .MARK_OCCURRENCES_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/MARK_OCCURRENCES_PROPERTY))

(defn code-folding-enabled!
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setCodeFoldingEnabled
 return-type:  void"
  [obj x]
  (.setCodeFoldingEnabled obj x))

(defn default-bracket-match-bg-color
"wrapper type:  :getter
        args:  []
       flags:  :static :public :final
  interop fn:  .getDefaultBracketMatchBGColor
 return-type:  java.awt.Color"
  [obj]
  (RSyntaxTextArea/getDefaultBracketMatchBGColor))

(defn marked-occurrences
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getMarkedOccurrences
 return-type:  java.util.List"
  [obj]
  (.getMarkedOccurrences obj))

(defn syntax-scheme
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getSyntaxScheme
 return-type:  org.fife.ui.rsyntaxtextarea.SyntaxScheme"
  [obj]
  (.getSyntaxScheme obj))

(defn bracket-matching-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .BRACKET_MATCHING_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/BRACKET_MATCHING_PROPERTY))

(defn copy-as-rtf
"wrapper type:  :unknown
        args:  []
       flags:  :public
  interop fn:  .copyAsRtf
 return-type:  void"
  [obj]
  (.copyAsRtf obj))

(defn default-syntax-scheme
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getDefaultSyntaxScheme
 return-type:  org.fife.ui.rsyntaxtextarea.SyntaxScheme"
  [obj]
  (.getDefaultSyntaxScheme obj))

(defn close-markup-tags?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .getCloseMarkupTags
 return-type:  boolean"
  [obj]
  (.getCloseMarkupTags obj))

(defn default-bracket-match-border-color
"wrapper type:  :getter
        args:  []
       flags:  :static :public :final
  interop fn:  .getDefaultBracketMatchBorderColor
 return-type:  java.awt.Color"
  [obj]
  (RSyntaxTextArea/getDefaultBracketMatchBorderColor))

(defn animate-bracket-matching-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .ANIMATE_BRACKET_MATCHING_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/ANIMATE_BRACKET_MATCHING_PROPERTY))

(defn focusable-tips-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .FOCUSABLE_TIPS_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/FOCUSABLE_TIPS_PROPERTY))

(defn fractional-font-metrics-enabled?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setFractionalFontMetricsEnabled
 return-type:  void"
  [obj x]
  (.setFractionalFontMetricsEnabled obj x))

(defn mark-occurrences-color
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getMarkOccurrencesColor
 return-type:  java.awt.Color"
  [obj]
  (.getMarkOccurrencesColor obj))

(defn mark-occurrences-of-token-type?
"wrapper type:  :boolean
        args:  [int]
       flags:  
  interop fn:  .getMarkOccurrencesOfTokenType
 return-type:  boolean"
  [obj x]
  (.getMarkOccurrencesOfTokenType obj x))

(defn hyperlink-foreground!
"wrapper type:  :setter
        args:  [java.awt.Color]
       flags:  :public
  interop fn:  .setHyperlinkForeground
 return-type:  void"
  [obj x]
  (.setHyperlinkForeground obj x))

(defn force-reparsing
"wrapper type:  :unknown
        args:  [int]
       flags:  :public
  interop fn:  .forceReparsing
 return-type:  void"
  [obj x]
  (.forceReparsing obj x))

(defn paint-mark-occurrences-border?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .getPaintMarkOccurrencesBorder
 return-type:  boolean"
  [obj]
  (.getPaintMarkOccurrencesBorder obj))

(defn matched-bracket-border-color!
"wrapper type:  :setter
        args:  [java.awt.Color]
       flags:  :public
  interop fn:  .setMatchedBracketBorderColor
 return-type:  void"
  [obj x]
  (.setMatchedBracketBorderColor obj x))

(defn add-notify!
"wrapper type:  :should-have-bang
        args:  []
       flags:  :public
  interop fn:  .addNotify
 return-type:  void"
  [obj]
  (.addNotify obj))

(defn bracket-matching-enabled?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setBracketMatchingEnabled
 return-type:  void"
  [obj x]
  (.setBracketMatchingEnabled obj x))

(defn add-active-line-range-listener!
"wrapper type:  :should-have-bang
        args:  [org.fife.ui.rsyntaxtextarea.ActiveLineRangeListener]
       flags:  :public
  interop fn:  .addActiveLineRangeListener
 return-type:  void"
  [obj x]
  (.addActiveLineRangeListener obj x))

(defn close-curly-braces-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .CLOSE_CURLY_BRACES_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/CLOSE_CURLY_BRACES_PROPERTY))

(defn antialias-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .ANTIALIAS_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/ANTIALIAS_PROPERTY))

(defn parser-notices
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getParserNotices
 return-type:  java.util.List"
  [obj]
  (.getParserNotices obj))

(defn hyperlinks-enabled-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .HYPERLINKS_ENABLED_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/HYPERLINKS_ENABLED_PROPERTY))

(defn tab-line-color-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .TAB_LINE_COLOR_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/TAB_LINE_COLOR_PROPERTY))

(defn tab-lines-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .TAB_LINES_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/TAB_LINES_PROPERTY))

(defn fire-parser-notices-change
"wrapper type:  :unknown
        args:  []
       flags:  
  interop fn:  .fireParserNoticesChange
 return-type:  void"
  [obj]
  (.fireParserNoticesChange obj))

(defn foreground-for-token
"wrapper type:  :getter
        args:  [org.fife.ui.rsyntaxtextarea.Token]
       flags:  :public
  interop fn:  .getForegroundForToken
 return-type:  java.awt.Color"
  [obj x]
  (.getForegroundForToken obj x))

(defn bracket-matching-enabled?
"wrapper type:  :boolean
        args:  []
       flags:  :public :final
  interop fn:  .isBracketMatchingEnabled
 return-type:  boolean"
  [obj]
  (.isBracketMatchingEnabled obj))

(defn syntax-editing-style
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getSyntaxEditingStyle
 return-type:  java.lang.String"
  [obj]
  (.getSyntaxEditingStyle obj))

(defn add-hyperlink-listener!
"wrapper type:  :should-have-bang
        args:  [javax.swing.event.HyperlinkListener]
       flags:  :public
  interop fn:  .addHyperlinkListener
 return-type:  void"
  [obj]
  (.addHyperlinkListener obj obj))

(defn foreground-for-token-type
"wrapper type:  :getter
        args:  [int]
       flags:  :public
  interop fn:  .getForegroundForTokenType
 return-type:  java.awt.Color"
  [obj x]
  (.getForegroundForTokenType obj x))

(defn templates-enabled?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :synchronized :static :public
  interop fn:  .setTemplatesEnabled
 return-type:  void"
  [obj x]
  (RSyntaxTextArea/setTemplatesEnabled x))

(defn remove-parser?
"wrapper type:  :boolean
        args:  [org.fife.ui.rsyntaxtextarea.parser.Parser]
       flags:  :public
  interop fn:  .removeParser
 return-type:  boolean"
  [obj x]
  (.removeParser obj x))

(defn default-selection-color
"wrapper type:  :getter
        args:  []
       flags:  :static :public
  interop fn:  .getDefaultSelectionColor
 return-type:  java.awt.Color"
  [obj]
  (RSyntaxTextArea/getDefaultSelectionColor))

(defn clear-parsers
"wrapper type:  :unknown
        args:  []
       flags:  :public
  interop fn:  .clearParsers
 return-type:  void"
  [obj]
  (.clearParsers obj))

(defn fire-marked-occurrences-changed
"wrapper type:  :unknown
        args:  []
       flags:  
  interop fn:  .fireMarkedOccurrencesChanged
 return-type:  void"
  [obj]
  (.fireMarkedOccurrencesChanged obj))

(defn font-for-token-type
"wrapper type:  :getter
        args:  [int]
       flags:  :public
  interop fn:  .getFontForTokenType
 return-type:  java.awt.Font"
  [obj x]
  (.getFontForTokenType obj x))

(defn animate-bracket-matching?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .getAnimateBracketMatching
 return-type:  boolean"
  [obj]
  (.getAnimateBracketMatching obj))

(defn matched-bracket-bg-color
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getMatchedBracketBGColor
 return-type:  java.awt.Color"
  [obj]
  (.getMatchedBracketBGColor obj))

(defn line-height
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getLineHeight
 return-type:  int"
  [obj]
  (.getLineHeight obj))

(defn auto-indent-enabled?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .isAutoIndentEnabled
 return-type:  boolean"
  [obj]
  (.isAutoIndentEnabled obj))

(defn mark-occurrences-color!
"wrapper type:  :setter
        args:  [java.awt.Color]
       flags:  :public
  interop fn:  .setMarkOccurrencesColor
 return-type:  void"
  [obj x]
  (.setMarkOccurrencesColor obj x))

(defn rsyntaxtextarea
"wrapper type:  :constructor
        args:  [int]
       flags:  :public
  interop fn:  Rsyntaxtextarea.
 return-type:  "
  [obj x]
  (RSyntaxTextArea. x))

(defn add-parser!
"wrapper type:  :should-have-bang
        args:  [org.fife.ui.rsyntaxtextarea.parser.Parser]
       flags:  :public
  interop fn:  .addParser
 return-type:  void"
  [obj x]
  (.addParser obj x))

(defn use-focusable-tips?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setUseFocusableTips
 return-type:  void"
  [obj x]
  (.setUseFocusableTips obj x))

(defn mark-occurrences?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .getMarkOccurrences
 return-type:  boolean"
  [obj]
  (.getMarkOccurrences obj))

(defn eol-markers-visible?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .getEOLMarkersVisible
 return-type:  boolean"
  [obj]
  (.getEOLMarkersVisible obj))

(defn hyperlinks-enabled?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .getHyperlinksEnabled
 return-type:  boolean"
  [obj]
  (.getHyperlinksEnabled obj))

(defn auto-indent-enabled?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setAutoIndentEnabled
 return-type:  void"
  [obj x]
  (.setAutoIndentEnabled obj x))

(defn tab-line-color!
"wrapper type:  :setter
        args:  [java.awt.Color]
       flags:  :public
  interop fn:  .setTabLineColor
 return-type:  void"
  [obj x]
  (.setTabLineColor obj x))

(defn remove-notify
"wrapper type:  :unknown
        args:  []
       flags:  :public
  interop fn:  .removeNotify
 return-type:  void"
  [obj]
  (.removeNotify obj))

(defn active-line-range!
"wrapper type:  :setter
        args:  [int int]
       flags:  :public
  interop fn:  .setActiveLineRange
 return-type:  void"
  [obj x y]
  (.setActiveLineRange obj x y))

(defn clear-whitespace-lines-enabled!
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setClearWhitespaceLinesEnabled
 return-type:  void"
  [obj x]
  (.setClearWhitespaceLinesEnabled obj x))

(defn code-folding-enabled?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .isCodeFoldingEnabled
 return-type:  boolean"
  [obj]
  (.isCodeFoldingEnabled obj))

(defn anti-aliasing-enabled?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .getAntiAliasingEnabled
 return-type:  boolean"
  [obj]
  (.getAntiAliasingEnabled obj))

(defn anti-aliasing-enabled?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setAntiAliasingEnabled
 return-type:  void"
  [obj x]
  (.setAntiAliasingEnabled obj x))

(defn fold-manager
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getFoldManager
 return-type:  org.fife.ui.rsyntaxtextarea.folding.FoldManager"
  [obj]
  (.getFoldManager obj))

(defn auto-indent-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .AUTO_INDENT_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/AUTO_INDENT_PROPERTY))

(defn fractional-fontmetrics-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .FRACTIONAL_FONTMETRICS_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/FRACTIONAL_FONTMETRICS_PROPERTY))

(defn code-folding-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .CODE_FOLDING_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/CODE_FOLDING_PROPERTY))

(defn eol-visible-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .EOL_VISIBLE_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/EOL_VISIBLE_PROPERTY))

(defn tab-line-color
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getTabLineColor
 return-type:  java.awt.Color"
  [obj]
  (.getTabLineColor obj))

(defn clear-whitespace-lines-enabled?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .isClearWhitespaceLinesEnabled
 return-type:  boolean"
  [obj]
  (.isClearWhitespaceLinesEnabled obj))

(defn font!
"wrapper type:  :setter
        args:  [java.awt.Font]
       flags:  :public
  interop fn:  .setFont
 return-type:  void"
  [obj x]
  (.setFont obj x))

(defn max-ascent
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getMaxAscent
 return-type:  int"
  [obj]
  (.getMaxAscent obj))

(defn parser-count
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getParserCount
 return-type:  int"
  [obj]
  (.getParserCount obj))

(defn animate-bracket-matching?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setAnimateBracketMatching
 return-type:  void"
  [obj x]
  (.setAnimateBracketMatching obj x))

(defn rsyntaxtextarea
"wrapper type:  :constructor
        args:  [int int]
       flags:  :public
  interop fn:  Rsyntaxtextarea.
 return-type:  "
  [obj x y]
  (RSyntaxTextArea. x y))

(defn rsyntaxtextarea
"wrapper type:  :constructor
        args:  []
       flags:  :public
  interop fn:  Rsyntaxtextarea.
 return-type:  "
  [obj]
  (RSyntaxTextArea.))

(defn match
"wrapper type:  :unknown
        args:  
       flags:  
  interop fn:  .match
 return-type:  "
  [obj]
  (.match obj))

(defn paint-tab-lines?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setPaintTabLines
 return-type:  void"
  [obj x]
  (.setPaintTabLines obj x))

(defn force-reparsing?
"wrapper type:  :boolean
        args:  [org.fife.ui.rsyntaxtextarea.parser.Parser]
       flags:  :public
  interop fn:  .forceReparsing
 return-type:  boolean"
  [obj x]
  (.forceReparsing obj x))

(defn underline-for-token?
"wrapper type:  :boolean
        args:  [org.fife.ui.rsyntaxtextarea.Token]
       flags:  :public
  interop fn:  .getUnderlineForToken
 return-type:  boolean"
  [obj x]
  (.getUnderlineForToken obj x))

(defn hyperlinks-enabled?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setHyperlinksEnabled
 return-type:  void"
  [obj x]
  (.setHyperlinksEnabled obj x))

(defn fold-toggled
"wrapper type:  :unknown
        args:  [org.fife.ui.rsyntaxtextarea.folding.Fold]
       flags:  :public
  interop fn:  .foldToggled
 return-type:  void"
  [obj x]
  (.foldToggled obj x))

(defn highlighter!
"wrapper type:  :setter
        args:  [javax.swing.text.Highlighter]
       flags:  :public
  interop fn:  .setHighlighter
 return-type:  void"
  [obj x]
  (.setHighlighter obj x))

(defn code-template-manager
"wrapper type:  :getter
        args:  []
       flags:  :synchronized :static :public
  interop fn:  .getCodeTemplateManager
 return-type:  org.fife.ui.rsyntaxtextarea.CodeTemplateManager"
  [obj]
  (RSyntaxTextArea/getCodeTemplateManager))

(defn whitespace-visible!
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setWhitespaceVisible
 return-type:  void"
  [obj x]
  (.setWhitespaceVisible obj x))

(defn eol-markers-visible?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setEOLMarkersVisible
 return-type:  void"
  [obj x]
  (.setEOLMarkersVisible obj x))

(defn use-focusable-tips?
"wrapper type:  :boolean
        args:  []
       flags:  :public
  interop fn:  .getUseFocusableTips
 return-type:  boolean"
  [obj]
  (.getUseFocusableTips obj))

(defn font-metrics-for-token-type
"wrapper type:  :getter
        args:  [int]
       flags:  :public
  interop fn:  .getFontMetricsForTokenType
 return-type:  java.awt.FontMetrics"
  [obj x]
  (.getFontMetricsForTokenType obj x))

(defn mark-occurrences?
"wrapper type:  :boolean
        args:  [boolean]
       flags:  :public
  interop fn:  .setMarkOccurrences
 return-type:  void"
  [obj x]
  (.setMarkOccurrences obj x))

(defn last-visible-offset
"wrapper type:  :getter
        args:  []
       flags:  :public
  interop fn:  .getLastVisibleOffset
 return-type:  int"
  [obj]
  (.getLastVisibleOffset obj))

(defn link-scanning-mask!
"wrapper type:  :setter
        args:  [int]
       flags:  :public
  interop fn:  .setLinkScanningMask
 return-type:  void"
  [obj x]
  (.setLinkScanningMask obj x))

(defn save-templates?
"wrapper type:  :boolean
        args:  []
       flags:  :synchronized :static :public
  interop fn:  .saveTemplates
 return-type:  boolean"
  [obj]
  (RSyntaxTextArea/saveTemplates))

(defn matched-bracket-bg-color!
"wrapper type:  :setter
        args:  [java.awt.Color]
       flags:  :public
  interop fn:  .setMatchedBracketBGColor
 return-type:  void"
  [obj x]
  (.setMatchedBracketBGColor obj x))

(defn clear-whitespace-lines-property
"wrapper type:  :unknown
        args:  
       flags:  :static :public :final
  interop fn:  .CLEAR_WHITESPACE_LINES_PROPERTY
 return-type:  "
  [obj]
  (RSyntaxTextArea/CLEAR_WHITESPACE_LINES_PROPERTY))

(defn templates-enabled?
"wrapper type:  :boolean
        args:  []
       flags:  :synchronized :static :public
  interop fn:  .getTemplatesEnabled
 return-type:  boolean"
  [obj]
  (RSyntaxTextArea/getTemplatesEnabled))

(defn rsyntaxtextarea
"wrapper type:  :constructor
        args:  [org.fife.ui.rsyntaxtextarea.RSyntaxDocument]
       flags:  :public
  interop fn:  Rsyntaxtextarea.
 return-type:  "
  [obj x]
  (RSyntaxTextArea. x))

(defn replace-range! [rsta s start end]
  "Replaces text from the indicated start to end position with the new text specified. Does nothing if the model is null. Simply does a delete if the new string is null or empty.
This method is thread safe, although most Swing methods are not. Please see Threads and Swing for more information.
Parameters:
str - the text to use as the replacement
start - the start position >= 0
end - the end position >= start
"
  (.replaceRange rsta s start end))

(defn input-map 
  [rta]
  (.getInputMap rta))

(defn set-parent!
  [im comp]
  (.setParent im (input-map comp)))

(defn set-input-map! 
  [rta im]
  (.setParent im (.getInputMap rta))
  (.setInputMap rta JComponent/WHEN_FOCUSED im))

(defn set-action-map! 
  [rta im]
  (.setActionMap rta im))

(defn is-osx? []
  (RSyntaxTextArea/isOSX ))
