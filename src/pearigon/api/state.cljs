(ns pearigon.api.state
  (:require
   [pearigon.state.core :as state]))

(defn add-shape!
  ([shape] (state/add-shape! shape))
  ([shape {:keys [selected default-styles]
           :or {selected true default-styles true}}]
   (state/add-shape! shape
                     :selected? selected
                     :default-styles? default-styles)))

(def ns-map
  {'add-shape! add-shape!})
