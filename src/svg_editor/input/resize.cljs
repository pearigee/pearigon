(ns svg-editor.input.resize
  (:require
   [svg-editor.state :as state]))

(defn- bind-resize []
  (.addEventListener js/window "resize"
                     #(state/update-view-size!)))

(defn init
  "Bind resize handlers."
  []
  (bind-resize))
