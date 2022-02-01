(ns frontend.input.resize
  (:require
   [frontend.state :as state]))

(defn- bind-resize []
  (.addEventListener js/window "resize"
                     #(state/update-view-size!)))

(defn init
  "Bind resize handlers."
  []
  (bind-resize))
