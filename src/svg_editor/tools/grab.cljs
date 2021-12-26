(ns svg-editor.tools.grab
  (:require [svg-editor.state :as state]
            [svg-editor.vector :refer [v+ v-]]))

(defn- grab-mousemove [state]
  (let [{impos :impos} (state/get-tool state)
        {mpos :pos} (state/get-mouse-state state)
        offset (v- mpos impos)]
    (state/map-selected-shapes!
     state
     #(merge % {:offset offset}))))

(defn- grab-click [state]
  (state/map-selected-shapes!
   state
   #(merge % {:pos (v+ (:pos %) (:offset %))
              :offset [0 0]}))
  (state/set-tool! state nil))

(defn grab [state initial-mouse-state]
  (state/set-tool! state {:type :grab
                          :on-mousemove grab-mousemove
                          :on-click grab-click
                          :impos (:pos initial-mouse-state)}))