(ns svg-editor.tools.grab
  (:require
   [svg-editor.state :as state]
   [svg-editor.tools.protocol :refer [OnMouseMove OnClick]]
   [svg-editor.math :refer [v+ v-]]))

(defrecord GrabTool [display action init-mouse-pos]
  OnMouseMove
  (on-mouse-move [t s {pos :pos}]
    (let [offset (v- pos (:init-mouse-pos t))]
      (state/map-selected-shapes!
       s
       #(merge % {:offset offset}))))

  OnClick
  (on-click [_ s _]
    (state/map-selected-shapes!
     s
     #(merge % {:pos (v+ (:pos %) (:offset %))
                :offset [0 0]}))
    (state/set-tool! s nil)))

(defn grab
  [s]
  (state/set-tool! s (GrabTool. "Grab"
                                :grab
                                (state/get-mouse-pos s))))
