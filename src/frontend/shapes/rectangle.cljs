(ns frontend.shapes.rectangle
  (:require [frontend.shapes.protocol :refer [RenderSVG Transform]]
            [frontend.math :refer [v+]]
            [frontend.state :as state]
            [frontend.shapes.utils :as utils]))

(defrecord Rectangle [id mat-id pos dim]

  Transform
  (transform [shape _]
    shape)

  (translate [shape vect]
    (assoc shape :pos (v+ pos vect)))

  (scale [shape vect]
    (assoc shape :dim (v+ dim vect)))

  RenderSVG
  (render-svg [shape]
    (let [materials (state/get-materials)
          {color :color} (get materials mat-id)
          [x y] pos
          [w h] dim]
      [:rect {:id id
              :fill color
              :class (utils/apply-selected-style shape "")
              :x (- x (/ w 2))
              :y (- y (/ h 2))
              :width w
              :height h}])))

(defn rectangle [pos dim]
  (Rectangle. (utils/new-shape-id) :default pos dim))
