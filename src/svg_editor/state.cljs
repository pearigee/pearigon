(ns svg-editor.state
  (:require
   [com.rpl.specter :as sp :include-macros true]
   [svg-editor.actions :as actions]
   [svg-editor.shapes.protocol :refer [OnSelect on-select]]
   [svg-editor.math :refer [v+]]))

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

(defn get-mouse-pos
  [s]
  (:pos (:mouse @s)))

(defn get-tool
  [s]
  (last (:tool @s)))

(defn get-shape [s id]
  (-> @s :shapes (get id)))

(defn get-draw-order [s]
  (:draw-order @s))

(defn get-preview [s id]
  (-> @s :shape-preview-override (get id)))

(defn get-shapes-with-override [s]
  (let [result (mapv #(if-let [preview (get-preview s %)]
                        preview
                        (get-shape s %))
                     (get-draw-order s))]
    result))

(defn get-shapes-ids-with-override [s ids]
  (mapv #(if-let [preview (get-preview s %)]
           preview
           (get-shape s %)) ids))

(defn get-materials
  [s]
  (:materials @s))

(defn get-material
  [s id]
  (get (:materials @s) id))

(defn get-panel
  [s]
  (:panel @s))

(defn get-view-pos
  [s]
  (:view-pos @s))

(defn get-view-dimensions
  [s]
  (:view-dimensions @s))

(defn get-view-zoom
  [s]
  (:view-zoom @s))

(defn get-view-pos-with-zoom
  [s]
  (let [[vx vy] (get-view-pos s)
        [dx dy] (get-view-dimensions s)
        z (get-view-zoom s)
        zpos [(+ vx (/ (* dx (- 1 z)) 2))
              (+ vy (/ (* dy (- 1 z)) 2))]]
    zpos))

(defn get-view-dim-with-zoom
  [s]
  (let [[dx dy] (get-view-dimensions s)
        z (get-view-zoom s)
        zdim [(* dx z) (* dy z)]]
    zdim))

(defn get-view-zoom-scale
  [s]
  (let [[dx _] (get-view-dimensions s)
        [zdx _] (get-view-dim-with-zoom s)]
    (/ dx zdx)))

(defn get-selected [s]
  (sp/select [:shapes sp/MAP-VALS #(:selected %)] @s))

(defn map-shapes! [s f]
  (swap! s #(sp/transform [:shapes sp/MAP-VALS] %2 %1) f))

(defn map-selected-shapes!
  [s f]
  (map-shapes! s #(if (:selected %) (f %) %)))

(defn map-shape-ids! [s id-set f]
  (map-shapes! s #(if (contains? id-set (:id %)) (f %) %)))

(defn map-selected-shapes-preview!
  [s f]
  (swap! s assoc :shape-preview-override
         (let [shapes (map f (get-selected s))]
           (reduce #(assoc %1 (:id %2) %2) {} shapes))))

(defn clear-shape-preview!
  [s]
  (swap! s assoc :shape-preview-override {}))

(defn set-shape! [s sid shape]
  (swap! s update-in [:shapes] assoc sid shape))

(defn deselect-all!
  [s]
  (map-shapes! s #(assoc % :selected false)))

(defn select-id!
  ([s id selected?]
   (when-let [shape (get-shape s id)]
     (set-shape! s id (merge shape {:selected selected?}))
     (when (satisfies? OnSelect shape) (on-select shape s))))
  ([s id] (select-id! s id true)))

(defn set-suggestions!
  [s suggestions]
  (swap! s assoc :suggestions suggestions))

(defn set-view-dimensions!
  [s dim]
  (swap! s assoc :view-dimensions dim))

(defn update-view-size!
  [s]
  ;; TODO: Figure out how to remove this DOM reference.
  (let [svg (js/document.getElementById "svg-root")
        height (.-clientHeight svg)
        width (.-clientWidth svg)]
    (set-view-dimensions! s [width height])))

(defn set-panel!
  [s panel]
  (swap! s assoc :panel panel))

(defn set-material!
  [s id value]
  (swap! s update-in [:materials] assoc id value))

(defn set-active-material!
  [s id]
  (swap! s assoc :active-material id))

(defn move-view-pos!
  [s delta-vec]
  (swap! s assoc :view-pos (v+ (:view-pos @s) delta-vec)))

;; TODO: Constrain zoom to valid values (i.e. viewBox has positive area).
(defn view-zoom!
  [s delta]
  (swap! s assoc :view-zoom (+ (:view-zoom @s) (/ delta 100))))

(defn add-material!
  [s id value]
  (swap! s update-in [:materials] assoc id value))

(defn push-tool! [s tool]
  (swap! s assoc :tool (conj (:tool @s) tool))
  (js/console.log "Tool pushed:" (:tool @s))
  (set-suggestions! s (actions/get-key-suggestions (get-tool s))))

(defn pop-tool! [s]
  (when-not (empty? (:tool @s))
    (swap! s assoc :tool (pop (:tool @s)))
    (js/console.log "Tool popped:" (:tool @s))
    (set-suggestions! s (actions/get-key-suggestions (get-tool s)))))

(defn update-tool! [s tool]
  (swap! s assoc :tool (sp/setval [sp/LAST] tool (:tool @s))))

(defn conj-draw-order [s id]
  (swap! s assoc :draw-order (conj (get-draw-order s) id)))

(defn add-shape!
  [s {:keys [id] :as shape} & {:keys [selected? draw-order? deselect-all?]
                               :or {selected? true
                                    deselect-all? true
                                    draw-order? true}}]
  (when deselect-all? (deselect-all! s))
  (swap! s update-in [:shapes] assoc id
         (merge shape {:selected selected?
                       :mat-id (:active-material @s)}))
  (when draw-order? (conj-draw-order s id)))

(defn set-mouse-state!
  [s mouse-s]
  (swap! s assoc :mouse mouse-s))
