(ns pearigon.api.core
  (:require
   [pearigon.api.shapes :as shapes]
   [pearigon.api.canvas :as canvas]
   [sci.core :as sci]))

(def opts
  {:namespaces
   {'clojure.core (merge canvas/ns-map
                         shapes/ns-map)
    'pearigon.api.canvas canvas/ns-map
    'pearigon.api.shapes shapes/ns-map}})

(defonce ctx (sci/init opts))

(defn eval-string [s]
  (sci/eval-string* ctx s))

(defn ^:export eval-string-js [s]
  (clj->js (sci/eval-string s opts)))
