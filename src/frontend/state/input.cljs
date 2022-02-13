(ns frontend.state.input
  (:require [reagent.core :as r]))

(def initial-state
  {;; Mouse position in canvas coordinates
   :mouse-pos [0 0]})

(def ^:dynamic *db* (r/atom initial-state))

(defn mouse-pos []
  (-> @*db* :mouse-pos))

(defn mouse-pos! [vec]
  (swap! *db* assoc :mouse-pos vec))
