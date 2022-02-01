(ns frontend.tools.add
  (:require
   [frontend.actions :as actions]
   [frontend.shapes.rectangle :refer [rectangle]]
   [frontend.shapes.circle :refer [circle]]
   [frontend.state :as state]
   [frontend.tools.grab :refer [grab]]
   [frontend.tools.protocol :refer [OnKeypress]]))

(defrecord AddTool [display action]
  OnKeypress
  (on-keypress [_ k]
    (let [[x y] (state/get-mouse-pos)
          shape (condp = k
                  (actions/get-key :add.rect) (rectangle [x y] [40 40])
                  (actions/get-key :add.circle) (circle [x y] 20)
                  nil)]
      (js/console.log "Shape selected:" shape)
      (if shape
        (do (state/add-shape! shape)
            (state/pop-tool!)
            (grab))))))

(defn add []
  (state/push-tool! (AddTool. "Add Shape" :add)))
