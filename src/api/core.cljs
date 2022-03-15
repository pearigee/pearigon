(ns api.core
  (:require [sci.core :as sci]
            [api.state :as state]
            [api.shapes :as shapes]))

(def opts
  {:namespaces
   {'clojure.core (merge state/ns-map
                         shapes/ns-map)}})

(defn eval-string [s]
  (sci/eval-string s opts))

(defn ^:export eval-string-js [s]
  (clj->js (sci/eval-string s opts)))
