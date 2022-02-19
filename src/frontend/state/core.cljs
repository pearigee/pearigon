(ns frontend.state.core
  "The state in this namespace will be persisted when saved.
  Changes to this state will be recorded for undo/redo.

  Anything that doesn't fit with the criteria above should live
  elsewhere (i.e. mouse position, keyboard state)"
  (:require
   [clojure.string :as str]
   [com.rpl.specter :as sp :include-macros true]
   [reagent.core :as r]
   [frontend.shapes.protocol :refer [OnSelect on-select]]
   [frontend.utils.styles :as styles]
   [frontend.utils.layers :as layers]
   [frontend.math :refer [v+]]))

(def initial-state
  {:shapes {}
   ;; When a shape ID is present in this map, it is rendered
   ;; in place of the original. This is intended for tool previews.
   :shape-preview-override {}
   :draw-order []
   :default-styles styles/default-styles
   :view-pos [0 0]
   :view-dimensions [0 0]
   :view-zoom 1
   :panel nil})

(def ^:dynamic *db* (r/atom initial-state))

(defn get-shape [id]
  ;; Order matters in the multi-path below. The nested structure should
  ;; be searched first.
  (sp/select-first [:shapes
                    (sp/multi-path
                     [sp/MAP-VALS :points sp/ALL #(= id (:id %))]
                     [id])]
                   @*db*))

(defn get-draw-order []
  (:draw-order @*db*))

(defn get-preview [id]
  (-> @*db* :shape-preview-override (get id)))

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

(defn get-panel []
  (:panel @*db*))

(defn get-view-pos []
  (:view-pos @*db*))

(defn get-view-dimensions []
  (:view-dimensions @*db*))

(defn get-view-zoom []
  (:view-zoom @*db*))

(defn get-view-pos-with-zoom []
  (let [[vx vy] (get-view-pos)
        [dx dy] (get-view-dimensions)
        z (get-view-zoom)
        zpos [(+ vx (/ (* dx (- 1 z)) 2))
              (+ vy (/ (* dy (- 1 z)) 2))]]
    zpos))

(defn get-view-dim-with-zoom []
  (let [[dx dy] (get-view-dimensions)
        z (get-view-zoom)
        zdim [(* dx z) (* dy z)]]
    zdim))

(defn get-view-zoom-scale []
  (let [[dx _] (get-view-dimensions)
        [zdx _] (get-view-dim-with-zoom)]
    (/ dx zdx)))

(defn get-selected []
  (sp/select [:shapes
              sp/MAP-VALS
              (sp/multi-path [#(:selected %)]
                             [:points sp/ALL #(:selected %)])] @*db*))

(defn selected-paths []
  (filter :points (get-selected)))

(defn default-styles []
  (:default-styles @*db*))

(defn selected? [id]
  (:selected (get-shape id)))

(defn map-shapes! [f]
  (swap! *db*
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
  (swap! *db* assoc :shape-preview-override
         (let [shapes (map f (get-selected))]
           (reduce #(assoc %1 (:id %2) %2) {} shapes))))

(defn clear-shape-preview! []
  (swap! *db* assoc :shape-preview-override {}))

(defn set-draw-order! [order]
  (swap! *db* assoc :draw-order order))

(defn conj-draw-order [id]
  (set-draw-order! (conj (get-draw-order) id)))

(defn set-shape! [sid shape]
  ;; Order matters in this multi-path. We must search the nested structure
  ;; before deleting the parent.
  (swap!
   *db*
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
  (swap! *db* assoc :default-styles styles))

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

(defn set-view-dimensions! [dim]
  (swap! *db* assoc :view-dimensions dim))

(defn move-up-draw-order! [id]
  (swap! *db* assoc :draw-order (layers/move-up (get-draw-order) id)))

(defn move-down-draw-order! [id]
  (swap! *db* assoc :draw-order (layers/move-down (get-draw-order) id)))

(defn update-view-size! []
  ;; TODO: Figure out how to remove this DOM reference.
  (let [svg (js/document.getElementById "svg-root")
        height (.-clientHeight svg)
        width (.-clientWidth svg)]
    (set-view-dimensions! [width height])))

(defn set-panel! [panel]
  (swap! *db* assoc :panel panel))

(defn move-view-pos! [delta-vec]
  (swap! *db* assoc :view-pos (v+ (:view-pos @*db*) delta-vec)))

;; TODO: Constrain zoom to valid values (i.e. viewBox has positive area).
(defn view-zoom! [delta]
  (swap! *db* assoc :view-zoom (+ (:view-zoom @*db*) (/ delta 100))))

(defn add-shape!
  [{:keys [id] :as shape} & {:keys [selected? draw-order?]
                             :or {selected? true
                                  draw-order? true}}]
  (swap! *db* update-in [:shapes] assoc id
         (merge shape {:styles (default-styles)}))
  (when selected? (select-id! id))
  (when draw-order? (conj-draw-order id)))
