(ns pearigon.utils.edn
  (:require
   [clojure.edn :as edn]
   [pearigon.shapes.path.path :refer [map->Path]]
   [pearigon.shapes.path.point :refer [map->Point]]))

(defn read-string
  "A wrapper of edn/read-string with readers for shape data."
  [s]
  (edn/read-string
   {:readers {'pearigon.shapes.path.path.Path map->Path
              'pearigon.shapes.path.point.Point map->Point
              ;; Include old paths for compatibility with existing save files.
              ;; Will delete this after a few commits.
              'frontend.shapes.path.path.Path map->Path
              'frontend.shapes.path.point.Point map->Point}}
   s))
