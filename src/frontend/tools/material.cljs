(ns frontend.tools.material
  (:require [frontend.state.core :as state]))

(defn material
  "Toggles the material panel."
  []
  (if (= (state/get-panel) :material)
    (state/set-panel! nil)
    (state/set-panel! :material)))
