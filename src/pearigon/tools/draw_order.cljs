(ns pearigon.tools.draw-order
  (:require
   [pearigon.state.core :as state]))

(defn move-up []
  (let [selection (first (filter :points (state/get-selected)))
        id (:id selection)]
    (when id (state/move-up-draw-order! id))))

(defn move-down []
  (let [selection (first (filter :points (state/get-selected)))
        id (:id selection)]
    (when id (state/move-down-draw-order! id))))
