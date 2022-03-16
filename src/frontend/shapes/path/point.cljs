(ns frontend.shapes.path.point
  (:require
   [frontend.math :as m]
   [frontend.shapes.protocol :refer [RenderSVG Transform]]
   [frontend.utils.ids :as ids]
   [frontend.utils.styles :as styles]))

(defrecord Point [id pos type r]

  Transform
  (transform [shape matrix]
    (assoc shape :pos (m/mv* matrix pos)))

  RenderSVG
  (render-svg [shape]
    (let [[x y] pos]
      [:circle {:id id
                :class (styles/apply-selected-style shape "point")
                :cx x
                :cy y
                :r r}])))

(defn point [pos type]
  (->Point (ids/shape-id) pos type 5))
