(ns frontend.shapes.circle
  (:require [frontend.shapes.protocol :refer [RenderSVG Transform]]
            [frontend.state :as state]
            [frontend.math :refer [v+]]
            [frontend.shapes.utils :as utils]))

(defrecord Circle [id mat-id pos r]

  Transform
  (translate [shape vect]
    (assoc shape :pos (v+ pos vect)))

  (scale [shape vect]
    (assoc shape :r (+ r (apply max vect))))

  RenderSVG
  (render-svg [shape]
    (let [materials (state/get-materials)
          {color :color} (get materials mat-id)
          [x y] pos]
      [:circle {:id id
                :fill color
                :class (utils/apply-selected-style shape "")
                :cx x
                :cy y
                :r r}])))

(defn circle [pos r]
  (Circle. (utils/new-shape-id) :default pos r))
