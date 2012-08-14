# SketchPad Change Log

##Version 0.0.1 (21st July 2012)

### New Committers
* Baishampayan Ghose (ghoseb)
* Jeff Rose (rosejn)

### Updates
* Expanded features in the user ns
- search and replace functions
- lein project functions
- record and playback editing macros
- more than double recordable (macro supported) text actions
* Leiningen integration for projects and per project REPL's
* Scratch buffers
* Save as
* Removed some broken menu items until they are fixed
- Choose font
- File revert
- Find and Replace
* Fixed New File dialog to have proper title
* Seesaw selector errors when no buffer is open
* Double clicking directory nodes in the file tree threw error
* SketchPad.user ns functioning properly again

### Bug fixes
* Errors cause by canceling a file save operation
* Dumb completion throwing error when no prefix or completion exist
* Close tab not working after tab saved
* Quit menu options appears twice
* Go to file tree focus
* Go to REPL focus
* Go to editor focus
* Create new REPL

### For next week
* Merge tab and buffer management updates which
- Have a way more intuitive workflow for creating new buffers
- Better saving of new buffers
- Renaming files
* Add additional config options for hiding and showing tabs
* Add configs options for default components displayed like the file tree
* Merge menu re-factoring, which should fix some cross platform issues as well as make search docs easy (i.e., (doc 'sketchpad.menu.file/save-as))
* Continue refining the Leiningen dependency and outside REPL processes


##Version 0.0.1 (29st July 2012)

**these updates are currently in the dev branch**

### Updates
* Scratch buffers
* Show/hide tabs
* More config options 
* Config functions now documents are available via user ns
- Configurable components
	+ Buffer/Editor text areas
	+ Repl text areas
	+ Buffer gutters
	+ File tree
- All prefs can be set in config/default.clj and all configuration functions can be accessed from sketchpad.user (default editor ns) or directly in sketchpad.config
* Hide/Show scroll bars


### Bug fixes
* Fix file tree resizing. Now the resize update is drawn.
* Update repl tab labels to be more visible
* Fix files loading in dirty state
* Fix out of bounds errors for non existent dumb completions

### For next week
* Finish integrating leiningen as a dependency for managing projects and per project repls
* Begin creating the lein + SketchPad build tool for packaging sketchpad and any project
* Update projects structure to support refactoring project paths etc.
* Integrate updated RSyntaxTextArea ClojureTokenMaker


##Version 0.0.1 (5th August 2012)

### Updates
* Leiningen projects
	- Leiningen project builder:
		+ Generates project.clj
		+ Auto Completion dependency input that is generated from available dependencies on Clojars.org. This will show all available deps for a given repo name.
	- Per project REPL's
		+ Each project REPL is created in an outside process via Leiningen
		+ All project deps are downloaded and managed by Pomegranate and require no local install of Leiningen
		+ nREPL server/client
* Merged dev updates into master
	- Substantially more config options for:
		+ Editor text areas
			- show/hide scrollbars
			- color schemes
		+ REPL text areas
			- show/hide scrollbars
			- color schemes
		+ Buffer Gutters
			- line number font
			- color schemes
			- index start
		+ File tree
			- show/hide scrollbars

### Bug fixes
* Menu item `Source->Find` now focuses Editor REPL and adds search function call.
* Source -> Toggle comment works correctly now on active buffer.
* Fixed and added methods for creating Leiningen project:
	- `File->New Project`
	- file tree right-click popup menu  `New Project`
	- SketchPad REPL, `sketchpad.user => (create-project)`
* Fixed painting bugs caused by EDT threading issues for
	- `sketchpad.user => (search "word"), (search-replace "word" "replace"), (search-replace-all "word" "replace")`
	- `sketchpad.user => (mark-occurrences "word")`

### For next week
* Continue refining Leiningen project management and creation process.
* Update documentation
* Add new tutorials for creating and managing projects and dependencies all from within SketchPad.


##Version 0.0.1 (12th August 2012)

### Updates

### Bug fixes

### For next week
