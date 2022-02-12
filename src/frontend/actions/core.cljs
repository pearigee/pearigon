(ns frontend.actions.core
  (:require [reagent.core :as r]
            [frontend.actions.config :refer [actions]]
            [frontend.actions.conditions :as c]))

(def suggestions (r/atom #{}))

(defn- get-key
  "Get the hotkey for this action."
  [id]
  (-> actions (get id) :key))

(defn- get-display
  "Get the label for this action."
  [id]
  (-> actions (get id) :display))

(defn clear-suggestions!
  "Clear the suggestion set.
   This should be done before recording new suggestions."
  []
  (reset! suggestions #{}))

(defn active?
  "Is the current action active based on the pressed key?
  If we recieve the fake hotkey :record-suggestions, add this
  action to the current suggestion set. "
  [id key]
  (if (and (= key :record-suggestions)
           (c/valid? (:when (get actions id))))
    (do (swap! suggestions conj id)
        false)
    (= key (get-key id))))

(defn get-hotkey-suggestions
  "Get active actions sorted by hotkey."
  []
  (->> @suggestions
       (map (fn [id] {:key-display (subs (str (get-key id)) 1)
                      :key (get-key id)
                      :display (get-display id)}))
       (sort-by :key-display)))
