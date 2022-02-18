(ns frontend.tools.styles-panel
  (:require [frontend.state.core :as state]))

(defn styles-panel
  "Toggles the styles panel."
  []
  (if (= (state/get-panel) :styles)
    (state/set-panel! nil)
    (state/set-panel! :styles)))
