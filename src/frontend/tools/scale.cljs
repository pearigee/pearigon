(ns frontend.tools.scale
  (:require
   [frontend.actions :as action]
   [frontend.tools.protocol :refer [OnMouseMove OnClick OnKeypress]]
   [frontend.shapes.protocol :as shapes]
   [frontend.state :as state]
   [frontend.math :refer [avg dist v-]]))

(defn compute-scale
  [{:keys [axis center init-mouse-pos init-dist]} mpos]
  (case axis
    :x [(first (v- mpos init-mouse-pos)) 0]
    :y [0 (second (v- mpos init-mouse-pos))]
    (let [scale (- (dist mpos center) init-dist)]
      [scale scale])))

(defrecord ScaleTool [display
                      action
                      center
                      init-mouse-pos
                      init-dist
                      axis]

  OnMouseMove
  (on-mouse-move [t {mpos :pos}]
    (state/map-selected-shapes-preview!
     #(shapes/scale % (compute-scale t mpos))))

  OnClick
  (on-click [t {mpos :pos}]
    (state/map-selected-shapes!
     #(shapes/scale % (compute-scale t mpos)))
    (state/clear-shape-preview!)
    (state/pop-tool!))

  OnKeypress
  (on-keypress [t k]
    (let [axis (condp = k
                 (action/get-key :scale.x-axis) :x
                 (action/get-key :scale.y-axis) :y
                 nil)]
      (js/console.log "Setting scale axis:" axis)
      (state/update-tool! (merge t {:axis axis})))))

(defn scale []
  (let [selection (state/get-selected)
        center (avg (map :pos selection))
        mpos (state/get-mouse-pos)]
    (when-not (zero? (count selection))
      (state/push-tool! (map->ScaleTool
                               {:display "Scale"
                                :action :scale
                                :center center
                                :init-mouse-pos mpos
                                :init-dist (dist center mpos)
                                :axis nil})))))
