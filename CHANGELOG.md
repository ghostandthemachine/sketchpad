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
- more than double recordable (macro supported) text actions
* Leiningen inegration for projects and per project REPL's
* Scratch buffers
* Save as
* Removed some broken menu items until they are fixed
- Choose font
- File revert
- Find and Replace
* Fixed New File dialog to have propper title
* Seesaw selector erros when no buffer is open
* Double clicking directory nodes in the file tree threw error
* Sketchpad.user ns functioning properly again

### Bugfixes
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
- have a way more intuitive workflow for creating new buffers
- better saving of new buffers
- renaming files
* Add additional config options for hiding and showing tabs
* Add configs options for default components displayed like the file tree
* Merge menu refactoring which should fix some cross platrom issues as well as make search docs easy (i.e., (doc 'sketchpad.menu.file/save-as))
* Continue refining the Leiningen dependency and outside REPL processes