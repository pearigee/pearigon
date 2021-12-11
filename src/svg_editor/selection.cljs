(ns svg-editor.selection
  (:require [svg-editor.state :as state]))

(defn deselect-all [state]
  (state/map-shapes state #(assoc % :selected false)))