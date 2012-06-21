(def sketchpad-prefs
	{:clojure-version "1.3"})

(def default-editor-prefs
	{
		;; load RSyntaxTextArea from xml theme file
		:rsta-theme "themes/dark.xml"
		;; params after this point will overide those set by the theme selection above
		:whitespace-visible false
		:line-wrap 				false
		:rounded-selection-edges true
		:animate-bracket-matching true
		:anti-aliasing true
		:code-folding true
		:auto-indent true
		:eol-marker false
;		:font "Monaco"	;; override if you don't want default based on OS
		:tab-size 				2
		:hyper-links-enabled false
		:mark-occurences false
		:mark-occurences-color "#ADA6A9"
		:paint-mark-occurences-border false
		:matched-bracket-bg-color [80 240 70 50]
		:matched-bracket-border-color [80 240 70 100]
		:tab-lines-enabled false
		:close-curly-braces true
	})

(def default-auto-completion-prefs
  {
  	:auto-complete true
  	:auto-activation true
  	:auto-activation-delay 1
  	:auto-complete-single-choice false
  	:show-description-window true
  	:description-window-size [600 300]
	  :parameter-assistance true
  	:trigger-key "control SPACE"
  	})

