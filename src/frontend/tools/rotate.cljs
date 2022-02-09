(ns frontend.tools.rotate
  (:require
   [frontend.tools.protocol :refer [OnMouseMove OnClick]]
   [frontend.shapes.protocol :as shapes]
   [frontend.state :as state]
   [frontend.math :as m]))

(defn calc-angle [[x y]]
  (m/atan2 y x))

(defn compute-transform [mpos origin init-angle]
  (let [rot-matrix (m/rotate (- init-angle (calc-angle (m/v- origin mpos))))
        [cx cy] origin]
    ;; Translate back to [0 0] so the rotate is applied from the origin.
    (m/transform (m/translate (- cx) (- cy))
                 rot-matrix
                 (m/translate cx cy))))

(defrecord RotateTool [display
                       action
                       origin
                       init-angle]

  OnMouseMove
  (on-mouse-move [_ {mpos :pos}]
    (state/map-selected-shapes-preview!
     #(shapes/transform % (compute-transform mpos origin init-angle))))

  OnClick
  (on-click [_ {mpos :pos}]
    (state/map-selected-shapes!
     #(shapes/transform % (compute-transform mpos origin init-angle)))
    (state/clear-shape-preview!)
    (state/pop-tool!)))

(defn rotate []
  (let [selection (state/get-selected)
        ;; Filter out shapes with no position (paths)
        origin (m/avg (filter identity (map :pos selection)))
        mpos (state/get-mouse-pos)]
    (when-not (zero? (count selection))
      (state/push-tool! (map->RotateTool
                         {:display "Rotate"
                          :action :rotate
                          :origin origin
                          :init-angle (calc-angle (m/v- origin mpos))})))))
