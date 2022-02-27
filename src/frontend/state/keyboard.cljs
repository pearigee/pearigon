(ns frontend.state.keyboard
  (:require [reagent.core :as r]
            [mount.core :refer-macros [defstate]]))

(defstate db
  :start
  (r/atom {:ctrl-down? false
           :alt-down? false}))

(defn ctrl-down?! [down?]
  (swap! @db assoc :ctrl-down? down?))

(defn just-ctrl? []
  (let [{:keys [alt-down? ctrl-down?]} @@db]
    (and ctrl-down?
         (not alt-down?))))

(defn alt-down?! [down?]
  (swap! @db assoc :alt-down? down?))

(defn just-alt? []
  (let [{:keys [alt-down? ctrl-down?]} @@db]
    (and alt-down?
         (not ctrl-down?))))
