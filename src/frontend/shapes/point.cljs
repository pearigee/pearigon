(ns frontend.shapes.point
  (:require [frontend.shapes.protocol :refer [RenderSVG Transform]]
            [frontend.math :as m]
            [frontend.shapes.utils :as utils]))

(defrecord Point [id pos type r]

  Transform
  (transform [shape matrix]
    (assoc shape :pos (m/mv* matrix pos)))

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
