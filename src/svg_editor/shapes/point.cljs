(ns svg-editor.shapes.point
  (:require [svg-editor.shapes.protocol :refer [RenderSVG Transform]]
            [svg-editor.math :refer [v+]]
            [svg-editor.shapes.utils :as utils]))

(defrecord Point [id pos r]

  Transform
  (translate [shape vect]
    (assoc shape :pos (v+ pos vect)))

  (scale [shape vect]
    shape)

  RenderSVG
  (render-svg [shape _]
    (let [[x y] pos]
      [:circle (merge {:id id
                       :fill "blue"
                       :cx x
                       :cy y
                       :r r}
                      (utils/apply-selected-style shape))])))

(defn point [pos r]
  (Point. (utils/new-shape-id) pos r))