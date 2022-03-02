(ns frontend.utils.edn
  (:require
   [clojure.edn :as edn]
   [frontend.shapes.path.path :refer [map->Path]]
   [frontend.shapes.path.point :refer [map->Point]]))

(defn read-string
  "A wrapper of edn/read-string with readers for shape data."
  [s]
  (edn/read-string
   {:readers {'frontend.shapes.path.path.Path map->Path
              'frontend.shapes.path.point.Point map->Point}}
   s))
