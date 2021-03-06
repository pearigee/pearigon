(ns pearigon.state.core
  "The state in this namespace will be persisted when saved.
  Changes to this state will be recorded for undo/redo.

  Anything that doesn't fit with the criteria above should live
  elsewhere (i.e. mouse position, keyboard state)"
  (:require
   [clojure.string :as str]
   [com.rpl.specter :as sp :include-macros true]
   [pearigon.shapes.protocol :refer [on-select OnSelect]]
   [pearigon.state.undo :as undo]
   [pearigon.utils.ids :as ids]
   [pearigon.utils.layers :as layers]
   [pearigon.utils.styles :as styles]
   [medley.core :refer [find-first]]
   [reagent.core :as r]))

(def initial-state
  {:shapes {}
   ;; When a shape ID is present in this map, it is rendered
   ;; in place of the original. This is intended for tool previews.
   :shape-preview-override {}
   :selected #{}
   :draw-order []
   :default-styles styles/default-styles})

(def *db (r/atom initial-state))

(defn get-shapes []
  (:shapes @*db))

(defn get-shape [id]
  (let [path (str/split id #":")]
   (case (count path)
     1 (get (get-shapes) (first path))
     2 (as-> @*db v
         (get-shapes)
         (get v (first path))
         (:points v)
         (find-first #(= (:id %) id) v)))))

(defn get-draw-order []
  (:draw-order @*db))

(defn get-preview [id]
  (-> @*db :shape-preview-override (get id)))

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
  (map #(get-shape %) (:selected @*db)))

(defn selected-paths []
  (filter :points (get-selected)))

(defn default-styles []
  (:default-styles @*db))

(defn selected? [id]
  (contains? (:selected @*db) id))

(defn save-state
  "Returns the data that should be persisted in project files and undo/redo."
  []
  (dissoc @*db :shape-preview-override))

(defn apply-save-state!
  "Merges the state supplied with the active model."
  [state]
  (when state (swap! *db merge state)))

(defn- undoable-swap! [& args]
  (undo/push-undo! (save-state))
  (apply swap! args))

(defn undo! []
  (apply-save-state! (undo/undo! (save-state))))

(defn redo! []
  (apply-save-state! (undo/redo! (save-state))))

(defn- map-shapes! [f]
  (undoable-swap! *db
                  #(sp/multi-transform
                    [:shapes
                     sp/MAP-VALS
                     (sp/multi-path [(sp/terminal f)]
                                    [:points sp/ALL (sp/terminal f)])]
                    %1)))

(defn map-selected-shapes! [f]
  (map-shapes! #(if (selected? (:id %)) (f %) %)))

(defn map-shape-ids! [id-set f]
  (map-shapes! #(if (contains? id-set (:id %)) (f %) %)))

(defn map-selected-shapes-preview! [f]
  (swap! *db assoc :shape-preview-override
         (let [shapes (map f (get-selected))]
           (reduce #(assoc %1 (:id %2) %2) {} shapes))))

(defn clear-shape-preview! []
  (swap! *db assoc :shape-preview-override {}))

(defn set-draw-order! [order]
  (undoable-swap! *db assoc :draw-order (vec order)))

(defn conj-draw-order [id]
  (set-draw-order! (conj (get-draw-order) id)))

(defn set-shape! [sid shape]
  ;; Order matters in this multi-path. We must search the nested structure
  ;; before deleting the parent.
  (undoable-swap!
   *db
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
  (undoable-swap! *db assoc :selected #{}))

(defn default-styles! [styles]
  (undoable-swap! *db assoc :default-styles styles))

(defn select-id!
  ([id selected]
   (when-let [shape (get-shape id)]
     (if selected
       (undoable-swap! *db assoc :selected (conj (:selected @*db) id))
       (undoable-swap! *db assoc :selected (disj (:selected @*db) id)))
     (when (satisfies? OnSelect shape) (on-select shape))))
  ([id] (select-id! id true)))

(defn toggle-selected! [id]
  (select-id! id (not (selected? id))))

(defn select-from-mouse-event! [event]
  ;; Only select on left click.
  (when (= (:button event) 0)
    (let [target-id (:target-id event)]
      (if (str/starts-with? target-id "shape-")
        (do (when-not (:shift event) (deselect-all!))
            (toggle-selected! target-id))
        (when (and (= (:target-id event) "svg-root")
                   (not (:shift event)))
          (deselect-all!))))))

(defn move-up-draw-order! [id]
  (set-draw-order! (layers/move-up (get-draw-order) id)))

(defn move-down-draw-order! [id]
  (set-draw-order! (layers/move-down (get-draw-order) id)))

(defn- shape-with-new-ids [shape]
  (let [id (ids/shape-id)
        points (:points shape)
        points-with-id (when points
                         (map #(assoc % :id (ids/point-id id)) points))]
    (merge shape
           {:id id}
           (when points-with-id {:points points-with-id}))))

(defn add-shape!
  [shape & {:keys [selected? draw-order? default-styles?]
            :or {selected? true
                 draw-order? true
                 default-styles? true}}]
  (let [{:keys [id] :as shape} (shape-with-new-ids shape)
        shape-with-styles (merge shape
                                 (when default-styles?
                                   {:styles (default-styles)}))]
    (undoable-swap! *db assoc-in [:shapes id]
                    shape-with-styles)
    (when selected? (select-id! id))
    (when draw-order? (conj-draw-order id))
    shape-with-styles))

(defn init! [state]
  (reset! *db (merge initial-state state)))
