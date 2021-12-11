(ns svg-editor.tools
  (:require [svg-editor.state :as state]))

(defn grab [mouse-state]
  {:type :grab
   :x (:page-x mouse-state)
   :y (:page-y mouse-state)})

(defn apply-grab [state mouse-state]
  (state/map-shapes
   state
   #(if (:selected %)
      (merge % {:x (:page-x mouse-state) :y (:page-y mouse-state)})
      %)))