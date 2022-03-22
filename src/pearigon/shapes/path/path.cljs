(ns pearigon.shapes.path.path
  (:require
   [pearigon.math :as m]
   [pearigon.shapes.path.point :refer [point]]
   [pearigon.shapes.path.svg :refer [points->svg]]
   [pearigon.shapes.protocol :refer [OnSelect RenderSVG Transform]]
   [pearigon.state.core :as state]
   [pearigon.utils.styles :as styles]))

(defrecord Path [id mat-id points closed?]

  Transform
  (transform [shape _]
    shape)

  OnSelect
  (on-select [_]
    (state/map-shape-ids! (into #{} (map :id points))
                          #(assoc % :selected true)))

  RenderSVG
  (render-svg [shape]
    (when-not (empty? points)
      (let [styles (styles/styles->svg-attr (:styles shape))
            ps (state/get-shapes-by-id-with-override (map :id points))]
        [:g
         [:path (merge {:id id
                        :class (styles/apply-selected-style shape "")
                        :d (points->svg ps closed?)}
                       styles)]]))))

(defn path
  ([] (->Path "" :default [] true))
  ([points] (->Path "" :default points true)))

(defn circle [pos size]
  (path
   (mapv
    #(assoc % :pos (m/v+ (:pos %) pos))
    (let [res 8
          step (/ (* 2 m/pi) res)]
      (for [p (range res)]
        (let [angle (* p step)]
          (point [(* size (m/cos angle))
                  (* size (m/sin angle))]
                 :round)))))))

(defn rectangle [pos size]
  (path
   (mapv
    #(assoc % :pos (m/v+ (:pos %) pos))
    [(point [(- size) size] :sharp)
     (point [size size] :sharp)
     (point [size (- size)] :sharp)
     (point [(- size) (- size)] :sharp)])))
