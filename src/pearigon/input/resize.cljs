(ns pearigon.input.resize
  (:require
   [pearigon.state.viewport :as viewport]))

(defn- bind-resize []
  (.addEventListener js/window "resize"
                     #(viewport/on-resize!)))

(defn init
  "Bind resize handlers."
  []
  (bind-resize))
