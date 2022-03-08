(ns frontend.actions.conditions
  (:require
   [frontend.state.core :as state]))

(declare valid?)

(defn- get-selected [{:keys [point path]
                      :or {point false
                           path false}}]
  (cond path
        (filter :points (state/get-selected))

        point
        (filter (fn [s] (#{:sharp :round} (:type s))) (state/get-selected))

        :else
        (state/get-selected)))

(defn- num-selected-eq? [[num config]]
  (= num (count (get-selected config))))

(defn- num-selected-gt? [[num config]]
  (> (count (get-selected config)) num))

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
        :num-selected-eq (num-selected-eq? args)
        :num-selected-gt (num-selected-gt? args)
        (js/console.error "Invalid action condition!" f args)))
    true))
