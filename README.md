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

<div style="float:right">
    <img src="https://raw.github.com/ghostandthemachine/sketchpad/master/img/quil-tone-creation-form.png"/>
</div>

New projects can be created in a few ways:

* Through the file menu, `File -> New Project...`
* or the file tree popup menu `New Project...`
* or from the SketchPad REPL with `sketchpad.user => (create-project)`

This will open the project setup from. The project creation form creates the new project's `project.clj` file based on the project param fields. One handy feature incorporated into the creation form is that you can use shorthand names for dependencies available on Clojars.org. The auto completion will query Clojars.org for matching dependencies and display all available versions etc. 

For this example we'll use Overtone and Quil to make a simple drawing and synth example. The dependencies text area will 

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
