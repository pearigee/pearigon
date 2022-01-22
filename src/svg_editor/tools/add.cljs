(ns svg-editor.tools.add
  (:require
   [svg-editor.actions :as actions]
   [svg-editor.shapes.rectangle :refer [rectangle]]
   [svg-editor.shapes.circle :refer [circle]]
   [svg-editor.state :as state]
   [svg-editor.tools.grab :refer [grab]]
   [svg-editor.tools.protocol :refer [OnKeypress]]))

(defrecord AddTool [display action]
  OnKeypress
  (on-keypress [_ s k]
    (let [[x y] (state/get-mouse-pos s)
          shape (condp = k
                  (actions/get-key :add.rect) (rectangle [x y] [40 40])
                  (actions/get-key :add.circle) (circle [x y] 20)
                  nil)]
      (js/console.log "Shape selected:" shape)
      (if shape
        (do (state/add-shape! s shape)
            (grab s))
        (state/set-tool! s nil)))))

(defn add
  [s]
  (state/set-tool! s (AddTool. "Add Shape" :add)))
