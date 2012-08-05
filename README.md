# SketchPad
### About the project

SketchPad is a Google Summer of Code project which aims to create an IDE inspired by [Processing](processing.org) for programming audio with [Clojure](clojure.org) and [Overtone](http://overtone.github.com/). Like Processing, the goal is to create an editor and project management application simple enough for a child to download, load an example, and click run. Beyond that it will provide a centralized UI to incorporate new Overtone GUI elements, manage Overtone projects, live code with repl driven development, and most of all worry about sound design and not platforms, dependencies, and enviroments. The project will be built on top of [Clooj](https://github.com/arthuredelstein/clooj) and [seesaw](https://github.com/daveray/seesaw).

# Introduction tutorial

To introduce the basic features of SketchPad we'll create a new [Leiningen](https://github.com/technomancy/leiningen) project to make some sounds with Overtone. 

## Running SketchPad

In the near future there will be a standalone version availble. In the meantime the project can be run like any other Leiningen project. From the project directory run

	$ lein run

This will load SketchPad and any previously opened projects as well as the SketchPad application REPL.

## Working with projects

The SketchPad project system is base on [Leiningen](https://github.com/technomancy/leiningen/) and Clojars. Leiningen is integrated into the application as a core dependency and allows SketchPad to create, build, and run Clojure projets without the need for any other libraries or system dependencies. To begin working on a project you first need to open an existing project or create a new one.

## Creating a project

To create a new project you can either right click in the file tree component and select `New Project` or select `File -> New Project`. This will open a dialog to choose a project directory title and location. So to create our new Overtone project we will:

1. `File -> New Project`.
2. For this tutorial, select the sketchpad/projects directory and title the new directory `overtone-tutorial`.
3. When the namespace prompt appears hit enter to verify naming the project namespace `overtone-tutorial.core`.

This will complete the Leiningen project creation process and the new project will now be loaded in the projects file tree. Now we just need to add the project's dependencies in the project.clj and we'll be ready for Leiningen to handle the rest. In the new `overtone-tutorial` project, double click the `project.clj` and update the dependencies to include Overtone:

```clj
	(defproject foobar "1.0.0-SNAPSHOT"
	    :description "FIXME: write"
	    :dependencies [[org.clojure/clojure "1.3.0"]
	  	               [overtone "0.8.0-SNAPSHOT"]])
```

## Opening a project REPL

Now with the new project loaded in the file tree and the Overtone dependency added to the project.clj, a REPL can be created in an outside process by right clicking a node in the new project and selecting `Create REPL`. Leiningen and Pomegranate will download all required dependencies and the new REPL will be loaded into a new tab. 










```clj
    ;; boot the server
    user=> (use 'overtone.live)

    ;; listen to the joys of a simple sine wave
    user=> (demo (sin-osc))

    ;; or something more interesting...
    user=>(demo 7 (lpf (mix (saw [50 (line 100 1600 5) 101 100.5]))
                  (lin-lin (lf-tri (line 2 20 5)) -1 1 400 4000)))
```






















## License

Copyright (C) 2012 

Distributed under the Eclipse Public License, the same as Clojure.
