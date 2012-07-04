(ns theme.dark)

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
  	:reserved_word {:fg "93C763" :bold false :italic true}
  	:reserved_word_2 {:fg "FFFFFF" :bold false}
  	:annotation {:fg "E8E2B7"}
  	:comment_documentation {:fg "6C788C"}
  	:comment_eol {:fg "66747B"}
  	:comment_multiline {:fg "66747B"}
  	:comment_keyword {:fg "ae9fbf"}
  	:comment_markup {:fg "ae9fbf"}
  	:function {:fg "66D9E7" :italic true}
  	:data_type {:fg "678CB1" :bold true}
  	:literal_boolean {:fg "93C763" :bold true}
  	:literal_number_decimal_int {:fg "AD73D9"}
  	:literal_number_float {:fg "AD73D9"}
  	:literal_number_hexadecimal {:fg "AD73D9"}
  	:literal_string_double_quote {:fg "FFF45E"}
  	:literal_char {:fg "FFF45E"}
  	:literal_backquote {:fg "FFF45E"}
  	:markup_tag_delimiter {:fg "678CB1"}
  	:markup_tag_name {:fg "ABBFD3"}
  	:markup_tag_attribute {:fg "B3B689"}
  	:markup_tag_attribute_value {:fg "e1e2cf"}
  	:markup_processing_instruction {:fg "A082BD"}
  	:markup_cdata {:fg "ae9fbf"}
  	:operator {:fg "EB2828"}
  	:preprocessor {:fg "F92772"}
  	:regex {:fg "d39745"}
  	:separator {:fg "E8E2B7"}
  	:variable {:fg "D8A20" :bold false}
  	:whitespace {:fg "E0E2E4"}
  	
  	:error_identifier {:fg "E0E2E4" :bg "2E2E2C"}
  	:error_number_format {:fg "E0E2E4" :bg "2E2E2C"}
  	:error_string_double {:fg "E0E2E4" :bg "04790e"}
  	:error_char {:fg "E0E2E4" :bg "04790e"}})