##Sketchpad Overview
Sketchpad is a light weight editor written in Clojure. With Clojure being the single target language, the entire application is geared towards Clojure development and intuitive access to common Clojure tools. This also gives developers access to features which are not traditionally available in IDE's.  As Sketchpad is the base for the Overtone GSoC project, it is designed to be integrated with outside libraries and projects. Here are some of core initial features.
####Editor
The text editor component contains the active buffers in tabs. A tab is opened by double clicking a file node in the file tree. The Sketchpad text area is built on the RSyntaxTextArea library which supports the majority of features popular IDE's that Eclipse and NetBeans do. While the features are too many to list some include: syntax highlight for almost 30 languages, auto-completion and dumb completion, search and replace, gutters, bracket matching, and a large library of macro recordable actions for manipulating buffers. Currently the main attributes are configurable from the config/default.clj file, which contains user preferences. Tabs can be navigated with cmd+alt+left/right and closed with cmd+w.
####File tree
Standard file tree for opening, closing, and creating projects and files. Can be hidden with cmd+1
####REPL
From manipulating buffers and creating executable macros, to managing project file structures and Leiningen projects, the Sketchpad editor REPL is a powerful tool for working with Clojure projects. The editor REPL defaults to load the sketchpad.user namespace, which provides functions for interacting with projects. Project repls will ultimately be created in outside processes via Leiningen, nrepl, and pomegranate. 
####User namespace
The goal is to have the user namespace provide access and functionality as powerful as vim and emacs but in digestible Clojure code. Scripting macros and custom tools is simple and logical. Since Sketchpad is written in Clojure, the user namespace can also deal directly with Leiningen projects, the editor application it self, and much more.
####Projects and Leiningen
This part of the project is still young but will be a central cog in the Sketchpad architecture. Leiningen has quickly become one the most popular Clojure project management tools and with the release of 2.0 this it will only be more popular. The goal is to leverage all of the Leiningen features from managing dependencies and projects to publishing projects to clojars all from Sketchpad and in Clojure. 
####Future plans
While the Overtone features for Sketchpad will be specialized for interacting with Overtone code, there are many other aspects of the project that have plans and potential. Leiningen integration will not only provide robust project management but also a nice way to integrate future features and plugins. Ideally Sketchpad will allow for installing new features and plugins directly from clojars.
Beyond providing a way to package Overtone and a powerful editor in one package there are a lot of plans for incorporating useful tools specific to hacking audio with Overtone. Here are some of the plans for audio related features:
- controlling code with GUI widgets. i.e. easy access to popup widgets like sliders and dials which update actual synth def parameters. 
- similarly, manipulate sequencer data structures with popup widgets. Double clicking a vector in a sequencer def could bring up a sequencer widget, which in turn updates the vector’s values in the file. etc.
- monitor levels in the text areas. Similar to Garage band, it would be nice to have level meters in the gutter for active synths. 
- in-depth Super Collider and Overtone docs integrated into Overtone projects. 
- a widget presentation component where widgets can be centrally organized and access in a logical way. 


