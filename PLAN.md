## Sketchpad Road Map

### Project Goals
- Create a cross platform application for programming Clojure and Overtone with these core features:
+ It is simple enough for a 10 year old to download, open, select an example, and run
+ Manages project and application dependencies through leinengine
+ Provides a rich text editor through [seesaw](https://github.com/daveray/seesaw), [Clooj](https://github.com/arthuredelstein/clooj), and [RSyntaxTextArea](http://fifesoft.com/rsyntaxtextarea/)
+ Has extensive documentation browsing for both clojure libraries as well as in depth Overtone API docs and SuperCollider UGen docs
+ Integration of Overtone GUI components 

-----------------------------------------------------------------------------------------------

### Road Map

#### Clooj updates
- Turn Clooj into a modular editor and repl library
	+ Fork and re factor Clooj to work nicely with seesaw by converting standard Clooj interop code to seesaw.
	+ Extract clooj application components from clooj.core and create a new clooj.dev-tools namespace which will contain all of the application components.
	+ Update clooj.core to build standard Clooj with simple (create-application) function where the desired components can be added and styled.  

- Create Sketchpad in overtone-sketchpad.core
	+ Use the clooj.dev-tools namespace to create custom Clooj base application as base of Sketchpad
	+ Overhaul standard Clooj layout to be more suited to Overtone:
	+ Use less real estate by making components collapsible
	+ Style and minimize seesaw borders and dividers to maximize frame space
	+ Implement tab system for multiple files being open at one time
	+ Boot overtone server on startup

- Add Overtone API documentation and SuperCollider UGen documentation
	+ Integrate JavaFX 2 WebView component via [upshot](https://github.com/daveray/upshot)
	+ Boot local web server with Noir to host Overtone documentation and examples
	+ Build WebView based documentation browser for searching and navigating docs and examples

- Integrate Overtone GUI components
	+ Widget docking system
	+ In text GUI component interaction

- Hardware management
	+ Midi device manager
	+ Midi controller assignment editor to customize different hardware controllers