(ns frontend.state
  (:require
   [com.rpl.specter :as sp :include-macros true]
   [reagent.core :as r]
   [frontend.actions :as actions]
   [frontend.shapes.protocol :refer [OnSelect on-select]]
   [frontend.math :refer [v+]]))

(defn initial-state
  []
  {:shapes {}
   ;; When a shape ID is present in this map, it is rendered
   ;; in place of the original. This is intended for tool previews.
   :shape-preview-override {}
   :draw-order []
   :mouse {:pos [0 0]}
   :materials {:default {:display "Default"
                         :color "#000"}}
   :active-material :default
   :view-pos [0 0]
   :view-dimensions [0 0]
   :view-zoom 1
   :panel nil
   :tool []
   :next-id 0
   :suggestions (actions/get-key-suggestions nil)})

(def ^:dynamic *db* (r/atom (initial-state)))

(defn get-mouse-pos []
  (:pos (:mouse @*db*)))

(defn get-tool []
  (last (:tool @*db*)))

(defn get-tool-stack []
  (:tool @*db*))

(defn get-shape [id]
  (-> @*db* :shapes (get id)))

(defn get-draw-order []
  (:draw-order @*db*))

(defn get-suggestions []
  (:suggestions @*db*))

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

(defn get-materials []
  (:materials @*db*))

(defn get-material [id]
  (get (:materials @*db*) id))

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
  (sp/select [:shapes sp/MAP-VALS #(:selected %)] @*db*))

(defn map-shapes! [f]
  (swap! *db* #(sp/transform [:shapes sp/MAP-VALS] %2 %1) f))

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

(defn set-shape! [sid shape]
  (swap! *db* update-in [:shapes] assoc sid shape))

(defn merge-shape! [sid partial-shape]
  (set-shape! sid (merge (get-shape sid) partial-shape)))

(defn deselect-all! []
  (map-shapes! #(assoc % :selected false)))

(defn select-id!
  ([id selected?]
   (when-let [shape (get-shape id)]
     (set-shape! id (merge shape {:selected selected?}))
     (when (satisfies? OnSelect shape) (on-select shape))))
  ([id] (select-id! id true)))

(defn set-suggestions! [suggestions]
  (swap! *db*  assoc :suggestions suggestions))

(defn set-view-dimensions! [dim]
  (swap! *db* assoc :view-dimensions dim))

(defn update-view-size! []
  ;; TODO: Figure out how to remove this DOM reference.
  (let [svg (js/document.getElementById "svg-root")
        height (.-clientHeight svg)
        width (.-clientWidth svg)]
    (set-view-dimensions! [width height])))

(defn set-panel! [panel]
  (swap! *db* assoc :panel panel))

(defn set-material! [id value]
  (swap! *db* update-in [:materials] assoc id value))

(defn set-active-material! [id]
  (swap! *db* assoc :active-material id))

(defn move-view-pos! [delta-vec]
  (swap! *db* assoc :view-pos (v+ (:view-pos @*db*) delta-vec)))

;; TODO: Constrain zoom to valid values (i.e. viewBox has positive area).
(defn view-zoom! [delta]
  (swap! *db* assoc :view-zoom (+ (:view-zoom @*db*) (/ delta 100))))

(defn add-material!
  [id value]
  (swap! *db* update-in [:materials] assoc id value))

(defn push-tool! [tool]
  (swap! *db* assoc :tool (conj (:tool @*db*) tool))
  (js/console.log "Tool pushed:" (:tool @*db*))
  (set-suggestions! (actions/get-key-suggestions (get-tool))))

(defn pop-tool! []
  (when-not (empty? (:tool @*db*))
    (swap! *db* assoc :tool (pop (:tool @*db*)))
    (js/console.log "Tool popped:" (:tool @*db*))
    (set-suggestions! (actions/get-key-suggestions (get-tool)))))

(defn update-tool! [tool]
  (swap! *db* assoc :tool (sp/setval [sp/LAST] tool (:tool @*db*))))

(defn conj-draw-order [id]
  (swap! *db* assoc :draw-order (conj (get-draw-order) id)))

(defn add-shape!
  [{:keys [id] :as shape} & {:keys [selected? draw-order? deselect-all?]
                             :or {selected? true
                                  deselect-all? true
                                  draw-order? true}}]
  (when deselect-all? (deselect-all!))
  (swap! *db* update-in [:shapes] assoc id
         (merge shape {:selected selected?
                       :mat-id (:active-material @*db*)}))
  (when draw-order? (conj-draw-order id)))

(defn set-mouse-state! [mouse-s]
  (swap! *db* assoc :mouse mouse-s))
