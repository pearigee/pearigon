(ns svg-editor.shapes.path
  (:require [svg-editor.shapes.protocol :refer [RenderSVG Transform
                                                OnSelect]]
            [svg-editor.state :as state]
            [svg-editor.math :refer [avg]]
            [svg-editor.shapes.utils :as utils]))

(defn svg-starting-pos [points closed?]
  (let [{[x1 y1] :pos t1 :type} (first points)
        {[x2 y2] :pos t2 :type} (second points)]
    (if (and closed? (>= (count points) 3))
      (cond (= t1 t2 :round)
            (avg [[x1 y1] [x2 y2]])

            (and (= t1 :round)
                 (= t2 :sharp))
            [x2 y2]

            :else
            [x1 y1])
      [x1 y1])))

(defn point->svg [points closed?]
  (let [[{[x1 y1] :pos t1 :type}
         {[x2 y2] :pos t2 :type}] points]
    (cond (= t1 t2 :round)
          (let [[xa ya] (avg [[x1 y1] [x2 y2]])]
            [1 (str "Q " x1 " " y1
                    " " xa " " ya)])

          (and (= t1 :round) (= t2 :sharp))
          [2 (str "Q " x1 " " y1
                  " " x2 " " y2)]

          (and closed?
               (= (count points) 1))
          [1 ""]

          :else
          [1 (str "L " x1 " " y1)])))

(defn points->svg [points closed?]
  (if (empty? points)
    ""
    (let [[xi yi] (svg-starting-pos points closed?)
          d (str "M " xi " " yi)]
      (loop [points (if closed?
                      (conj (into [] (rest points))
                            (first points) (second points))
                      (rest points))
             d d]
        (if (empty? points)
          d
          (let [[n result] (point->svg points closed?)]
            (recur (drop n points) (str d result))))))))

(defrecord Path [id mat-id points closed?]

  Transform
  (translate [shape _]
    shape)

  (scale [shape _]
    shape)

  OnSelect
  (on-select [_]
    (state/map-shape-ids! (into #{} points) #(assoc % :selected true)))

  RenderSVG
  (render-svg [shape]
    (when-not (empty? points)
      (let [materials (state/get-materials)
            {:keys [color]} (get materials mat-id)
            point-shapes (state/get-shapes-by-id-with-override points)]
        [:g
         [:path {:id id
                 :class (utils/apply-selected-style shape "")
                 :fill color
                 :d (points->svg point-shapes closed?)}]]))))

(defn path []
  (Path. (utils/new-shape-id) :default [] true))
