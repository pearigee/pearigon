(ns frontend.tools.delete
  (:require [frontend.state :as state]))

(defn delete []
  (let [selection (map :id (state/get-selected))]
    (apply state/delete! selection)))
