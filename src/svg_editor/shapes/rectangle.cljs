(ns svg-editor.shapes.rectangle
  (:require [svg-editor.shapes.protocol :refer [RenderSVG Transform]]
            [svg-editor.math :refer [v+]]
            [svg-editor.state :as state]
            [svg-editor.shapes.utils :as utils]))

(defrecord Rectangle [id mat-id pos dim]

  Transform
  (translate [shape vect]
    (assoc shape :pos (v+ pos vect)))

  (scale [shape vect]
    (assoc shape :dim (v+ dim vect)))

  RenderSVG
  (render-svg [shape s]
    (let [materials (state/get-materials s)
          {color :color} (get materials mat-id)
          [x y] pos
          [w h] dim]
      [:rect (merge {:id id
                     :fill color
                     :x (- x (/ w 2))
                     :y (- y (/ h 2))
                     :width w
                     :height h}
                    (utils/apply-selected-style shape))])))

(defn rectangle [pos dim]
  (Rectangle. (utils/new-shape-id) :default pos dim))
