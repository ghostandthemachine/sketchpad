# Sketchpad
## Getting started
   
      $ git clone git@github.com:ghostandthemachine/sketchpad.git
      $ git clone git@github.com:overtone/overtone.git

      $ cd sketchpad

      $ lein deps

      $ lein run

Once the Sketchpad is open you need to select Open Project under the Project
menu and open the Overtone directory.
To explore the examples select examples in the Doc Tree. Once you select and
example you can evaluate an entire file in the repl by hitting cmd E. 

## About the project

The Overtone Sketchpad is a Google Summer of Code project which aims to create an IDE inspired by [Processing](processing.org) for programming audio with [Clojure](clojure.org) and [Overtone](http://overtone.github.com/). Like Processing, the goal is to create an editor and project management application simple enough for a child to download, load an example, and click run. Beyond that it will provide a centralized UI to incorporate new Overtone GUI elements, manage Overtone projects, live code with repl driven development, and most of all worry about sound design and not platforms, dependencies, and enviroments. The project will be built on top of [Clooj](https://github.com/arthuredelstein/clooj) and [seesaw](https://github.com/daveray/seesaw). 

## License

Copyright (C) 2012 

Distributed under the Eclipse Public License, the same as Clojure.
