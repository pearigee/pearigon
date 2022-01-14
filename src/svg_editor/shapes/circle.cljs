(ns svg-editor.shapes.circle
  (:require [svg-editor.shapes.protocol :refer [RenderSVG Transform]]
            [svg-editor.math :refer [v+]]
            [svg-editor.shapes.utils :as utils]))

(defrecord Circle [id mat-id pos r]

  Transform
  (translate [shape vect]
    (assoc shape :pos (v+ pos vect)))

  (scale [shape vect]
    (assoc shape :r (+ r (apply max vect))))

  RenderSVG
  (render-svg [shape materials]
    (let [{color :color} (get materials mat-id)
          [x y] pos]
      [:circle (merge {:id id
                       :fill color
                       :cx x
                       :cy y
                       :r r}
                      (utils/apply-selected-style shape))])))

(defn circle [pos r]
  (Circle. (utils/new-shape-id) :default pos r))
