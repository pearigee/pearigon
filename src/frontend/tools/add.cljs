(ns frontend.tools.add
  (:require
   [frontend.input.hotkeys :as hotkeys]
   [frontend.shapes.path.path :as p]
   [frontend.state.core :as state]
   [frontend.tools.grab :refer [grab]]
   [frontend.tools.protocol :refer [OnKeypress]]))

(defrecord AddTool [display action]
  OnKeypress
  (on-keypress [_ k]
    (let [[x y] (state/get-mouse-pos)
          shape (cond
                   (hotkeys/active? :add.rect k)
                   (p/rectangle [x y] 40)

                   (hotkeys/active? :add.circle k)
                   (p/circle [x y] 40)

                   :else nil)]
      (when shape
        (state/deselect-all!)
        (state/add-shape! shape)
        (state/pop-tool!)
        (grab)))))

(defn add []
  (state/push-tool! (->AddTool "Add Shape" :add)))
