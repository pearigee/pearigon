(ns svg-editor.tools.scale
  (:require
   [svg-editor.actions :as action]
   [svg-editor.tools.protocol :refer [OnMouseMove OnClick OnKeypress]]
   [svg-editor.state :as state]
   [svg-editor.math :refer [avg dist v+ v-]]))

(defn- apply-scale
  [shape]
  (case (:type shape)
    :rect (merge shape {:dim (v+ (:dim shape) (:offset-scale shape))
                        :offset-scale [0 0]})
    :circle (merge shape {:r (+ (:r shape)
                                (apply max (:offset-scale shape)))
                          :offset-scale [0 0]})
    shape))

(defrecord ScaleTool [display
                      action
                      center
                      init-mouse-pos
                      init-dist
                      axis]

  OnClick
  (on-click [_ s _]
    (state/map-selected-shapes! s #(apply-scale %))
    (state/set-tool! s nil))

  OnMouseMove
  (on-mouse-move [{init-dist :init-dist
                   center :center
                   init-mouse-pos :init-mouse-pos
                   axis :axis}
                  s
                  {mpos :pos}]
    (state/map-selected-shapes!
     s
     #(merge % {:offset-scale
                (case axis
                  :x [(first (v- mpos init-mouse-pos)) 0]
                  :y [0 (second (v- mpos init-mouse-pos))]
                  (let [scale (- (dist mpos center) init-dist)]
                    [scale scale]))})))

  OnKeypress
  (on-keypress [t s k]
    (let [axis (condp = k
                 (action/get-key :scale.x-axis) :x
                 (action/get-key :scale.y-axis) :y
                 nil)]
      (js/console.log "Setting scale axis:" axis)
      (state/set-tool! s (merge t {:axis axis})))))

(defn scale
  [state]
  (let [selection (state/get-selected state)
        center (avg (map :pos selection))
        mpos (state/get-mouse-pos state)]
    (when-not (zero? (count selection))
      (state/set-tool! state (map->ScaleTool
                              {:display "Scale"
                               :action :scale
                               :center center
                               :init-mouse-pos mpos
                               :init-dist (dist center mpos)
                               :axis nil})))))
