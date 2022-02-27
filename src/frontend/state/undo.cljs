(ns frontend.state.undo
  (:require [frontend.utils.async :refer [debounce]]
            [clojure.core.async :refer [chan go >! go-loop <!]]
            [reagent.core :as r]))

;; Values initialized in init! below.
(def undo-db (r/atom {}))

(defn- on-change! [val]
  (go (>! (:on-change-chan @undo-db) val)))

(defn on-change-chan []
  (:on-change-chan @undo-db))

(defn- trim-undo-stack
  "Limit the size of the undo stack."
  [stack]
  (into [] (take-last 50 stack)))

(defn- push-undo-impl! [val & {:keys [reset-redo?]
                               :or {reset-redo? true}}]
  (swap! undo-db assoc
         :undo (trim-undo-stack (conj (:undo @undo-db) val)))
  ;; Reset the undo state by default. The state is stale after new
  ;; modifications.
  (when reset-redo? (swap! undo-db assoc :redo [])))

(defn push-undo! [val]
  (go (>! (:undo-chan @undo-db) val)))

(defn- push-redo! [val]
  (swap! undo-db assoc :redo (conj (:redo @undo-db) val)))

(defn undo!
  "Trigger an undo action.
  Takes the current value so that the undo can be redo'ed."
  [val]
  (let [new-state (peek (:undo @undo-db))
        new-undo (when new-state (pop (:undo @undo-db)))]
    (when new-state
      (push-redo! val)
      (on-change! new-state)
      (swap! undo-db assoc :undo new-undo))))

(defn redo!
  "Trigger a redo action.
  Takes the current value so the redo can be undo'ed."[val]
  (let [new-state (peek (:redo @undo-db))
        new-redo (when new-state (pop (:redo @undo-db)))]
    (when new-state
      (push-undo-impl! val :reset-redo? false)
      (on-change! new-state)
      (swap! undo-db assoc :redo new-redo))))

(defn init! [& {:keys [debounce? debounce-ms]
                :or {debounce? true
                     debounce-ms 250}}]
  (let [undo-chan (chan)
        initial-state {:undo []
                       :redo []
                       :undo-chan undo-chan
                       :on-change-chan (chan)}]
    (reset! undo-db initial-state)

    ;; Subscribe to incoming undo values, optionally with a debounce.
    (let [debounced-chan (if debounce?
                           (debounce undo-chan debounce-ms)
                           undo-chan)]
      (go-loop [val (<! debounced-chan)]
        (push-undo-impl! val)
        (recur (<! debounced-chan))))))
