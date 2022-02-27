(ns frontend.state.actions
  (:require [reagent.core :as r]
            [mount.core :refer-macros [defstate]]
            [frontend.actions.config :refer [actions]]
            [frontend.actions.conditions :as c]))

(defstate suggestions
  :start
  (r/atom #{}))

(defn- get-key
  "Get the hotkey for this action."
  [id]
  (-> actions (get id) :key))

(defn- get-key-display [id]
  (-> actions (get id) :key :display))

(defn- get-display
  "Get the label for this action."
  [id]
  (-> actions (get id) :display))

(defn- similar?
  "A equality comparision that asserts nil to false."
  [a b]
  (= (boolean a) (boolean b)))

(defn- key-matches? [a b]
  (and (= (:code a) (:code b))
       (similar? (:alt a) (:alt b))
       (similar? (:ctrl a) (:ctrl b))
       (similar? (:shift a) (:shift b))))

(defn uses-ctrl? [id]
  (:ctrl (get-key id)))

(defn uses-alt? [id]
  (:alt (get-key id)))

(defn uses-no-modifiers? [id]
  (let [{:keys [alt ctrl]} (get-key id)]
    (and (not alt) (not ctrl))))

(defn clear-suggestions!
  "Clear the suggestion set.
   This should be done before recording new suggestions."
  []
  (reset! @suggestions #{}))

(defn active?
  "Is the current action active based on the pressed key?
  If we recieve the fake hotkey :record-suggestions, add this
  action to the current suggestion set. "
  [id key]
  (when (c/valid? (:when (get actions id)))
    (if (= key :record-suggestions)
      (do (swap! @suggestions conj id)
          false)
      (key-matches? key (get-key id)))))

(defn get-hotkey-suggestions
  "Get active actions sorted by hotkey."
  []
  (->> @@suggestions
       (map (fn [id] {:key-display (get-key-display id)
                      :key (get-key id)
                      :display (get-display id)}))
       (sort-by :key-display)))
