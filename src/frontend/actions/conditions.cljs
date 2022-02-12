(ns frontend.actions.conditions
  (:require [frontend.state.core :as state]))

;; Forward declaratoin of valid so it can be used recursively below.
(declare valid?)

(defn- num-selected-eq? [[num {:keys [path?] :or {path? false}}]]
  (if path?
    (= num (count (filter :points (state/get-selected))))
    (= num (count (state/get-selected)))))

(defn- num-selected-gt? [[num]]
  (> (count (state/get-selected)) num))

(defn- and? [args]
  (every? true? (map valid? args)))

(defn- or? [args]
  (some true? (map valid? args)))

(defn valid? [condition]
  (if-not (nil? condition)
          (let [f (first condition)
                args (rest condition)]
            (case f
              :and (and? args)
              :or (or? args)
              :num-selected-eq? (num-selected-eq? args)
              :num-selected-gt? (num-selected-gt? args)
              (js/console.error "Invalid action condition!" f args)))
          true))
