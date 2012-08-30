(ns sketchpad.buffer.token
	(:require [sketchpad.util.tab :as tab]
			[seesaw.core :as seesaw])
	(:import (org.fife.ui.rsyntaxtextarea.RSyntaxUtilities)))

(defn token-list-for-line
"Returns the token list for a given line."
	([] (token-list-for-line (tab/current-text-area)))
	([text-area]
	(let [doc (.getDocument text-area)
		line (.getCaretLine text-area)
		token (.getTokenListForLine doc line)]
		token)))

(defn current-token 
"Returns the Token object for the current token."	
	([] (current-token (tab/current-text-area)))
	([text-area]
	(let [doc (.getDocument text-area)
		line (.getCaretLine text-area)
		token (.getTokenListForLine doc line)
		dot (.getCaretPosition text-area)
		cur-token (org.fife.ui.rsyntaxtextarea.RSyntaxUtilities/getTokenAtOffset token dot)]
		cur-token)))

(defn line-token
	([] (line-token (tab/current-text-area)))
	([text-area]
		(let [list-from-line (token-list-for-line text-area)
			token-list (atom [(.getLexeme list-from-line)])]
			(loop [t list-from-line]
				(when-not (nil? (.getNextToken t))
					(swap! token-list (fn [tl] (conj tl (.getLexeme t))))
				 	 (recur (.getNextToken t))))
			@token-list)))

(defn- file-exists?
	[possible-path]
	(let [f (clojure.java.io/file possible-path)]
		(.exists f)))

(defn- is-number?
	[possible-number]
	(=
		(type (load-string possible-number))
		java.lang.Long))

(defn- can-be-opened?
	[line-tokens]
	(and
		(file-exists? (first line-tokens))
		(is-number? (second line-tokens))))
