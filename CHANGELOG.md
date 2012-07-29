# Sketchpad Change Log

##Version 0.0.1 (21st July 2012)

### New Committers
* Baishampayan Ghose (ghoseb)
* Jeff Rose (rosejn)

### Updates
* Expanded features in the user ns
- search and replace functions
- lein project functions
- record and playback editing macros
- more than double record-able (macro supported) text actions
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
* Sketchpad.user ns functioning properly again

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
- Have a way more intuitive work flow for creating new buffers
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
* Config functions now documents and available via user ns
- Configurable components
	+ Buffer/Editor text areas
	+ Repl text areas
	+ Buffer gutters
	+ File tree
- All prefs can be set in config/default.clj and all configuration functions can be accessed from sketchpad.user (default editor ns) or directly in sketchpad.config
* Hide/Show scroll bars


### Buf fixes
* Fix file tree resizing. Now the resize update is drawn.
* Update repl tab labels to be more visible
* Fix files loading in dirty state
* Fix out of bounds errors for non existent dumb completions

### For next week
* Finish integrating leiningen as a dependency for managing projects and per project repls
* Begin creating the lein + SketchPad build tool for packaging sketchpad and any project
* Update projects structure to support refactoring project paths etc.
* Integrate updated RSyntaxTextArea ClojureTokenMaker
