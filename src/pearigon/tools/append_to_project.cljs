(ns pearigon.tools.append-to-project
  (:require
   [pearigon.api.canvas :as canvas]
   [pearigon.utils.edn :as edn]
   [pearigon.shapes.path.path :as path]
   [pearigon.utils.file-system :as fs]))

(defn append-to-project []
  (fs/open-project
   (fn [file]
     (let [result (edn/read-string file)]
       (doseq [sid (:draw-order result)]
         (let [shape (-> result :shapes (get sid))]
           (canvas/add-shape!
            (path/map->Path
             ;; Select only the keys the app currently supports.
             (select-keys shape
                          [:id :points :closed? :styles])))))))))
