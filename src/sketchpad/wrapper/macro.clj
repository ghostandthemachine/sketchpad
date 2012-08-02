(ns sketchpad.wrapper.macro
  (:import (org.fife.ui.rtextarea Macro)))

(defn macro
"
        args:  [java.lang.String java.util.List]
       flags:  :public
  interop fn:  Macro.
 return-type:  "
  [obj x y]
  (Macro. x y))

(defn save-to-file!
"
        args:  [java.lang.String]
       flags:  :public
  interop fn:  .saveToFile
 return-type:  void"
  [obj x]
  (.saveToFile obj x))

(defn macro
"
        args:  []
       flags:  :public
  interop fn:  Macro.
 return-type:  "
  [obj]
  (Macro. ))

(defn add-macro-record!
"
        args:  [org.fife.ui.rtextarea.Macro$MacroRecord]
       flags:  :public
  interop fn:  .addMacroRecord
 return-type:  void"
  [obj x]
  (.addMacroRecord obj x))

(defn macro
"
        args:  [java.io.File]
       flags:  :public
  interop fn:  Macro.
 return-type:  "
  [obj x]
  (Macro. x))

(defn name!
"
        args:  [java.lang.String]
       flags:  :public
  interop fn:  .setName
 return-type:  void"
  [obj x]
  (.setName obj x))

(defn macro
"
        args:  [java.lang.String]
       flags:  :public
  interop fn:  Macro.
 return-type:  "
  [obj x]
  (Macro. x))

(defn macro-records
"
        args:  []
       flags:  :public
  interop fn:  .getMacroRecords
 return-type:  java.util.List"
  [obj]
  (.getMacroRecords obj))

(defn name
"
        args:  []
       flags:  :public
  interop fn:  .getName
 return-type:  java.lang.String"
  [obj]
  (.getName obj))

