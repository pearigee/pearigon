(ns pearigon.shapes.path.path
  (:require
   [pearigon.math :as m]
   [pearigon.shapes.path.point :refer [point]]
   [pearigon.shapes.path.svg :refer [points->svg]]
   [pearigon.shapes.protocol :refer [OnSelect RenderSVG Transform]]
   [pearigon.state.core :as state]
   [pearigon.utils.styles :as styles]))

(defrecord Path [id points closed?]

  Transform
  (transform [shape _]
    shape)

  OnSelect
  (on-select [_]
    (doseq [id (into #{} (map :id points))]
      (state/select-id! id)))

  RenderSVG
  (render-svg [shape]
    (when-not (empty? points)
      (let [styles (styles/styles->svg-attr (:styles shape))
            ps (state/get-shapes-by-id-with-override (map :id points))]
        [:g
         [:path (merge {:id id
                        :class (styles/apply-selected-style
                                (state/selected? id)
                                "")
                        :d (points->svg ps closed?)}
                       styles)]]))))

(defn path
  ([] (->Path "" [] true))
  ([points] (->Path "" points true)))

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
