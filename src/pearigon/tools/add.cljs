(ns pearigon.tools.add
  (:require
   [pearigon.shapes.path.path :as p]
   [pearigon.state.actions :as actions]
   [pearigon.state.core :as state]
   [pearigon.state.mouse :as mouse]
   [pearigon.state.tools :as tools]
   [pearigon.tools.grab :refer [grab]]
   [pearigon.tools.protocol :refer [OnKeypress]]))

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
