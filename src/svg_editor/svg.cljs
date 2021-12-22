(ns svg-editor.svg
  (:require [svg-editor.vector :refer [v+]]))

(defn apply-selected-style [shape]
  (if (:selected shape)
    {:stroke "#1fd2ff"
     :stroke-width  "3px"
     :stroke-dasharray "4"}
    {}))

(defn shape->svg [shape]
  (let [{pos :pos
         offset :offset
         type :type
         id :id} shape
        [x y] pos
        [ox oy] offset]
    (case type
      :circle [:circle (merge {:id id
                               :cx (+ x ox)
                               :cy (+ y oy)
                               :r (+ (:r shape) (apply max (:offset-scale shape)))}
                              (apply-selected-style shape))]
      :rect [:rect (let [[w h] (v+ (:dim shape) (:offset-scale shape))]
                     (merge {:id id
                             :x (- (+ x ox) (/ w 2))
                             :y (- (+ y oy) (/ h 2))
                             :width w
                             :height h}
                            (apply-selected-style shape)))])))