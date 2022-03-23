(ns pearigon.utils.ids)

(defn shape-id []
  (str "shape-" (random-uuid)))

(defn point-id [parent-id]
  (str parent-id ":" (random-uuid)))
