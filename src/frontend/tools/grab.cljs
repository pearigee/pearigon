(ns frontend.tools.grab
  (:require
   [frontend.state.core :as state]
   [frontend.tools.protocol :refer [OnMouseMove OnClick]]
   [frontend.shapes.protocol :as shape]
   [frontend.math :as m]))

(defn- compute-transform [pos init-mouse-pos]
  (let [[x y] (m/v- pos init-mouse-pos)]
    (m/translate x y)))

(defrecord GrabTool [display action init-mouse-pos]
  OnMouseMove
  (on-mouse-move [_ {pos :pos}]
    (state/map-selected-shapes-preview!
     #(shape/transform % (compute-transform pos init-mouse-pos))))

  OnClick
  (on-click [_ {pos :pos}]
    (state/map-selected-shapes!
     #(shape/transform % (compute-transform pos init-mouse-pos)))
    (state/clear-shape-preview!)
    (state/pop-tool!)))

(defn grab []
  (state/push-tool! (->GrabTool "Grab"
                                :grab
                                (state/get-mouse-pos))))
