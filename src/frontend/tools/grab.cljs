(ns frontend.tools.grab
  (:require
   [frontend.state :as state]
   [frontend.tools.protocol :refer [OnMouseMove OnClick]]
   [frontend.shapes.protocol :refer [translate]]
   [frontend.math :refer [v-]]))

(defrecord GrabTool [display action init-mouse-pos]
  OnMouseMove
  (on-mouse-move [_ {pos :pos}]
    (let [offset (v- pos init-mouse-pos)]
      (state/map-selected-shapes-preview! #(translate % offset))))

  OnClick
  (on-click [_ {pos :pos}]
    (let [offset (v- pos init-mouse-pos)]
      (state/map-selected-shapes! #(translate % offset)))
    (state/clear-shape-preview!)
    (state/pop-tool!)))

(defn grab []
  (state/push-tool! (GrabTool. "Grab"
                               :grab
                               (state/get-mouse-pos))))
