(ns frontend.tools.draw-order
  (:require
   [frontend.state.core :as state]))

(defn move-up []
  (let [selection (first (state/get-selected))
        id (:id selection)]
    (when id (state/move-up-draw-order! id))))

(defn move-down []
  (let [selection (first (state/get-selected))
        id (:id selection)]
    (when id (state/move-down-draw-order! id))))
