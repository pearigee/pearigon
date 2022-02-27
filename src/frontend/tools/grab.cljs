(ns frontend.tools.grab
  (:require
   [frontend.math :as m]
   [frontend.shapes.protocol :as shape]
   [frontend.state.core :as state]
   [frontend.state.mouse :as mouse]
   [frontend.state.tools :as tools]
   [frontend.tools.protocol :refer [OnClick OnMouseMove]]))

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
    (tools/pop-tool!)))

(defn grab []
  (tools/push-tool! (->GrabTool "Grab"
                                :grab
                                (mouse/pos))))
