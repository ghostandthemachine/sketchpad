(use '[seesaw color])

(def default-background-color [39 49 34])

(def default-editor-prefs
   {:buffer-theme "themes/dark.xml" ;; load RSyntaxTextArea from xml theme file
	;; params after this point will overide those set by the theme selection above
	; :background-image "img/invisible.png"
	:whitespace-visible false
	:line-wrap 	true
	:rounded-selection-edges true
	:animate-bracket-matching true
	:anti-aliasing true
	:code-folding true
	:auto-indent true
	:eol-marker false
	:font "MENLO-BOLD-10"
	:tab-size 				2
	:hyper-links-enabled false
	:mark-occurences false
	:mark-occurences-color "#ADA6A9"
	:paint-mark-occurences-border false
	:matched-bracket-bg-color [80 240 70 50]
	:matched-bracket-border-color [80 240 70 100]
	:tab-lines-enabled true
	:tab-lines-color [100 100 100 100]
	:templates-enabled true
	:close-curly-braces true})

(def default-repl-prefs
   {:buffer-theme "themes/dark.xml" ;; load RSyntaxTextArea from xml theme file
	;; params after this point will overide those set by the theme selection above
	; :background-image "img/invisible.png"
	:whitespace-visible false
	:line-wrap 	true
	:rounded-selection-edges true
	:animate-bracket-matching true
	:anti-aliasing true
	:code-folding true
	:auto-indent true
	:eol-marker false
	:font "MENLO-BOLD-10"
	:tab-size 				2
	:hyper-links-enabled false
	:mark-occurences false
	:mark-occurences-color "#ADA6A9"
	:paint-mark-occurences-border false
	:matched-bracket-bg-color [80 240 70 50]
	:matched-bracket-border-color [80 240 70 100]
	:tab-lines-enabled true
	:tab-lines-color [100 100 100 100]
	:templates-enabled true
	:close-curly-braces true
	:response-timeout 1000})

(def default-repl-scroller-prefs
  {:vertical-scroll-bar false
   :horizontal-scroll-bar false
   :scroller-border-enabled false})

(def default-auto-completion-prefs
  { :auto-complete true
  	:auto-activation true
  	:auto-activation-delay 1
  	:auto-complete-single-choice false
  	:show-description-window true
  	:description-window-size [600 300]
	:parameter-assistance true
  	:trigger-key "control SPACE"})

(def default-buffer-scroller-prefs
  { :vertical-scroll-bar false
   	:horizontal-scroll-bar false
  	:fold-indicator-enabled false ;; not functional until new Clojure Lexxer is integrated
   	:line-numbers-enabled true
   	:scroller-border-enabled false})

(def default-file-scroller-tree-prefs
	{:vertical-scroll-bar false
	 :horizontal-scroll-bar false})

(def default-file-tree-prefs
	{})

(def default-gutter-prefs
  {:border-color                       [0 0 0 0]
   :bookmarking-enabled                false
   :fold-indicator-enabled             false  ;; not functional until new Clojure Lexxer is integrated
   :line-number-color                  [143 144 134]
   :line-number-font                   "MENLO-10"
   :line-number-start-index            1
   :active-range-color 				   [50 50 41]})

(def default-sketchpad-prefs
	{:show-tabs? true})

(def default-project-style-prefs
	[(color :white) (color :orange) (color :green) (color :yellow) (color :blue) (color :red) (color :purple) (color :pink)])
