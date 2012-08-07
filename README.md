# SketchPad
### About the project

SketchPad is a Google Summer of Code project which aims to create an IDE inspired by [Processing](processing.org) for programming audio with [Clojure](clojure.org) and [Overtone](http://overtone.github.com/). Like Processing, the goal is to create an editor and project management application simple enough for a child to download, load an example, and click run. Beyond that it will provide a centralized UI to incorporate new Overtone GUI elements, manage Overtone projects, live code with repl driven development, and most of all worry about sound design and not platforms, dependencies, and environments. The project will be built on top of [Clooj](https://github.com/arthuredelstein/clooj) and [seesaw](https://github.com/daveray/seesaw).

# Introduction tutorial

To introduce the basic features of SketchPad we'll create a new [Leiningen](https://github.com/technomancy/leiningen) project to make some sounds with Overtone. 

## Running SketchPad

In the near future there will be a standalone version available. In the meantime the project can be run like any other Leiningen project. From the project directory run

	$ lein run

This will load SketchPad and any previously opened projects as well as the SketchPad application REPL.

## Working with projects

The SketchPad project system is base on [Leiningen](https://github.com/technomancy/leiningen/) and Clojars. Leiningen is integrated into the application as a core dependency and allows SketchPad to create, build, and run Clojure projets without the need for any other libraries or system dependencies. To begin working on a project you first need to open an existing project or create a new one.

## Creating a project

New projects can be created in a few ways:

* Through the file menu, `File -> New Project...`
* or the file tree popup menu `New Project...`
* or from the SketchPad REPL with `sketchpad.user => (create-project)`

<div style="float:right">
    <img src="https://raw.github.com/ghostandthemachine/sketchpad/master/img/quil-tone-creation-form.png"/>
</div>

This will open the project setup from. The project creation form creates the new project's `project.clj` file based on the project param fields. One handy feature incorporated into the creation form is that you can use shorthand names for dependencies available on Clojars.org. The auto completion will query Clojars.org for matching dependencies and display all available versions etc. 

For this example we'll use Overtone and Quil to make a simple drawing and synth example. To add the dependencies to the project, type in the name of any repo on Clojars and all available matches will be displayed in the auto completion list. Test this out by typing overtone and selecting [overtone "0.8.0-SNAPSHOT"]. You'll see that the dependencies can be retrieved with shorthand names (`overtone -> [overtone "0.8.0-SNAPSHOT"]`, `quil -> [quil "1.6.0"]`). After you have added Overtone and Quil as dependencies, click ok. This will create the new project and a new `project.clj` file that looks like this:

```clj
    (defproject quil-tone "0.0.1-SNAPSHOT"
		:description "A SketchPad tutorial project using Overtone and Quil."
		:dependencies [[org.clojure/clojure "1.3.0"]
					   [quil "1.6.0"]
				       [overtone "0.8.0-SNAPSHOT"]])
```


## Some example code

With the project created it's time to make some circles and sounds. For this example will use the basic Quil example and add a random synth noise that is fired when a new circle is drawn. Here is the code:

```clj
	(ns quil-tone.core
	  (:use [quil.core])
	  (:require [overtone.core :as overtone]))

	(overtone/definst baz [freq 440] (* 0.3 (overtone/saw freq)))

	(defn setup []
	  (smooth)                          ;;Turn on anti-aliasing
	  (frame-rate 1)                    ;;Set framerate to 1 FPS
	  (background 200))                 ;;Set the background colour to
	                                    ;;  a nice shade of grey.
	(defn draw []
	  (stroke (random 255))             ;;Set the stroke colour to a random grey
	  (stroke-weight (random 10))       ;;Set the stroke thickness randomly
	  (fill (random 255))               ;;Set the fill colour to a random grey
	  (let [diam (random 100)           ;;Set the diameter to a value between 0 and 100
	        x    (random (width))       ;;Set the x coord randomly within the sketch
	        y    (random (height))]     ;;Set the y coord randomly within the sketch
		(overtone/kill baz)
		(baz (* 6 diam))
	    (ellipse x y diam diam)))       ;;Draw a circle at x y with the correct diameter

	(defsketch quil-tone-example                  ;;Define a new sketch named example
	  :title "Oh so many grey circles"  ;;Set the title of the sketch
	  :setup setup                      ;;Specify the setup fn
	  :draw draw                        ;;Specify the draw fn
	  :size [323 200])                  ;;You struggle to beat the golden ratio
```
Now let's try it out!

## Opening a project REPL

A REPL can be created in an outside process by right clicking a node in the new project and selecting `Create REPL`. Leiningen and Pomegranate will download all required dependencies and the new nREPL will be loaded into a new tab. Once the new tab is loaded you can navigate to the previous and next tabs with the key commands `meta alt UP` for the next REPL tab and `meta alt DOWN` for the previous tab.

With the new properly classpathed and loaded REPL we can now boot the Overtone server awestruck by our amazing art.

```clj
    ;; boot the server
    user=> (use 'overtone.live)
```

Wait for Overtone server to load...

```clj
    ;; listen to the joys of a simple sine wave
    user=> (use 'quil-tone.core)
```

You should now see the Quil panel with circles being drawn and random synths!

****

# SketchPad Feature Overview

# REPL's

## SketchPad Application REPL 
A major goal for SketchPad is for the application REPL to be a powerful tool for everything from scripting edits, to managing files, to any other IDE related task. The current features include

- Navigate session history with `control UP` and `control DOWN`
- Navigate current tab with `meta alt UP` and `meta alt DOWN`

####Search functions for current buffer:
+ Function call from REPL
	- Search `(search "word")`
	- Search-replace `(search "word" "replace-word")`
	- Search-replace-all `(search "word" "replace-word")`
+ Menu hotkey to add function call to REPL text area and focus REPL
	- Search `meta F`
	- Search-replace `meta shift F`
	- Search-replace-all `meta control F`
+ Mark occurences
	- Mark a word `(mark "word-to-mark")`
	- Clear all marked occurences `(mark)`
####Buffer edit actions

In addition to all of the copy, paste, undo, redo, etc., commands all of the common IDE edit actions are available. All of these edit actions are macro recordable and include.

#### Edit actions from `sketchpad.buffer.action`

+ Recordable edit actions that support macro recording and playback:begin-line "meta LEFT"
- selection-begin-line
- selection-end-line
- selection-end
- selection-backward
- previous-word
- selection-previous-word
- selection-down
- line-down
- selection-forward
- next-word
- selection-next-word
- selection-up
- selection-page-up
- selection-page-left
- delete-next-char
- delete-rest-of-line
- delete-line
- join-lines
- delete-prev-char
- delete-prev-word
- and more...

#### Access SketchPad projects

+ `(current-project)`



## Leiningen Project REPL's

####Access Leiningen functions

- Docs: `(doc function-name-here)`, `(find-doc "part-of-name-here")`

- Source: `(source function-name-here)`

- Javadoc: `(javadoc java-object-or-class-here)`

- Examples from clojuredocs.org: `[clojuredocs or cdoc]`




## Managing projects

#### Create a new project

- From the SketchPad application REPL `sketchpad.user => (create-project)`
	
- From the Project Menu `Project -> New Project`
	
- From the file tree popup menu `New Project`

#### Open an existing project

- From the SketchPad application REPL `sketchpad.user => (open-project "/path/to/project/root")`
	
- From the Project Menu `Project -> Open Project`
	
- From the file tree popup menu `Open Project`

## Managing buffers and tabs

#### Open a buffer









## License

Copyright (C) 2012 

Distributed under the Eclipse Public License, the same as Clojure.