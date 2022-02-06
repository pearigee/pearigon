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
          shape (condp = k
                   (actions/get-key :add.rect)
                   (p/rectangle [x y] 40)

                   (actions/get-key :add.circle)
                   (p/circle [x y] 40)

                   nil)]
      (when shape
        (state/deselect-all!)
        (state/add-shape! shape)
        (state/pop-tool!)
        (grab)))))

(defn add []
  (state/push-tool! (AddTool. "Add Shape" :add)))
