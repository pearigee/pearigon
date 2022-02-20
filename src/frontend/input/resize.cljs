(ns frontend.input.resize
  (:require [frontend.state.viewport :as viewport]))

(defn- bind-resize []
  (.addEventListener js/window "resize"
                     #(viewport/on-resize!)))

(defn init
  "Bind resize handlers."
  []
  (bind-resize))
