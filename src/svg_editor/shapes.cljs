(ns svg-editor.shapes)

(def id-state (atom 0))
(defn next-id []
  (swap! id-state inc)
  @id-state)

(defn circle [x y r]
  {:id (next-id)
   :type :circle
   :x x
   :y y
   :r r})