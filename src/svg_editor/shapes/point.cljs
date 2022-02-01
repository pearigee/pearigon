(ns svg-editor.shapes.point
  (:require [svg-editor.shapes.protocol :refer [RenderSVG Transform]]
            [svg-editor.math :refer [v+]]
            [svg-editor.shapes.utils :as utils]))

(defrecord Point [id pos type r]

  Transform
  (translate [shape vect]
    (assoc shape :pos (v+ pos vect)))

  (scale [shape vect]
    shape)

  RenderSVG
  (render-svg [shape]
    (let [[x y] pos]
      [:circle {:id id
                :class (utils/apply-selected-style shape "point")
                :cx x
                :cy y
                :r r}])))

(defn point [pos type]
  (Point. (utils/new-shape-id) pos type 5))
