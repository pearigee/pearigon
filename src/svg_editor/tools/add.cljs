(ns svg-editor.tools.add
  (:require [svg-editor.state :as state]
            [svg-editor.shapes :as shapes]
            [svg-editor.tools.grab :refer [grab]]))

(defn- add-keypress [state key]
  (let [mouse (state/get-mouse-state state)
        {[x y] :pos} mouse
        shape (case key
                :r (shapes/rect x y 40 40)
                :c (shapes/circle x y 20)
                nil)]
    (js/console.log "Shape selected:" shape)
    (if shape
      (do (state/add-shape-and-select! state shape)
          (grab state mouse))
      (state/set-tool! state nil))))

(defn add [state]
  (state/set-tool! state {:type :add-shape
                         :on-keypress add-keypress}))