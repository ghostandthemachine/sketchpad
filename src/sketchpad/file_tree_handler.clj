(ns sketchpad.file-tree-handler)

(defn handle-filetree-double-click
  [e app]
  (let [tree (:docs-tree app)
        path (.getPathForLocation tree (.getX e) (.getY e))]
    (awt-event
      (save-tree-selection tree path))))

(defn handle-single-click [row path app-atom]
    (.setSelectionRow (@app-atom :docs-tree) row))

(defn handle-double-click [row path app-atom]
  (try 
    (let [file (.. path getLastPathComponent getUserObject)
    			proj (.getPathComponent path 1)
    			proj-str (trim-parens (last (string/split (.toString proj) #"   ")))]
      (when (fm/text-file? file) ;; handle if dir is selected instead of file
        (do 
          (new-file-tab! app-atom file proj-str)
          (save-tree-selection tree path))))
    (catch java.lang.NullPointerException e)))
  
(defn handle-right-click [row path app]
  (.setSelectionRow (app :docs-tree) row))
