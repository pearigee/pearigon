(ns pearigon.shapes.path.point
  (:require
   [pearigon.math :as m]
   [pearigon.shapes.protocol :refer [RenderSVG Transform]]
   [pearigon.utils.ids :as ids]
   [pearigon.utils.styles :as styles]))

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
