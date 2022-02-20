(ns frontend.tools.styles-panel
  (:require [frontend.state.viewport :as viewport]))

(defn styles-panel
  "Toggles the styles panel."
  []
  (if (= (viewport/panel) :styles)
    (viewport/panel! nil)
    (viewport/panel! :styles)))
