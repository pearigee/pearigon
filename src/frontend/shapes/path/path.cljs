(ns frontend.shapes.path.path
  (:require [frontend.shapes.protocol :refer [RenderSVG Transform
                                              OnSelect]]
            [frontend.state.core :as state]
            [frontend.math :as m]
            [frontend.shapes.path.point :refer [point]]
            [frontend.shapes.utils :as utils]
            [frontend.shapes.path.svg :refer [points->svg]]))

(defrecord Path [id mat-id points closed?]

  Transform
  (transform [shape _]
    shape)

  OnSelect
  (on-select [_]
    (state/map-shape-ids! (into #{} (map :id points))
                          #(assoc % :selected true)))

  RenderSVG
  (render-svg [shape]
    (when-not (empty? points)
      (let [materials (state/get-materials)
            {:keys [color]} (get materials mat-id)
            ps (state/get-shapes-by-id-with-override (map :id points))]
        [:g
         [:path {:id id
                 :class (utils/apply-selected-style shape "")
                 :fill color
                 :d (points->svg ps closed?)}]]))))

(defn path
  ([] (Path. (utils/new-shape-id) :default [] true))
  ([points] (Path. (utils/new-shape-id) :default points true)))

(defn circle [pos size]
  (path
   (mapv
    #(assoc % :pos (m/v+ (:pos %) pos))
    (let [res 8
          step (/ (* 2 m/pi) res)]
      (for [p (range res)]
        (let [angle (* p step)]
          (point [(* size (m/cos angle))
                  (* size (m/sin angle))]
                 :round)))))))

(defn rectangle [pos size]
  (path
   (mapv
    #(assoc % :pos (m/v+ (:pos %) pos))
    [(point [(- size) size] :sharp)
     (point [size size] :sharp)
     (point [size (- size)] :sharp)
     (point [(- size) (- size)] :sharp)])))
