(ns overtone-sketchpad.theme
    (:import (org.fife.ui.rsyntaxtextarea Theme))
  	(:require [clojure.java.io :as io]))

(defn theme
"Loads a theme.\n
Parameters: in - The input stream to read from. This will be closed when this method returns\n
Returns: The theme.\n
Throws:	java.io.IOException - If an IO error occurs.\n
See Also: save(OutputStream)"
	[file-name]
	(Theme/load (io/input-stream file-name)))

(defn apply!
"Applies this theme to a text area.\n
Parameters: textArea - The text area to apply this theme to."
	[theme rtextarea]
	(.apply theme rtextarea))

(defn save!
"Saves this theme to an output stream.\n
Parameters: out - The output stream to write to.\n
Throws: java.io.IOException - If an IO error occurs.\n
See Also: load(InputStream)\n"
	[theme file-name]
	(.save theme (io/output-stream file-name)))