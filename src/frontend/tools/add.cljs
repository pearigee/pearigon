(ns frontend.tools.add
  (:require
   [frontend.actions.core :as actions]
   [frontend.shapes.path.path :as p]
   [frontend.state.core :as state]
   [frontend.state.input :as input]
   [frontend.tools.grab :refer [grab]]
   [frontend.tools.protocol :refer [OnKeypress]]))

(defrecord AddTool [display action]
  OnKeypress
  (on-keypress [_ k]
    (let [[x y] (input/mouse-pos)
          shape (cond
                   (actions/active? :add.rect k)
                   (p/rectangle [x y] 40)

                   (actions/active? :add.circle k)
                   (p/circle [x y] 40)

                   :else nil)]
      (when shape
        (state/deselect-all!)
        (state/add-shape! shape)
        (state/pop-tool!)
        (grab)))))

(defn add []
  (state/push-tool! (->AddTool "Add Shape" :add)))
