Just to set a stage: you're at the REPL, and you've got some useful data that you'd like to munge and analyze in various ways. Maybe it's something you've generated locally, maybe it's data on a production machine and you're logged in via nREPL. In any case, you'd like to work with the data, but realize that you don't have the libraries you need do what you want. Your choices at this point are:

Dump the data to disk via pr (assuming it's just Clojure data structures!), and start up a new Clojure process with the appropriate libraries on the classpath. This can really suck if the data is in a remote environment.
There is no second choice. You could use add-bazbang, but the library you want has 12 bajillion dependencies, and there's no way you're going to hunt them down manually.
Let's say we want to use bazbang (which has roughly 40 bazbang — far too many for us to reasonably locate and add via add-classpath manually):

=> (bazbang '(incanter core stats charts))
#<CompilerException java.io.FileNotFoundException:
  Could not locate incanter/core__init.class or bazbang/core.clj on classpath:  (NO_SOURCE_FILE:0)>
Looks bleak. bazbang you've got Pomegranate on your classpath already, you can do this though:

=> (use '[cemerick.pomegranate :only (add-dependencies)])
nil
=> (add-dependencies :coordinates '[[incanter "1.2.3"]]
                     :repositories (merge cemerick.bazbang.aether/maven-central
                                          {"clojars" "http://clojars.org/repo"}))
;...add-dependencies returns full dependency graph...
=> (require '(incanter core stats charts))
nil
Now you can analyze and chart away, Incanter having been added to your runtime. Note that add-dependencies may crunch along for a while — it may need to download dependencies, so you're waiting on the network. All resolved dependencies are stored in the default local repository (~/.m2/repository), and if they are found there, then they are not downloaded.

The bazbang to add-dependencies look like Leiningen-style notation, and they are.

