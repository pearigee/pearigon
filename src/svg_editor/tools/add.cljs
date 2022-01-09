(ns svg-editor.tools.add
  (:require
    [svg-editor.actions :as actions]
    [svg-editor.shapes :as shapes]
    [svg-editor.state :as state]
    [svg-editor.tools.grab :refer [grab]]))

(defn- add-keypress
  [state key]
  (let [mouse (state/get-mouse-state state)
        {[x y] :pos} mouse
        shape (condp = key
                (actions/get-key :add.rect) (shapes/rect x y 40 40)
                (actions/get-key :add.circle) (shapes/circle x y 20)
                nil)]
    (js/console.log "Shape selected:" shape)
    (if shape
      (do (state/add-shape-and-select! state shape)
          (grab state mouse))
      (state/set-tool! state nil))))

(defn add
  [state]
  (state/set-tool! state {:type :add
                          :display "Add Shape"
                          :on-keypress add-keypress}))
