(ns pearigon.state.mouse
  (:require
   [reagent.core :as r]))

(def initial-state
  {;; Mouse position in canvas coordinates
   :mouse-pos [0 0]
   :buttons-down {}})

(def *db (r/atom initial-state))

(defn pos []
  (:mouse-pos @*db))

(defn pos! [vec]
  (swap! *db assoc :mouse-pos vec))

(defn button-down? [button]
  (get (:buttons-down @*db) button))

(defn merge-buttons-down! [map]
  (swap! *db assoc :buttons-down (merge (:buttons-down @*db) map)))
