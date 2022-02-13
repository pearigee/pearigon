(ns frontend.state.input
  (:require [reagent.core :as r]))

(def initial-state
  {;; Mouse position in canvas coordinates
   :mouse-pos [0 0]
   :ctrl-down? false
   :alt-down? false})

(def ^:dynamic *db* (r/atom initial-state))

(defn mouse-pos []
  (-> @*db* :mouse-pos))

(defn mouse-pos! [vec]
  (swap! *db* assoc :mouse-pos vec))

(defn ctrl-down? []
  (-> @*db* :ctrl-down?))

(defn ctrl-down?! [down?]
  (swap! *db* assoc :ctrl-down? down?))

(defn just-ctrl? []
  (let [{:keys [alt-down? ctrl-down?]} @*db*]
    (and ctrl-down?
         (not alt-down?))))

(defn alt-down? []
  (-> @*db* :alt-down?))

(defn alt-down?! [down?]
  (swap! *db* assoc :alt-down? down?))

(defn just-alt? []
  (let [{:keys [alt-down? ctrl-down?]} @*db*]
    (and alt-down?
         (not ctrl-down?))))
