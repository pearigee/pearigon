(ns svg-editor.shapes.circle
  (:require [svg-editor.shapes.protocol :refer [RenderSVG Transform]]
            [svg-editor.state :as state]
            [svg-editor.math :refer [v+]]
            [svg-editor.shapes.utils :as utils]))

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
