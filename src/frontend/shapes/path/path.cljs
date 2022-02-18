(ns frontend.shapes.path.path
  (:require [frontend.shapes.protocol :refer [RenderSVG Transform
                                              OnSelect]]
            [frontend.state.core :as state]
            [frontend.utils.styles :refer [styles->svg-attr]]
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
      (let [styles (styles->svg-attr (:styles shape))
            ps (state/get-shapes-by-id-with-override (map :id points))]
        [:g
         [:path (merge {:id id
                        :class (utils/apply-selected-style shape "")
                        :d (points->svg ps closed?)}
                       styles)]]))))

(defn path
  ([] (->Path (utils/new-shape-id) :default [] true))
  ([points] (->Path (utils/new-shape-id) :default points true)))

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
