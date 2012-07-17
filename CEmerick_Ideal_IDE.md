###From Chas Emerick's Post ["The Ideal Clojure Development Environment"](http://cemerick.com/ideal-clojure-development-environment/)
- - - -
- Pervasive syntax highlighting

	+ More than just s-expressions and typical values: regex literals, <a href="http://cemerick.com/2009/12/04/string-interpolation-in-clojure/">interpolated strings</a>, etc

- ~~Brace, bracket, paren matching~~
- paredit equivalency
- Easily-togglable rainbow parens (I usually hate them, but then end up thinking they&#8217;d be handy for ~5 seconds)
- s-expression navigation
- automatic structural formatting

- don&#8217;t apply the same rules to docstrings as would be applied to other multiline string literals

- all of the above needs to deeply configurable – there are lots of different preferences out there, and I hold at least half of them from time to time!

- code completion

	+ ~~Should have visibility to locally-defined vars/bindings, those that are :required or :used, as well as stuff in core~~
	+ Host (Java) interop completion should be key off of the type hints in the surrounding code, and do what it can with imports and such
	+ ~~In all cases, provide associated docs as well as parameter hints (that are bolded, etc. as you go along adding parameters)~~

- in-place popup macroexpansion
- generation of deftype/defrecord/extend-type/gen-class/proxy method scaffolding

	+ Roughly the equivalent of the &#8220;implement method&#8221; quickfix that is available in Java IDEs when you add a superclass or interface to a class definition; i.e. be able to type <tt>(proxy [java.awt.event.MouseAdapter]</tt>, and have shells of the various methods from the specified interface and/or abstract superclasses dropped into place. The same thing should be applicable to all forms that generate classes from clojure.


- absolutely top-notch Java development support

	+ this one item almost necessitates that I&#8217;ll always be in a major IDE, i.e. Eclipse, IntellJ, or NetBeans. A lot of the lower-level plumbing that&#8217;s necessary to pull off things like a complete debugger, profiler, etc. etc. is so complex, I can&#8217;t imagine anyone matching &#8220;the big three&#8221; without simply co-opting one of them.

- debugger and profiler that are able to effortlessly &#8220;focus&#8221; on clojure-level or java-level code/data structures depending on what I&#8217;m interested in at the time
- code coverage tools that hook into clojure.test and, again, can track clojure- and/or java-level coverage equally well

- maven (and eventually, clojure <a href="http://polyglot.sonatype.org/">polyglot maven</a>) support

- ~~ensure that project settings, REPL configuration, etc. etc. are all driven from the POM~~

- ~~insofar as lein becomes a mainstay of clojure development, support will be needed for it as well~~

- Refactoring (rarely needed, but when needed, it would be <strong>incredibly</strong>helpful)

	+ ~~This would almost certainly be limited in static usage – local renames, and such.~~
	+ ~~Given an open REPL that&#8217;s had my entire application loaded (and therefore should be able to use function/var metadata to its fullest), I should be able to do damn near anything you can do in a top-notch Java IDE (within the bounds of what&#8217;s relevant, anyway).
	+ the equivalent of Java IDEs&#8217; &#8220;organize imports&#8221; in clojure ns use/import/require clauses~~

- Static analysis

	+ Symbol navigation

- Go to declaration
- A &#8220;Go to Var&#8221; option, corollary to &#8220;Go to File&#8221; or &#8220;Open Resource&#8221;; I should be able to type &#8220;redu&#8221; to find and go to <tt>reductions</tt>, or type &#8220;w-o&#8221; to find and to go <tt>with-open</tt>, in any namespace in any open project

	+ Find usages

- Making this work across projects would be absolutely stellar.  i.e. If I&#8217;ve got two projects open, I should be able to find usages in project B of a var defined in a namespace in project A.


- current-file code outline
- static namespace browser, with reasonable search capabilities

- Sane UI/UX

	+ progressive disclosure, discoverability of functionality, familiar help systems, etc
	+ ~~Supporting, nay, embracing mousing. Yes, I use the mouse (actually, a trackball) in addition to the keyboard, get over it. Much of my time spent &#8220;programming&#8221; is actually spent doing things other than typing (thank goodness), and many of these tasks are not best done with the keyboard.~~
	+ All of this <strong>should</strong> go without saying IMO, but emacs seems to put a stake in the ground that much of the above is unnecessary or undesirable. The issue of progressive disclosure and discovery of functionality is a particular sore spot for me. Many of the most important features of a development environment (or perhaps any class of nontrivial software) are the most esoteric that might go untouched for weeks or months (debuggers, profilers, code coverage tools, configuration settings, etc), and one must be able to <strong>effectively</strong> stumble through them for the first time, and after having not touched them for a long time. I don&#8217;t want to have to learn, remember, or google for the <tt>M-x run-clojure-debugger</tt> command or somesuch along with 200, 500, 1000? others.


- REPL support (overall, I think it&#8217;d be hard to improve on enclojure<sup><a href="#fn1"> 1 </a> </sup> – and, AFAIK, swank-clojure, in this department)
- all REPLs should be separate processes (I don&#8217;t want a REPL session to hork my main environment)

	+ therefore, all REPLs are fundamentally remote REPLs
	+ ensure that the REPL server library is lean, available, stable, and licensed liberally, so I can roll it into my applications and therefore&#8230;.
	+ connect to running REPL servers on any IP/port from the dev environment, making all REPL-enabled functionality available regardless of where the host REPL process is running

- REPL history persistent across IDE restarts
- ~~Support for multiple REPLs (i.e. load code into the last focused REPL)~~
- ~~Full editor capability in the REPL – highlighting, symbol completion, formatting, etc.~~
- ~~Browsable/searchable REPL history~~

	+ ~~Prior expressions accessible via standard command history (Ctrl-up/down, etc), as well as by clicking on expressions still shown in the REPL window~~

- Runtime namespace browser, with the same usage characteristics as the static one, but tied to the focused REPL
- Configurable pretty-printing of output

	+ This also means being able to &#8220;print&#8221; non-textual data, such as having images drawn inline into the REPL if so desired, etc.
	+ See <a href="http://re-factor.blogspot.com/2010/09/visual-repl.html">Factor&#8217;s REPL</a> for an example

- Ability to check status of and kill long-running REPL invocations
- Optional (likely default) separation of input, *out*, and *err* content
- ~~Automatic generation and configuration of the classpath for local REPLs; this includes (almost certainly in this order!):~~

	+ ~~all current-project source paths (this ensures that changes source files are loaded before same-named files or AOT&#8217;ed classfiles from the project&#8217;s artifact(s))~~
	+ ~~all <strong>other</strong> projects&#8217; source paths (this allows me to load changes I&#8217;ve made to code across my &#8220;main&#8221; project&#8217;s dependencies)~~
	+ ~~all project dependencies (no-brainer)~~
	+ ~~the project&#8217;s artifact(s) (or perhaps <tt>target/classes</tt> (in the case of maven projects) is enough – obviously necessary in order to have access to classfiles from other languages/tools that generate them)~~

- ~~&#8220;Zero-config&#8221;~~

	+ ~~All clojure projects (whether maven- or lein-based) have clojure as a dependency, so requiring any special &#8220;clojure setup&#8221; or creation of a &#8220;clojure platform&#8221; is either pointless or dangerous (the latter if you are unwittingly using a statically-defined &#8220;clojure platform&#8221; while your project&#8217;s POM is explicitly declaring a different version of clojure as a dependency) <sup> <a href="#fn2"> 2 </a> </sup>
	+ The one area where having a default set of Clojure libraries available (as a &#8220;platform&#8221;, if it must be called that) is to start a REPL for a non-Clojure project, which <strong>is</strong> very convenient.~~
