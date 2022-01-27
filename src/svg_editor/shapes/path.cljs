(ns svg-editor.shapes.path
  (:require [svg-editor.shapes.protocol :refer [RenderSVG Transform
                                                render-svg
                                                OnSelect]]
            [svg-editor.state :as state]
            [svg-editor.math :refer [avg]]
            [svg-editor.shapes.utils :as utils]))

(defn point->svg [points]
  (let [[{[x1 y1] :pos t1 :type}
         {[x2 y2] :pos t2 :type}] points]
    (cond (= t1 t2 :round)
          (let [[xa ya] (avg [[x1 y1] [x2 y2]])]
            [1 (str "Q " x1 " " y1
                    " " xa " " ya)])

          (and (= t1 :round) (= t2 :sharp))
          [2 (str "Q " x1 " " y1
                  " " x2 " " y2)]

          :else
          [1 (str "L " x1 " " y1)])))

(defn points->svg [points]
  (if (empty? points)
    ""
    (let [{[x y] :pos} (first points)
          d (str "M " x " " y " ")]
      (loop [points (rest points)
             d d]
        (if (empty? points)
          d
          (let [[n result] (point->svg points)]
            (recur (drop n points) (str d result))))))))

(defrecord Path [id mat-id points]

  Transform
  (translate [shape _]
    shape)

  (scale [shape _]
    shape)

  OnSelect
  (on-select [_ s]
    (state/map-shape-ids! s (into #{} points) #(assoc % :selected true)))

  RenderSVG
  (render-svg [shape s]
    (when-not (empty? points)
      (let [materials (state/get-materials s)
            {:keys [color]} (get materials mat-id)
            point-shapes (state/get-shapes-ids-with-override s points)]
        [:g
         [:path (merge {:id id
                        :fill color
                        :d (points->svg point-shapes)}
                       (utils/apply-selected-style shape))]
         (when (state/is-active-path? s id)
           (for [p point-shapes]
             ^{:key (:id p)}
             [render-svg p s]))]))))

(defn path []
  (Path. (utils/new-shape-id) :default []))
