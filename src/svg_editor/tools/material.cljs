(ns svg-editor.tools.material
  (:require [svg-editor.state :as state]))

(defn material []
  (state/set-panel! :material))
