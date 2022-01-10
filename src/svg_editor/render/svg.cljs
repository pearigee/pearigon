(ns svg-editor.render.svg
  (:require
    [svg-editor.math :refer [v+]]))

(defn apply-selected-style
  [shape]
  (if (:selected shape)
    {:stroke "#1fd2ff"
     :stroke-width  "3px"
     :stroke-dasharray "4"}
    {}))

(defn shape->svg
  [shape materials]
  (let [{pos :pos
         offset :offset
         material-id :material
         type :type
         id :id} shape
        {color :color} (material-id materials)
        [x y] pos
        [ox oy] offset]
    (case type
      :circle [:circle (merge {:id id
                               :fill color
                               :cx (+ x ox)
                               :cy (+ y oy)
                               :r (+ (:r shape) (apply max (:offset-scale shape)))}
                              (apply-selected-style shape))]
      :rect [:rect (let [[w h] (v+ (:dim shape) (:offset-scale shape))]
                     (merge {:id id
                             :fill color
                             :x (- (+ x ox) (/ w 2))
                             :y (- (+ y oy) (/ h 2))
                             :width w
                             :height h}
                            (apply-selected-style shape)))])))
