(ns pearigon.api.canvas
  (:require
   [pearigon.state.core :as state]))

(defn add-shape!
  ([shape] (state/add-shape! shape))
  ([shape {:keys [selected default-styles]
           :or {selected true default-styles true}}]
   (state/add-shape! shape
                     :selected? selected
                     :default-styles? default-styles)))

(defn selected? [sid]
  (state/selected? sid))

(defn draw-order []
  (state/get-draw-order))

(defn shapes
  ([] (shapes {}))
  ([{:keys [preview] :or {preview true}}]
   (if preview
     (state/get-shapes-with-override)
     (state/get-shapes))))

(def ns-map
  {'add-shape! add-shape!
   'selected? selected?
   'draw-order draw-order
   'shapes shapes})
