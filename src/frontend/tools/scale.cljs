(ns frontend.tools.scale
  (:require
   [frontend.input.hotkeys :as hotkeys]
   [frontend.tools.protocol :refer [OnMouseMove OnClick OnKeypress]]
   [frontend.shapes.protocol :as shapes]
   [frontend.state.core :as state]
   [frontend.math :as m]))

(defn tune
  "Adjust the scale multiplier for usability.

  The scale should always start at 1 (i.e. start at the current scale).
  We multiply by 0.01 to scale down pixels moved to a reasonable adjustment."
  [x]
  (+ 1 (* x 0.01)))

(defn compute-scale
  "Build the scale matrix from the tool state and mouse position."
  [{:keys [axis center init-mouse-pos init-dist]} mpos]
  (case axis
    :x (m/scale (tune (first (m/v- mpos init-mouse-pos))) 1)
    :y (m/scale  1 (tune (second (m/v- mpos init-mouse-pos))))
    (let [scale (tune (- (m/dist mpos center) init-dist))]
      (m/scale scale scale))))

(defn compute-transform [tool mpos origin]
  (let [scale-matrix (compute-scale tool mpos)
        [cx cy] origin]
    ;; Translate back to [0 0] so the scale is applied from the origin.
    (m/transform (m/translate (- cx) (- cy))
                 scale-matrix
                 (m/translate cx cy))))

(defrecord ScaleTool [display
                      action
                      center
                      init-mouse-pos
                      init-dist
                      axis]

  OnMouseMove
  (on-mouse-move [t {mpos :pos}]
    (state/map-selected-shapes-preview!
     #(shapes/transform % (compute-transform t mpos center))))

  OnClick
  (on-click [t {mpos :pos}]
    (state/map-selected-shapes!
     #(shapes/transform % (compute-transform t mpos center)))
    (state/clear-shape-preview!)
    (state/pop-tool!))

  OnKeypress
  (on-keypress [t k]
    (let [axis (cond
                 (hotkeys/active? :scale.x-axis k) :x
                 (hotkeys/active? :scale.y-axis k) :y
                 :else nil)]
      (js/console.log "Setting scale axis:" axis k)
      (when axis
        (state/update-tool! (merge t {:axis axis}))))))

(defn scale []
  (let [selection (state/get-selected)
        ;; Filter out shapes with no position (paths)
        center (m/avg (filter identity (map :pos selection)))
        mpos (state/get-mouse-pos)]
    (when-not (zero? (count selection))
      (state/push-tool! (map->ScaleTool
                         {:display "Scale"
                          :action :scale
                          :center center
                          :init-mouse-pos mpos
                          :init-dist (m/dist center mpos)
                          :axis nil})))))
