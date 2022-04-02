(ns pearigon.api.shapes
  (:require
   [pearigon.shapes.path.path :as path]))

(defn circle [pos r]
  (path/circle pos r))

(defn rectangle [pos size]
  (path/rectangle pos size))

(def ns-map
  {'circle circle
   'rectangle rectangle})
