(ns sketchpad.wrapper.search-engine
	(:refer-clojure :exclude [find replace])
	(:import (org.fife.ui.rtextarea SearchEngine)))

(defn find [rta context]
	"Finds the next instance of the string/regular expression specified from the caret position. If a match is found, it is selected in this text area.\n
	Parameters:\n
	\ttextArea - The text area in which to search.\n
	\tcontext - What to search for and all search options\n
	Returns:\n
	\tWhether a match was found (and thus selected).\n
	Throws:\n
	\tjava.util.regex.PatternSyntaxException - If this is a regular expression search but the search text is an invalid regular expression."
	(SearchEngine/find rta context))

(defn get-next-math-pos [search-for search-in forward match-case whole-word]
	"Searches searchIn for an occurrence of searchFor either forwards or backwards, matching case or not.
	Most clients will have no need to call this method directly.

	Parameters:\n
	\tsearchFor - The string to look for.\n
	\tsearchIn - The string to search in.\n
	\tforward - Whether to search forward or backward in searchIn.\n
	\tmatchCase - If true, do a case-sensitive search for searchFor.\n
	\twholeWord - If true, searchFor occurrences embedded in longer words in searchIn don't count as matches.\n
	Returns:\n
	\tThe starting position of a match, or -1 if no match was found.\n"
	(SearchEngine/getNextMatchPos search-for search-in forward match-case whole-word))

(defn get-replacement-text [regex-matcher template]
	"Called internally by getMatches(). This method assumes that the specified matcher has just found a match, and that you want to get the string with which to replace that match.
	Escapes simply insert the escaped character, except for \\n and \\t, which insert a newline and tab respectively. Substrings of the form $\\d+ are considered to be matched groups. To include a literal dollar sign in your template, escape it (i.e. \\$).\n

	Most clients will have no need to call this method directly.\n

	Parameters:\n
	\tm - The matcher.\n
	\ttemplate - The template for the replacement string. For example, \"foo\" would yield the replacement string \"foo\", while \"$1 is the greatest\" would yield different values depending on the value of the first captured group in the match.\n
	Returns:\n
	\tThe string to replace the match with.\n
	Throws:\n
	\tjava.lang.IndexOutOfBoundsException - If template references an invalid group (less than zero or greater than the number of groups matched)."
	(SearchEngine/getReplacementText regex-matcher template))

(defn replace [rta context]
	"Finds the next instance of the text/regular expression specified from the caret position. If a match is found, it is replaced with the specified replacement string.\n
	Parameters:\n
	\ttextArea - The text area in which to search.\n
	\tcontext - What to search for and all search options.\n
	Returns:\n
	\tWhether a match was found (and thus replaced).\n
	Throws:\n
	\tjava.util.regex.PatternSyntaxException - If this is a regular expression search but the search text is an invalid regular expression.\n
	\tjava.lang.IndexOutOfBoundsException - If this is a regular expression search but the replacement text references an invalid group (less than zero or greater than the number of groups matched)."\n
	(SearchEngine/replace rta context))

(defn replace-all [rta context]
	"Replaces all instances of the text/regular expression specified in the specified document with the specified replacement.\n
	Parameters:\n
	\ttextArea - The text area in which to search.\n
	\tcontext - What to search for and all search options.\n
	Throws:\n
	\tjava.util.regex.PatternSyntaxException - If this is a regular expression search but the replacement text is an invalid regular expression.\n
	\tjava.lang.IndexOutOfBoundsException - If this is a regular expression search but the replacement text references an invalid group (less than zero or greater than the number of groups matched)."
	(SearchEngine/replaceAll rta context))
