(ns svg-editor.input.resize
  (:require
    [svg-editor.state :as state]))

(defn- bind-resize
  [s]
  (.addEventListener js/window "resize"
                     #(state/update-view-size! s)))

(defn init
  "Bind resize handlers."
  [s]
  (bind-resize s))
