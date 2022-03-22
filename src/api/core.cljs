(ns api.core
  (:require
   [api.shapes :as shapes]
   [api.state :as state]
   [sci.core :as sci]))

(def opts
  {:namespaces
   {'clojure.core (merge state/ns-map
                         shapes/ns-map)}})

(defonce ctx (sci/init opts))

(defn eval-string [s]
  (sci/eval-string* ctx s))

(defn ^:export eval-string-js [s]
  (clj->js (sci/eval-string s opts)))