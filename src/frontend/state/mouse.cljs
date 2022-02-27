(ns frontend.state.mouse
  (:require [reagent.core :as r]
            [mount.core :refer-macros [defstate]]))

(defstate db
  :start
  (r/atom {;; Mouse position in canvas coordinates
           :mouse-pos [0 0]}))

(defn pos []
  (-> @@db :mouse-pos))

(defn pos! [vec]
  (swap! @db assoc :mouse-pos vec))
