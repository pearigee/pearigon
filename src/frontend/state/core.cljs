(ns frontend.state.core
  "The state in this namespace will be persisted when saved.
  Changes to this state will be recorded for undo/redo.

  Anything that doesn't fit with the criteria above should live
  elsewhere (i.e. mouse position, keyboard state)"
  (:require
   [clojure.string :as str]
   [clojure.core.async :refer [go-loop <!]]
   [com.rpl.specter :as sp :include-macros true]
   [reagent.core :as r]
   [frontend.shapes.protocol :refer [OnSelect on-select]]
   [frontend.state.undo :as undo]
   [frontend.utils.styles :as styles]
   [frontend.utils.layers :as layers]))

(def initial-state
  {:shapes {}
   ;; When a shape ID is present in this map, it is rendered
   ;; in place of the original. This is intended for tool previews.
   :shape-preview-override {}
   :draw-order []
   :default-styles styles/default-styles})

(def db (r/atom initial-state))

(defn get-shape [id]
  ;; Order matters in the multi-path below. The nested structure should
  ;; be searched first.
  (sp/select-first [:shapes
                    (sp/multi-path
                     [sp/MAP-VALS :points sp/ALL #(= id (:id %))]
                     [id])]
                   @db))

(defn get-draw-order []
  (:draw-order @db))

(defn get-preview [id]
  (-> @db :shape-preview-override (get id)))

(defn get-shapes-with-override []
  (let [result (mapv #(if-let [preview (get-preview %)]
                        preview
                        (get-shape %))
                     (get-draw-order))]
    result))

(defn get-shapes-by-id-with-override [ids]
  (mapv #(if-let [preview (get-preview %)]
           preview
           (get-shape %)) ids))

(defn get-selected []
  (sp/select [:shapes
              sp/MAP-VALS
              (sp/multi-path [#(:selected %)]
                             [:points sp/ALL #(:selected %)])] @db))

(defn selected-paths []
  (filter :points (get-selected)))

(defn default-styles []
  (:default-styles @db))

(defn selected? [id]
  (:selected (get-shape id)))

(defn save-state
  "Returns the data that should be persisted in project files and undo/redo."
  []
  (dissoc @db :shape-preview-override))

(defn apply-save-state!
  "Merges the state supplied with the active model."
  [state]
  (swap! db merge state))

(defn- undoable-swap! [& args]
  (undo/push-undo! (save-state))
  (apply swap! args))

(defn undo! []
  (undo/undo! (save-state)))

(defn redo! []
  (undo/redo! (save-state)))

(defn- map-shapes! [f]
  (undoable-swap! db
         #(sp/multi-transform
           [:shapes
            sp/MAP-VALS
            (sp/multi-path [(sp/terminal f)]
                           [:points sp/ALL (sp/terminal f)])]
           %1)))

(defn map-selected-shapes! [f]
  (map-shapes! #(if (:selected %) (f %) %)))

(defn map-shape-ids! [id-set f]
  (map-shapes! #(if (contains? id-set (:id %)) (f %) %)))

(defn map-selected-shapes-preview! [f]
  (swap! db assoc :shape-preview-override
         (let [shapes (map f (get-selected))]
           (reduce #(assoc %1 (:id %2) %2) {} shapes))))

(defn clear-shape-preview! []
  (swap! db assoc :shape-preview-override {}))

(defn set-draw-order! [order]
  (undoable-swap! db assoc :draw-order (vec order)))

(defn conj-draw-order [id]
  (set-draw-order! (conj (get-draw-order) id)))

(defn set-shape! [sid shape]
  ;; Order matters in this multi-path. We must search the nested structure
  ;; before deleting the parent.
  (undoable-swap!
   db
   (fn [db] (sp/multi-transform
             [:shapes
              sp/MAP-VALS
              (sp/multi-path [:points
                              sp/ALL
                              #(= sid (:id %))
                              (sp/terminal-val shape)]
                             [#(= sid (:id %))
                              (sp/terminal-val shape)])]
             db))))

(defn delete! [& ids]
  (let [id-set (into #{} ids)]
    (set-draw-order! (filter #(not (id-set %)) (get-draw-order))))
  (doseq [id ids]
    (set-shape! id sp/NONE)))

(defn merge-shape! [sid partial-shape]
  (set-shape! sid (merge (get-shape sid) partial-shape)))

(defn deselect-all! []
  (map-shapes! #(assoc % :selected false)))

(defn default-styles! [styles]
  (undoable-swap! db assoc :default-styles styles))

(defn select-id!
  ([id selected?]
   (when-let [shape (get-shape id)]
     (set-shape! id (merge shape {:selected selected?}))
     (when (satisfies? OnSelect shape) (on-select shape))))
  ([id] (select-id! id true)))

(defn toggle-selected! [id]
  (select-id! id (not (selected? id))))

(defn select-from-mouse-event! [event]
  (let [target-id (:target-id event)]
    (if (str/starts-with? target-id "shape-")
      (do (when-not (:shift event) (deselect-all!))
          (toggle-selected! target-id))
      (when (and (= (:target-id event) "svg-root")
                 (not (:shift event)))
        (deselect-all!)))))

(defn move-up-draw-order! [id]
  (set-draw-order! (layers/move-up (get-draw-order) id)))

(defn move-down-draw-order! [id]
  (set-draw-order! (layers/move-down (get-draw-order) id)))

(defn add-shape!
  [{:keys [id] :as shape} & {:keys [selected? draw-order?]
                             :or {selected? true
                                  draw-order? true}}]
  (undoable-swap! db update-in [:shapes] assoc id
         (merge shape {:styles (default-styles)}))
  (when selected? (select-id! id))
  (when draw-order? (conj-draw-order id)))

(defn init! []
  (reset! db initial-state)
  (go-loop [val (<! (undo/on-change-chan))]
    (apply-save-state! val)
    (recur (<! (undo/on-change-chan)))))
