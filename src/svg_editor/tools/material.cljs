(ns svg-editor.tools.material
  (:require
    [svg-editor.state :as state]))

(defn material
  [state]
  (state/set-panel! state :material))
