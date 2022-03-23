(ns pearigon.shapes.path.point
  (:require
   [pearigon.math :as m]
   [pearigon.shapes.protocol :refer [RenderSVG Transform]]
   [pearigon.state.core :as state]
   [pearigon.utils.ids :as ids]
   [pearigon.utils.styles :as styles]))

(defrecord Point [id pos type r]

  Transform
  (transform [shape matrix]
    (assoc shape :pos (m/mv* matrix pos)))

  RenderSVG
  (render-svg [_]
    (let [[x y] pos]
      [:circle {:id id
                :class (styles/apply-selected-style (state/selected? id)
                                                    "point")
                :cx x
                :cy y
                :r r}])))

(defn point
  ([pos type parent-id]
   (->Point (ids/point-id parent-id) pos type 5))
  ([pos type] (point pos type "")))
