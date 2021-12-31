(ns svg-editor.shapes)

(def id-state (atom 0))
(defn next-id []
  (swap! id-state inc)
  (str "shape-" @id-state))

(defn shape [value]
  (merge {:id (next-id)
          :pos [0 0]
          :material :default
          :offset [0 0]
          :offset-scale [0 0]}
         value))

(defn circle [x y r]
  (shape {:type :circle
          :pos [x y]
          :r r}))

(defn rect [x y w h]
  (shape {:type :rect
          :pos [x y]
          :dim [w h]}))