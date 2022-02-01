(ns frontend.tools.material
  (:require [frontend.state :as state]))

(defn material []
  (state/set-panel! :material))
