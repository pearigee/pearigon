(ns svg-editor.tools.grab
  (:require [svg-editor.state :as state]
            [svg-editor.vector :refer [v+]]))

(defn- grab-mousemove [state mouse-state]
  (let [grab (state/get-tool state)
        offset [(- (:page-x mouse-state) (:x grab))
                (- (:page-y mouse-state) (:y grab))]]
    (state/map-shapes
     state
     #(if (:selected %)
        (merge % {:offset offset})
        %))))

(defn- grab-click [state]
  (state/map-shapes
   state
   #(if (:selected %)
      (merge % {:pos (v+ (:pos %) (:offset %))
                :offset [0 0]})
      %))
  (state/set-tool state nil))

(defn grab [state initial-mouse-state]
  (state/set-tool state {:type :grab
                         :on-mousemove grab-mousemove
                         :on-click grab-click
                         :x (:page-x initial-mouse-state)
                         :y (:page-y initial-mouse-state)}))