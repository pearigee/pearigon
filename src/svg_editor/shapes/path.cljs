(ns svg-editor.shapes.path
  (:require [svg-editor.shapes.protocol :refer [RenderSVG Transform
                                                render-svg
                                                OnSelect]]
            [svg-editor.state :as state]
            [svg-editor.shapes.utils :as utils]))

(defn points->svg [s points]
  (let [ps (state/get-shapes-ids-with-override s points)
        {[x y] :pos} (first ps)
        path (rest ps)
        path-str (map (fn [{[x y] :pos}]
                        (str "L" x " " y " ")) path)]
    (str "M" x " " y " "
         (apply str path-str))))

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
                        :d (points->svg s points)}
                       (utils/apply-selected-style shape))]
         (when (state/is-active-path? s id)
           (for [p point-shapes]
             ^{:key (:id p)}
             [render-svg p s]))]))))

(defn path []
  (Path. (utils/new-shape-id) :default []))
