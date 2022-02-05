(ns frontend.tools.add
  (:require
   [frontend.actions :as actions]
   [frontend.shapes.path :as p]
   [frontend.state :as state]
   [frontend.tools.grab :refer [grab]]
   [frontend.tools.protocol :refer [OnKeypress]]))

(defrecord AddTool [display action]
  OnKeypress
  (on-keypress [_ k]
    (let [[x y] (state/get-mouse-pos)
          points (condp = k
                   (actions/get-key :add.rect)
                   (p/rectangle-points [x y] 40)

                   (actions/get-key :add.circle)
                   (p/circle-points [x y] 40)

                   nil)]
      (when points
        (state/deselect-all!)
        (p/create-path! points)
        (state/pop-tool!)
        (grab)))))

(defn add []
  (state/push-tool! (AddTool. "Add Shape" :add)))
