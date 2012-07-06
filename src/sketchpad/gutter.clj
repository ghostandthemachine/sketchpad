(ns sketchpad.gutter
	(:import (org.fife.ui.rtextarea Gutter)))

(defn line-numbering-start-index
"
        args:  []
       flags:  :public
  interop fn:  .getLineNumberingStartIndex
 return-type:  int"
	[obj]
	(.getLineNumberingStartIndex obj))

(defn active-line-range-color
"
        args:  []
       flags:  :public
  interop fn:  .getActiveLineRangeColor
 return-type:  java.awt.Color"
	[obj]
	(.getActiveLineRangeColor obj))

(defn bookmarks
"
        args:  []
       flags:  :public
  interop fn:  .getBookmarks
 return-type:  org.fife.ui.rtextarea.GutterIconInfo<>"
	[obj]
	(.getBookmarks obj))

(defn line-number-font!
"
        args:  [java.awt.Font]
       flags:  :public
  interop fn:  .setLineNumberFont
 return-type:  void"
	[obj x]
	(.setLineNumberFont obj x))

(defn toggle-bookmark?
"
        args:  [int]
       flags:  :public
  interop fn:  .toggleBookmark
 return-type:  boolean"
	[obj x]
	(.toggleBookmark obj x))

(defn line-numbers-enabled?
"
        args:  []
       flags:  :public
  interop fn:  .getLineNumbersEnabled
 return-type:  boolean"
	[obj]
	(.getLineNumbersEnabled obj))

(defn text-area!
"
        args:  [org.fife.ui.rtextarea.RTextArea]
       flags:  
  interop fn:  .setTextArea
 return-type:  void"
	[obj x]
	(.setTextArea obj x))

(defn line-number-color
"
        args:  []
       flags:  :public
  interop fn:  .getLineNumberColor
 return-type:  java.awt.Color"
	[obj]
	(.getLineNumberColor obj))

(defn show-collapsed-region-tool-tips?
"
        args:  []
       flags:  :public
  interop fn:  .getShowCollapsedRegionToolTips
 return-type:  boolean"
	[obj]
	(.getShowCollapsedRegionToolTips obj))

(defn default-active-line-range-color
"
        args:  
       flags:  :static :public :final
  interop fn:  .DEFAULT_ACTIVE_LINE_RANGE_COLOR
 return-type:  "
	[obj]
	(org.fife.ui.rtextarea.Gutter/DEFAULT_ACTIVE_LINE_RANGE_COLOR))

(defn icon-row-header-enabled?
"
        args:  [boolean]
       flags:  
  interop fn:  .setIconRowHeaderEnabled
 return-type:  void"
	[obj x]
	(.setIconRowHeaderEnabled obj x))

(defn fold-indicator-foreground
"
        args:  []
       flags:  :public
  interop fn:  .getFoldIndicatorForeground
 return-type:  java.awt.Color"
	[obj]
	(.getFoldIndicatorForeground obj))

(defn active-line-range-color!
"
        args:  [java.awt.Color]
       flags:  :public
  interop fn:  .setActiveLineRangeColor
 return-type:  void"
	[obj x]
	(.setActiveLineRangeColor obj x))

(defn fold-background
"
        args:  []
       flags:  :public
  interop fn:  .getFoldBackground
 return-type:  java.awt.Color"
	[obj]
	(.getFoldBackground obj))

(defn remove-all-tracking-icons
"
        args:  []
       flags:  :public
  interop fn:  .removeAllTrackingIcons
 return-type:  void"
	[obj]
	(.removeAllTrackingIcons obj))

(defn bookmarking-enabled?
"
        args:  []
       flags:  :public
  interop fn:  .isBookmarkingEnabled
 return-type:  boolean"
	[obj]
	(.isBookmarkingEnabled obj))

(defn border-color
"
        args:  []
       flags:  :public
  interop fn:  .getBorderColor
 return-type:  java.awt.Color"
	[obj]
	(.getBorderColor obj))

(defn fold-icons!
"
        args:  [javax.swing.Icon javax.swing.Icon]
       flags:  :public
  interop fn:  .setFoldIcons
 return-type:  void"
	[obj x y]
	(.setFoldIcons obj x y))

(defn add-line-tracking-icon!
"
        args:  [int javax.swing.Icon]
       flags:  :public
  interop fn:  .addLineTrackingIcon
 return-type:  org.fife.ui.rtextarea.GutterIconInfo"
	[obj x y]
	(.addLineTrackingIcon obj x y))

(defn icon-row-header-enabled?
"
        args:  []
       flags:  :public
  interop fn:  .isIconRowHeaderEnabled
 return-type:  boolean"
	[obj]
	(.isIconRowHeaderEnabled obj))

(defn remove-tracking-icon
"
        args:  [org.fife.ui.rtextarea.GutterIconInfo]
       flags:  :public
  interop fn:  .removeTrackingIcon
 return-type:  void"
	[obj x]
	(.removeTrackingIcon obj x))

(defn fold-indicator-foreground!
"
        args:  [java.awt.Color]
       flags:  :public
  interop fn:  .setFoldIndicatorForeground
 return-type:  void"
	[obj x]
	(.setFoldIndicatorForeground obj x))

(defn fold-indicator-enabled?
"
        args:  []
       flags:  :public
  interop fn:  .isFoldIndicatorEnabled
 return-type:  boolean"
	[obj]
	(.isFoldIndicatorEnabled obj))

(defn fold-background!
"
        args:  [java.awt.Color]
       flags:  :public
  interop fn:  .setFoldBackground
 return-type:  void"
	[obj x]
	(.setFoldBackground obj x))

(defn line-numbers-enabled
"
        args:  [boolean]
       flags:  
  interop fn:  .setLineNumbersEnabled
 return-type:  void"
	[obj x]
	(.setLineNumbersEnabled obj x))

(defn line-number-color!
"
        args:  [java.awt.Color]
       flags:  :public
  interop fn:  .setLineNumberColor
 return-type:  void"
	[obj x]
	(.setLineNumberColor obj x))

(defn add-offset-tracking-icon!
"
        args:  [int javax.swing.Icon]
       flags:  :public
  interop fn:  .addOffsetTrackingIcon
 return-type:  org.fife.ui.rtextarea.GutterIconInfo"
	[obj x y]
	(.addOffsetTrackingIcon obj x y))

(defn bookmark-icon!
"
        args:  [javax.swing.Icon]
       flags:  :public
  interop fn:  .setBookmarkIcon
 return-type:  void"
	[obj x]
	(.setBookmarkIcon obj x))

(defn fold-indicator-enabled?
"
        args:  [boolean]
       flags:  :public
  interop fn:  .setFoldIndicatorEnabled
 return-type:  void"
	[obj x]
	(.setFoldIndicatorEnabled obj x))

(defn component-orientation!
"
        args:  [java.awt.ComponentOrientation]
       flags:  :public
  interop fn:  .setComponentOrientation
 return-type:  void"
	[obj x]
	(.setComponentOrientation obj x))

(defn gutter
"
        args:  [org.fife.ui.rtextarea.RTextArea]
       flags:  :public
  interop fn:  Gutter.
 return-type:  "
	[obj x]
	(Gutter. x))

(defn bookmark-icon
"
        args:  []
       flags:  :public
  interop fn:  .getBookmarkIcon
 return-type:  javax.swing.Icon"
	[obj]
	(.getBookmarkIcon obj))

(defn line-numbering-start-index!
"
        args:  [int]
       flags:  :public
  interop fn:  .setLineNumberingStartIndex
 return-type:  void"
	[obj x]
	(.setLineNumberingStartIndex obj x))

(defn bookmarking-enabled?
"
        args:  [boolean]
       flags:  :public
  interop fn:  .setBookmarkingEnabled
 return-type:  void"
	[obj x]
	(.setBookmarkingEnabled obj x))

(defn tracking-icons
"
        args:  [java.awt.Point]
       flags:  :public
  interop fn:  .getTrackingIcons
 return-type:  java.lang.Object<>"
	[obj x]
	(.getTrackingIcons obj x))

(defn line-number-font
"
        args:  []
       flags:  :public
  interop fn:  .getLineNumberFont
 return-type:  java.awt.Font"
	[obj]
	(.getLineNumberFont obj))

(defn border-color!
"
        args:  [java.awt.Color]
       flags:  :public
  interop fn:  .setBorderColor
 return-type:  void"
	[obj x]
	(.setBorderColor obj x))

(defn show-collapsed-region-tool-tips?
"
        args:  [boolean]
       flags:  :public
  interop fn:  .setShowCollapsedRegionToolTips
 return-type:  void"
	[obj x]
	(.setShowCollapsedRegionToolTips obj x))



