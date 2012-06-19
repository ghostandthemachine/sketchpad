(def sketchpad-prefs
	{:clojure-version "1.3"})

(def default-editor-prefs
	{
		:whitespace-visible false
		:line-wrap 				false
		; :highlight-current-line true
		:rounded-selection-edges true
;		:background-img "img/dir"
		:animate-bracket-matching true
		:anti-aliasing true
		:code-folding true
		:auto-indent true
		; :clear-white-space-lines false
		:eol-marker false
;		:font "Monaco"	;; override if you don't want default based on OS
		:tab-size 				2 ;; default tab size
		:hyper-links-enabled false
		:mark-occurences false
		:mark-occurences-color "#ADA6A9"
		:paint-mark-occurences-border false
		:matched-bracket-bg-color "#C8FCBB"
		:matched-bracket-border-color "#57FC2D"
		:tab-lines-enabled false
		; :templates-enabled true
		:close-curly-braces true
	})

(def default-auto-completion-prefs
  {
  	:auto-complete true
  	:auto-activation true
  	:auto-activation-delay 500
  	:auto-complete-single-choice false
  	:show-description-window true
  	:description-window-size [600 300]
	:parameter-assistance true
  	:trigger-key "control SPACE"
  	})

