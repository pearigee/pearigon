(ns svg-editor.shapes)

(def id-state (atom 0))
(defn next-id []
  (swap! id-state inc)
  (str "shape-" @id-state))

(defn shape [value]
  (merge {:id (next-id)
          :x 0
          :y 0
          :offset-x 0
          :offset-y 0}
         value))

(defn circle [x y r]
  (shape {:type :circle
          :x x
          :y y
          :r r}))

(defn rect [x y w h]
  (shape {:type :rect 
          :x x 
          :y y
          :w w 
          :h h}))