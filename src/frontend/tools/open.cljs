(ns frontend.tools.open
  (:require
   [clojure.edn :as edn]
   [frontend.shapes.path.path :refer [map->Path]]
   [frontend.shapes.path.point :refer [map->Point]]
   [frontend.state.core :as state]
   [frontend.utils.file-system :as fs]))

(defn open []
  (fs/open-project
   (fn [file]
     (let [readers {'frontend.shapes.path.path.Path map->Path
                    'frontend.shapes.path.point.Point map->Point}
           result (edn/read-string {:readers readers} file)]
       (state/apply-save-state! result)))))
