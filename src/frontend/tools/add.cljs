(ns frontend.tools.add
  (:require
   [frontend.actions.core :as actions]
   [frontend.shapes.path.path :as p]
   [frontend.state.core :as state]
   [frontend.state.tools :as tools]
   [frontend.state.mouse :as mouse]
   [frontend.tools.grab :refer [grab]]
   [frontend.tools.protocol :refer [OnKeypress]]))

(defrecord AddTool [display action]
  OnKeypress
  (on-keypress [_ k]
    (let [[x y] (mouse/pos)
          shape (cond
                   (actions/active? :add.rect k)
                   (p/rectangle [x y] 40)

                   (actions/active? :add.circle k)
                   (p/circle [x y] 40)

                   :else nil)]
      (when shape
        (state/deselect-all!)
        (state/add-shape! shape)
        (tools/pop-tool!)
        (grab)))))

(defn add []
  (tools/push-tool! (->AddTool "Add Shape" :add)))
