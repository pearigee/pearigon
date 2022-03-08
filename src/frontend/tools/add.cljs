(ns frontend.tools.add
  (:require
   [frontend.shapes.path.path :as p]
   [frontend.state.actions :as actions]
   [frontend.state.core :as state]
   [frontend.state.mouse :as mouse]
   [frontend.state.tools :as tools]
   [frontend.tools.grab :refer [grab]]
   [frontend.tools.protocol :refer [OnKeypress]]))

(defn- add-shape [shape]
  (state/deselect-all!)
  (state/add-shape! shape)
  (grab))

(defn add-rect []
  (let [[x y] (mouse/pos)]
    (add-shape (p/rectangle [x y] 40))))

(defn add-circle []
  (let [[x y] (mouse/pos)]
    (add-shape (p/circle [x y] 40))))

(defrecord AddTool [display action]
  OnKeypress
  (on-keypress [_ k]
    (cond
      (actions/active? :add.rect k)
      (do (tools/pop-tool!)
          (add-rect))

      (actions/active? :add.circle k)
      (do (tools/pop-tool!)
          (add-circle)))))

(defn add []
  (tools/push-tool! (->AddTool "Add Shape" :add)))
