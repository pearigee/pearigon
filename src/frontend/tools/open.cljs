(ns frontend.tools.open
  (:require [frontend.state.core :as state]
            [frontend.utils.file-system :as fs]
            [frontend.shapes.path.path :refer [map->Path]]
            [frontend.shapes.path.point :refer [map->Point]]
            [clojure.edn :as edn]))

(defn open []
  (fs/open-project
   (fn [file]
     (let [readers {'frontend.shapes.path.path.Path map->Path
                    'frontend.shapes.path.point.Point map->Point}
           result (edn/read-string {:readers readers} file)]
       (state/apply-save-state! result)))))
