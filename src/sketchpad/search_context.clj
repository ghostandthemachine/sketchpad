(ns sketchpad.search-context
	(:import (org.fife.ui.rtextarea SearchContext)))

(defn regular-expression?
"Returns whether only \"whole word\" matches should be returned. A match is considered to be \"whole word\" if the character on either side of the matched text is a non-word character, or if there is no character on one side of the word, such as when it's at the beginning or end of a line.\n
\tReturns:\n
\tWhether only \"whole word\" matches should be returned."
  [obj]
	(.isRegularExpression obj))

(defn replace-with!
"Sets the text to replace with, if doing a replace operation.\n
Parameters:\n
\treplaceWith - The text to replace with."
	[obj x]
	(.setReplaceWith obj x))

(defn search-for!
"Sets the text to search for.\n
Parameters:\n
\tsearchFor - The text to search for."
	[obj x]
	(.setSearchFor obj x))

(defn regular-expression!
"Sets whether a regular expression search should be done.\n
Parameters:\n
\tregex - Whether a regular expression search should be done."
	[obj x]
	(.setRegularExpression obj x))

(defn search-selection-only!
"Sets whether only the selected text should be searched. This flag is currently not supported.\n
Parameters:\n
\tselectionOnly - Whether only selected text should be searched."
	[obj x]
	(.setSearchSelectionOnly obj x))

(defn replace-with
  "Sets the text to replace with, if doing a replace operation.\n
  Parameters:\n
  \treplaceWith - The text to replace with."
	[obj]
	(.getReplaceWith obj))

(defn search-selection-only?
  "Sets whether only the selected text should be searched. This flag is currently not supported.\n
  Parameters:\n
  \tselectionOnly - Whether only selected text should be searched."
	[obj]
	(.getSearchSelectionOnly obj))

(defn search-forward!
  "Sets whether the search should be forward through the text (vs. backwards).\n
  Parameters:\n
  \tforward - Whether we should search forwards."
	[obj x]
	(.setSearchForward obj x))

(defn whole-word!
  "Sets whether only \"whole word\" matches should be returned. A match is considered to be \"whole word\" if the character on either side of the matched text is a non-word character, or if there is no character on one side of the word, such as when it's at the beginning or end of a line.\n
  Parameters:\n
  \twholeWord - Whether only \"whole word\" matches should be returned."
	[obj x]
	(.setWholeWord obj x))

(defn match-case!
  "Sets whether case should be honored while searching.\n
  Parameters:\n
  \tmatchCase - Whether case should be honored."
	[obj x]
	(.setMatchCase obj x))

(defn whole-word?
  "Returns whether only \"whole word\" matches should be returned. A match is considered to be \"whole word\" if the character on either side of the matched text is a non-word character, or if there is no character on one side of the word, such as when it's at the beginning or end of a line.\n
  Returns:\n
  \tWhether only \"whole word\" matches should be returned."
	[obj]
	(.getWholeWord obj))

(defn search-forward?
  "Returns whether the search should be forward through the text (vs. backwards).\n
  Returns:\n
  \tWhether we should search forwards."
	[obj]
	(.getSearchForward obj))

(defn search-for
  "Sets the text to search for.\n
  Parameters:\n
  \tsearchFor - The text to search for."
	[obj]
	(.getSearchFor obj))

(defn match-case?
  "Returns whether case should be honored while searching.\n
  Returns:\n
  \tWhether case should be honored."
	[obj]
	(.getMatchCase obj))

(defn search-context
  "Creates a new search context. Specifies a forward search, case-insensitive, not whole-word, not a regular expression."
	[]
	(SearchContext. ))

(defn search-context
  "Creates a new search context. Specifies a forward search, case-insensitive, not whole-word, not a regular expression.\n
  Parameters\n
  \tsearchFor - The text to search for."
  [x]
  (SearchContext. x))