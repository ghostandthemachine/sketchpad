; (use '[seesaw color])

(def default-background-color [39 49 34])

(def default-editor-prefs
   {:buffer-theme "dark.xml" ;; load RSyntaxTextArea from xml theme file
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
	:line-wrap 	false
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
  	:auto-activation-delay 1000
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
   :fold-indicator-enabled             true  ;; not functional until new Clojure Lexxer is integrated
   :line-number-color                  [143 144 134]
   :line-number-font                   "MENLO-10"
   :line-number-start-index            1
   :active-range-color 				   [50 50 41]})

(def default-sketchpad-prefs
	{:show-tabs? true})

(def project-dir-path "projects/")

(def default-project-style-prefs
	[:white :orange :green :yellow :blue :red :purple :pink])

(def default-buffer-key-bindings
 {:begin-line "meta LEFT"
	:begin "meta UP"
	:selection-begin "meta shift UP"
	:end-line "meta RIGHT"
	:selection-begin-line "meta shift LEFT"
	:selection-end-line "meta shift RIGHT"
	:end "meta DOWN"
	:selection-end "meta shift DOWN"
	:backward "LEFT"
	:selection-backward "shift LEFT"
	:previous-word "alt LEFT"
	:selection-previous-word "alt shift LEFT"
	:down "DOWN"
	:selection-down "shift DOWN"
	:line-down "alt DOWN"
	:forward "RIGHT"
	:selection-forward "shift RIGHT"
	:next-word "alt RIGHT"
	:selection-next-word "alt shift RIGHT"
	:up "UP"
	:selection-up "alt shift UP"
	:line-up "alt UP"
	:page-up "PAGE_UP"
	:selection-page-up "shift PAGE_UP"
	:selection-page-left "meta shift PAGE_UP"
	:page-down "PAGE_DOWN"
	:selection-page-down "shift PAGE_DOWN"
	:selection-page-right "meta shift PAGE_DOWN"
	:key-cut "meta CUT"
	:key-copy "meta COPY"
	:key-paste "meta PASTE"
	:cut "meta X"
	:copy "meta C"
	:paste "meta V"
	:delete-next-char "DELETE"
	:delete-cut "shift DELETE"
	:delete-rest-of-line "meta DELETE"
	:toggle-text-mode "INSERT"
	:insert-paste "shift INSERT"
	:insert-copy "meta INSERT"
	:select-all "meta A"
	:delete-line "meta D"
	:join-lines "meta J"
	:delete-prev-char "shift BACK_SPACE"
	:delete-prev-word "meta BACK_SPACE"
	:scroll-down "meta alt OPEN_BRACKET"
	:scroll-up "meta alt CLOSE_BRACKET"
	:insert-tab "TAB"
	:insert-break "ENTER"
	:shift-insert-break "shift ENTER"
	:dumb-complete-word "alt TAB"
	:undo "meta Z"
	:redo "meta shift Z"
	:next-bookmark "F2"
	:prev-bookmark "shift F2"
	:toggle-bookmark "meta F2"
	:prev-occurrence "meta shift K"
	:next-occurrence "meta K"})
