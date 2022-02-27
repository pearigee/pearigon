(ns frontend.state.mouse
  (:require [reagent.core :as r]))

(def initial-state
  {;; Mouse position in canvas coordinates
   :mouse-pos [0 0]})

(def ^:dynamic *db* (r/atom initial-state))

(defn pos []
  (-> @*db* :mouse-pos))

(defn pos! [vec]
  (swap! *db* assoc :mouse-pos vec))
