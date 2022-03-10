(ns frontend.tools.duplicate
  (:require [frontend.state.core :as state]
            [frontend.tools.grab :refer [grab]]))

(defn duplicate []
  ;; Only duplicate paths (not the child points).
  (let [selection (filter :points (state/get-selected))]
    (state/deselect-all!)
    (doseq [shape selection]
      ;; Preserve the styles of the input.
      (let [id (:id (state/add-shape! shape :default-styles? false))]
        (state/select-id! id)))
    (grab)))
