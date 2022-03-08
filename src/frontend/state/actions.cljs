(ns frontend.state.actions
  (:require
   [frontend.actions.conditions :as c]
   [clojure.string :as str]
   [reagent.core :as r]))

(def initial-state {:suggestions #{}
                    :config {}
                    :action->handler {}
                    :after-action (fn [])})

(def *db (r/atom initial-state))

(defn- config []
  (:config @*db))

(defn- suggestions []
  (:suggestions @*db))

(defn- action-handler [id]
  (-> (:action->handler @*db) (get id)))

(defn- hotkey
  "Get the hotkey for this action."
  [id]
  (-> (config) (get id) :key))

(defn- hotkey-display [id]
  (-> (config) (get id) :key :display))

(defn display
  "Get the label for this action."
  [id]
  (-> (config) (get id) :display))

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
  (:ctrl (hotkey id)))

(defn uses-alt? [id]
  (:alt (hotkey id)))

(defn uses-no-modifiers? [id]
  (let [{:keys [alt ctrl] :as config} (hotkey id)]
    (and config (not alt) (not ctrl))))

(defn clear-suggestions!
  "Clear the suggestion set.
   This should be done before recording new suggestions."
  []
  (swap! *db assoc :suggestions #{}))

(defn after-action! []
  (when-let [after-action (:after-action @*db)]
    (after-action)))

(defn execute!
  "Executes an action by ID. Returns true if executed, false otherwise."
  [id]
  (if-let [action (action-handler id)]
    (do (action)
        (after-action!)
        true)
    false))

(defn active?
  "Is the current action active based on the pressed key?
  If we recieve the fake hotkey :record-suggestions, add this
  action to the current suggestion set. "
  [id key]
  (when (c/valid? (:when (get (config) id)))
    (if (= key :record-suggestions)
      (do (swap! *db assoc :suggestions (conj (suggestions) id))
          false)
      (key-matches? key (hotkey id)))))

(defn get-hotkey-suggestions
  "Get active actions sorted by display."
  []
  (->> (suggestions)
       (map (fn [id] {:key-display (hotkey-display id)
                      :key (hotkey id)
                      :display (display id)
                      :id id}))
       (sort-by :display)))

(defn search-actions [query]
  (->> (seq (config))
       (filter (fn [[_ config]] (str/includes?
                                 (str/lower-case (:display config))
                                 (-> query
                                     str/trim
                                     str/lower-case))))
       (filter (fn [[_ config]] (and
                                 (:searchable config)
                                 (c/valid? (:when config)))))
       (map (fn [[id config]] {:display (:display config) :id id}))))

(defn init! [state]
  (reset! *db (merge initial-state state)))
