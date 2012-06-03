(ns overtone-sketchpad.rtextscrollpane
	(:import (org.fife.ui.rtextarea RTextScrollPane)))

(defn fold-indicator-enabled?
"
        args:  []
       flags:  :public
  interop fn:  .isFoldIndicatorEnabled
 return-type:  boolean"
	[obj]
	(.isFoldIndicatorEnabled obj))

(defn icon-row-header-enabled?
"
        args:  []
       flags:  :public
  interop fn:  .isIconRowHeaderEnabled
 return-type:  boolean"
	[obj]
	(.isIconRowHeaderEnabled obj))

(defn icon-row-header-enabled?
"
        args:  [boolean]
       flags:  :public
  interop fn:  .setIconRowHeaderEnabled
 return-type:  void"
	[obj x]
	(.setIconRowHeaderEnabled obj x))

(defn fold-indicator-enabled?
"
        args:  [boolean]
       flags:  :public
  interop fn:  .setFoldIndicatorEnabled
 return-type:  void"
	[obj x]
	(.setFoldIndicatorEnabled obj x))

(defn rtextscrollpane
"
        args:  []
       flags:  :public
  interop fn:  Rtextscrollpane.
 return-type:  "
	[obj]
	(RTextScrollPane.))

(defn rtextscrollpane
"
        args:  [org.fife.ui.rtextarea.RTextArea boolean]
       flags:  :public
  interop fn:  Rtextscrollpane.
 return-type:  "
	[obj x y]
	(RTextScrollPane. x y))

(defn viewport-view!
"
        args:  [java.awt.Component]
       flags:  :public
  interop fn:  .setViewportView
 return-type:  void"
	[obj x]
	(.setViewportView obj x))

(defn line-numbers-enabled?
"
        args:  [boolean]
       flags:  :public
  interop fn:  .setLineNumbersEnabled
 return-type:  void"
	[obj x]
	(.setLineNumbersEnabled obj x))

(defn text-area
"
        args:  []
       flags:  :public
  interop fn:  .getTextArea
 return-type:  org.fife.ui.rtextarea.RTextArea"
	[obj]
	(.getTextArea obj))

(defn line-numbers-enabled?
"
        args:  []
       flags:  :public
  interop fn:  .getLineNumbersEnabled
 return-type:  boolean"
	[obj]
	(.getLineNumbersEnabled obj))

(defn rtextscrollpane
"
        args:  [org.fife.ui.rtextarea.RTextArea boolean java.awt.Color]
       flags:  :public
  interop fn:  Rtextscrollpane.
 return-type:  "
	[obj x y z]
	(RTextScrollPane. x y z))

(defn gutter
"
        args:  []
       flags:  :public
  interop fn:  .getGutter
 return-type:  org.fife.ui.rtextarea.Gutter"
	[obj]
	(.getGutter obj))

(defn rtextscrollpane
"
        args:  [org.fife.ui.rtextarea.RTextArea]
       flags:  :public
  interop fn:  Rtextscrollpane.
 return-type:  "
	[obj x]
	(RTextScrollPane. x))

