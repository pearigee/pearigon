(ns svg-editor.tools.scale
  (:require
   [svg-editor.actions :as action]
   [svg-editor.tools.protocol :refer [OnMouseMove OnClick OnKeypress]]
   [svg-editor.shapes.protocol :as shapes]
   [svg-editor.state :as state]
   [svg-editor.math :refer [avg dist v-]]))

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
  (on-mouse-move [t s {mpos :pos}]
    (state/map-selected-shapes-preview!
     s
     #(shapes/scale % (compute-scale t mpos))))

  OnClick
  (on-click [t s {mpos :pos}]
    (state/map-selected-shapes!
     s
     #(shapes/scale % (compute-scale t mpos)))
    (state/clear-shape-preview! s)
    (state/pop-tool! s))

  OnKeypress
  (on-keypress [t s k]
    (let [axis (condp = k
                 (action/get-key :scale.x-axis) :x
                 (action/get-key :scale.y-axis) :y
                 nil)]
      (js/console.log "Setting scale axis:" axis)
      (state/update-tool! s (merge t {:axis axis})))))

(defn scale
  [state]
  (let [selection (state/get-selected state)
        center (avg (map :pos selection))
        mpos (state/get-mouse-pos state)]
    (when-not (zero? (count selection))
      (state/push-tool! state (map->ScaleTool
                               {:display "Scale"
                                :action :scale
                                :center center
                                :init-mouse-pos mpos
                                :init-dist (dist center mpos)
                                :axis nil})))))
