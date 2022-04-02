(ns pearigon.api.canvas
  (:require
   [pearigon.state.core :as state]))

(defn add-shape!
  ([shape] (add-shape! shape {}))
  ([shape {:keys [selected]
           :or {selected true}}]
   (state/add-shape! shape
                     :selected? selected
                     ;; Only apply defaults if no existing styles are defined.
                     :default-styles? (nil? (:styles shape)))))

(defn selected? [sid]
  (state/selected? sid))

(defn draw-order []
  (state/get-draw-order))

(defn default-styles []
  (state/default-styles))

(defn default-styles! [styles]
  (state/default-styles! styles))

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
   'default-styles default-styles
   'default-styles! default-styles!
   'shapes shapes})
