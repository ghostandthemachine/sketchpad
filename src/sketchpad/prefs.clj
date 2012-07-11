(ns sketchpad.prefs
  (:import (java.util.prefs Preferences)
           (java.io ByteArrayInputStream ByteArrayOutputStream
                File FilenameFilter StringReader
                ObjectInputStream ObjectOutputStream
                OutputStream Writer PrintStream)))

(def sketchpad-prefs (.. Preferences userRoot
                     (node "clooj") (node "c6833c87-9631-44af-af83-f417028ea7aa")))

(def sketchpad-prefs (.. Preferences userRoot
  (node "sketchpad") (node "c6833c87-9631-44af-af83-f417028ea7aa")))

(defn partition-str [n s]
  (let [l (.length s)]
    (for [i (range 0 l n)]
      (.substring s i (Math/min l (+ (int i) (int n))))))) 

(def pref-max-bytes (* 3/4 Preferences/MAX_VALUE_LENGTH))

(defn write-value-to-prefs
  "Writes a pure clojure data structure to Preferences object."
  [prefs key value]
  (let [chunks (partition-str pref-max-bytes (with-out-str (pr value)))
        node (. prefs node key)]
    (.clear node)
    (doseq [i (range (count chunks))]
      (. node put (str i) (nth chunks i)))))

(defn read-value-from-prefs
  "Reads a pure clojure data structure from Preferences object."
  [prefs key]
  (when-not (.endsWith key "/")
    (let [node (. prefs node key)]
      (let [s (apply str
                     (for [i (range (count (. node keys)))]
                       (.get node (str i) nil)))]
        (when (and s (pos? (.length s))) (read-string s))))))

(defn write-obj-to-prefs
  "Writes a java object to a Preferences object."
  [prefs key obj]
  (let [bos (ByteArrayOutputStream.)
        os (ObjectOutputStream. bos)
        node (. prefs node key)]
    (.writeObject os obj)
    (. node putByteArray "0" (.toByteArray bos))))

(defn read-obj-from-prefs
  "Reads a java object from a Preferences object."
  [prefs key]
  (let [node (. prefs node key)
        bis (ByteArrayInputStream. (. node getByteArray "0" nil))
        os (ObjectInputStream. bis)]
    (.readObject os)))