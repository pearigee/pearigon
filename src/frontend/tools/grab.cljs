(ns frontend.tools.grab
  (:require
   [frontend.math :as m]
   [frontend.shapes.protocol :as shape]
   [frontend.state.core :as state]
   [frontend.state.mouse :as mouse]
   [frontend.state.tools :as tools]
   [frontend.state.actions :as actions]
   [frontend.tools.protocol :refer [OnClick OnMouseMove OnKeypress]]))

(defn- compute-transform [pos init-mouse-pos axis]
  (let [[x y] (m/v- pos init-mouse-pos)]
    (case axis
      :x (m/translate x 0)
      :y (m/translate 0 y)
      (m/translate x y))))

(defrecord GrabTool [display action init-mouse-pos axis]
  OnKeypress
  (on-keypress [t k]
    (let [axis (cond
                 (actions/active? :lock-x-axis k) :x
                 (actions/active? :lock-y-axis k) :y
                 :else nil)]
      (when axis
        (tools/update-tool! (merge t {:axis axis})))))

  OnMouseMove
  (on-mouse-move [_ {pos :pos}]
    (state/map-selected-shapes-preview!
     #(shape/transform % (compute-transform pos init-mouse-pos axis))))

  OnClick
  (on-click [_ {pos :pos}]
    (state/map-selected-shapes!
     #(shape/transform % (compute-transform pos init-mouse-pos axis)))
    (state/clear-shape-preview!)
    (tools/pop-tool!)))

(defn grab []
  (tools/push-tool! (->GrabTool "Grab"
                                :grab
                                (mouse/pos)
                                nil)))
