(ns api.core
  (:require [sci.core :as sci]
            [api.state :as state]
            [api.shapes :as shapes]))

(def opts
  {:namespaces
   {'clojure.core (merge state/ns-map
                         shapes/ns-map)}})

(defonce ctx (sci/init opts))

(defn eval-string [s]
  (sci/eval-string* ctx s))

(defn ^:export eval-string-js [s]
  (clj->js (sci/eval-string s opts)))
