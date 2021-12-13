(ns svg-editor.state
  (:require [reagent.core :as r]))

(defn initial-state []
  (r/atom {:shapes []
           :mouse {:page-x 0
                   :page-y 0}
           :tool nil
           :next-id 0}))

(defn map-shapes [state f]
  (swap! state update-in [:shapes]
         (fn [shapes]
           (map f shapes))))

(defn deselect-all [state]
  (map-shapes state #(assoc % :selected false)))

(defn set-tool [state tool]
  (swap! state assoc :tool tool))

(defn add-shape [state shape]
  (deselect-all state)
  (swap! state update-in [:shapes] conj
         (merge shape {:selected true})))

(defn set-mouse-state [state mouse-state]
  (swap! state assoc :mouse mouse-state))