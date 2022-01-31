(ns svg-editor.tools.grab
  (:require
   [svg-editor.state :as state]
   [svg-editor.tools.protocol :refer [OnMouseMove OnClick]]
   [svg-editor.shapes.protocol :refer [translate]]
   [svg-editor.math :refer [v-]]))

(defrecord GrabTool [display action init-mouse-pos]
  OnMouseMove
  (on-mouse-move [_ {pos :pos}]
    (let [offset (v- pos init-mouse-pos)]
      (state/map-selected-shapes-preview! #(translate % offset))))

  OnClick
  (on-click [_ {pos :pos}]
    (let [offset (v- pos init-mouse-pos)]
      (state/map-selected-shapes! #(translate % offset)))
    (state/clear-shape-preview!)
    (state/pop-tool!)))

(defn grab []
  (state/push-tool! (GrabTool. "Grab"
                                 :grab
                                 (state/get-mouse-pos))))
