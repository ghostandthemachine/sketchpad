(ns sketchpad.theme.dark)

(def dark-theme 
  {
  	;; Dark theme based off of Notepad++'s Obsidian theme.
  	:baseFont {:size 14}
  	;; General editor colors.
  	:background "272822"
  	:caret "c1cbc2"
  	:selection {:bg "404E51" :rounded-edges true}
  	:current-line-highlight {:color "2F393C" :fade true}
  	:marginLine "394448"
  	:mark-all-highlight "6b8189"
  	:mark-occurrences-highlight {:color "6b8189" :border true}
  	:matchedBracket {:fg "6A8088" :bg "6b8189" :animate true}
  	:hyperlinks "A082BD"
  	
  	;; Gutter styling.
  	:gutter-border-color "81969A"
  	:line-numbers "8F908A"
  	:fold-indicator {:fg "6A8088" :icon-bg "2f383c"}
  	:icon-row-header {:active-line-range "3399ff"}
  	
  	;; Syntax tokens.
  	:identifier {:fg "E0E2E4"}
  	:reserved-word {:fg "93C763" :bold false :italic true}
  	:reserved-word-2 {:fg "FFFFFF" :bold false}
  	:annotation {:fg "E8E2B7"}
  	:comment-documentation {:fg "6C788C"}
  	:comment-eol {:fg "66747B"}
  	:comment-multiline {:fg "66747B"}
  	:comment-keyword {:fg "ae9fbf"}
  	:comment-markup {:fg "ae9fbf"}
  	:function {:fg "66D9E7" :italic true}
  	:data-type {:fg "678CB1" :bold true}
  	:literal-boolean {:fg "93C763" :bold true}
  	:literal-number-decimal-int {:fg "AD73D9"}
  	:literal-number-float {:fg "AD73D9"}
  	:literal-number-hexadecimal {:fg "AD73D9"}
  	:literal-string-double-quote {:fg "FFF45E"}
  	:literal-char {:fg "FFF45E"}
  	:literal-backquote {:fg "FFF45E"}
  	:markup-tag-delimiter {:fg "678CB1"}
  	:markup-tag-name {:fg "ABBFD3"}
  	:markup-tag-attribute {:fg "B3B689"}
  	:markup-tag-attribute-value {:fg "e1e2cf"}
  	:markup-processing-instruction {:fg "A082BD"}
  	:markup-cdata {:fg "ae9fbf"}
  	:operator {:fg "EB2828"}
  	:preprocessor {:fg "F92772"}
  	:regex {:fg "d39745"}
  	:separator {:fg "E8E2B7"}
  	:variable {:fg "D8A20" :bold false}
  	:whitespace {:fg "E0E2E4"}
  	
  	:error-identifier {:fg "E0E2E4" :bg "2E2E2C"}
  	:error-number-format {:fg "E0E2E4" :bg "2E2E2C"}
  	:error-string-double {:fg "E0E2E4" :bg "04790e"}
  	:error-char {:fg "E0E2E4" :bg "04790e"}})	