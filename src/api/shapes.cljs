(ns api.shapes
  (:require
   [pearigon.shapes.path.path :as path]))

(defn circle [pos r]
  (path/circle pos r))

(defn square [pos size]
  (path/rectangle pos size))

(def ns-map
  {'circle circle
   'square square})
