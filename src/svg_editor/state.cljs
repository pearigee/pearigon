(ns svg-editor.state
  (:require
    [svg-editor.actions :as actions]
    [svg-editor.math :refer [v+]]))

(defn initial-state
  []
  {:shapes []
   ;; When a shape ID is present in this map, it is rendered
   ;; in place of the original. This is intended for tool previews.
   :shape-preview-override {}
   :mouse {:pos [0 0]}
   :materials {:default {:display "Default"
                         :color "#000"}}
   :active-material :default
   :view-pos [0 0]
   :view-dimensions [0 0]
   :view-zoom 1
   :panel nil
   :tool nil
   :next-id 0
   :suggestions (actions/get-key-suggestions nil)})

(defn get-mouse-pos
  [state]
  (:pos (:mouse @state)))

(defn get-tool
  [state]
  (:tool @state))

(defn get-shapes
  [state]
  (:shapes @state))

(defn get-shape-preview-override [state]
  (:shape-preview-override @state))

(defn get-shapes-with-override
  [state]
  (let [shapes (get-shapes state)
        previews (get-shape-preview-override state)]
    (mapv #(if-let [preview (get previews (:id %))]
             preview
             %) shapes)))

(defn get-materials
  [state]
  (:materials @state))

(defn get-material
  [state id]
  (id (:materials @state)))

(defn get-panel
  [state]
  (:panel @state))

(defn get-view-pos
  [state]
  (:view-pos @state))

(defn get-view-dimensions
  [state]
  (:view-dimensions @state))

(defn get-view-zoom
  [state]
  (:view-zoom @state))

(defn get-view-pos-with-zoom
  [state]
  (let [[vx vy] (get-view-pos state)
        [dx dy] (get-view-dimensions state)
        z (get-view-zoom state)
        zpos [(+ vx (/ (* dx (- 1 z)) 2))
              (+ vy (/ (* dy (- 1 z)) 2))]]
    zpos))

(defn get-view-dim-with-zoom
  [state]
  (let [[dx dy] (get-view-dimensions state)
        z (get-view-zoom state)
        zdim [(* dx z) (* dy z)]]
    zdim))

(defn get-view-zoom-scale
  [state]
  (let [[dx _] (get-view-dimensions state)
        [zdx _] (get-view-dim-with-zoom state)]
    (/ dx zdx)))

(defn get-selected [state]
  (filter
    :selected
    (get-shapes state)))

(defn map-shapes!
  [state f]
  (swap! state update-in [:shapes]
         (fn [shapes]
           (mapv f shapes))))

(defn map-selected-shapes!
  [state f]
  (swap! state update-in [:shapes]
         (fn [shapes]
           (mapv (fn [shape]
                   (if (:selected shape)
                     (f shape)
                     shape)) shapes))))

(defn map-selected-shapes-preview!
  [state f]
  (swap! state update-in [:shape-preview-override]
         (fn []
           (let [shapes (map f (get-selected state))]
             (reduce #(assoc %1 (:id %2) %2) {} shapes)))))

(defn clear-shape-preview!
  [state]
  (swap! state assoc :shape-preview-override {}))

(defn deselect-all!
  [state]
  (map-shapes! state #(assoc % :selected false)))

(defn select-id!
  ([state id selected?] (map-shapes! state #(if (= id (:id %))
                                              (merge % {:selected selected?})
                                              %)))
  ([state id] (select-id! state id true)))

(defn set-suggestions!
  [state suggestions]
  (swap! state assoc :suggestions suggestions))

(defn set-view-dimensions!
  [state dim]
  (swap! state assoc :view-dimensions dim))

(defn update-view-size!
  [state]
  ;; TODO: Figure out how to remove this DOM reference.
  (let [svg (js/document.getElementById "svg-root")
        height (.-clientHeight svg)
        width (.-clientWidth svg)]
    (set-view-dimensions! state [width height])))

(defn set-panel!
  [state panel]
  (swap! state assoc :panel panel))

(defn set-material!
  [state id value]
  (swap! state update-in [:materials] assoc id value))

(defn set-active-material!
  [state id]
  (swap! state assoc :active-material id))

(defn move-view-pos!
  [state delta-vec]
  (swap! state assoc :view-pos (v+ (:view-pos @state) delta-vec)))

;; TODO: Constrain zoom to valid values (i.e. viewBox has positive area).
(defn view-zoom!
  [state delta]
  (swap! state assoc :view-zoom (+ (:view-zoom @state) (/ delta 100))))

(defn add-material!
  [state id value]
  (swap! state update-in [:materials] assoc id value))

(defn set-tool!
  [state tool]
  (js/console.log "Tool selected: " tool)
  (swap! state assoc :tool tool)
  (set-suggestions! state (actions/get-key-suggestions (get-tool state))))

(defn add-shape-and-select!
  [state shape]
  (deselect-all! state)
  (swap! state update-in [:shapes] conj
         (merge shape {:selected true
                       :mat-id (:active-material @state)})))

(defn set-mouse-state!
  [state mouse-state]
  (swap! state assoc :mouse mouse-state))
