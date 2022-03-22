(ns pearigon.tools.styles-panel
  (:require
   [pearigon.state.viewport :as viewport]))

(defn styles-panel
  "Toggles the styles panel."
  []
  (if (= (viewport/panel) :styles)
    (viewport/panel! nil)
    (viewport/panel! :styles)))
