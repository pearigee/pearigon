(ns svg-editor.tools.grab
  (:require
   [svg-editor.state :as state]
   [svg-editor.tools.protocol :refer [OnMouseMove OnClick]]
   [svg-editor.shapes.protocol :refer [translate]]
   [svg-editor.math :refer [v-]]))

(defrecord GrabTool [display action init-mouse-pos]
  OnMouseMove
  (on-mouse-move [_ s {pos :pos}]
    (let [offset (v- pos init-mouse-pos)]
      (state/map-selected-shapes-preview! s #(translate % offset))))

  OnClick
  (on-click [_ s {pos :pos}]
    (let [offset (v- pos init-mouse-pos)]
      (state/map-selected-shapes! s #(translate % offset)))
    (state/clear-shape-preview! s)
    (state/set-tool! s nil)))

(defn grab
  [s]
  (state/set-tool! s (GrabTool. "Grab"
                                :grab
                                (state/get-mouse-pos s))))
