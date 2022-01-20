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
  [s]
  (:pos (:mouse @s)))

(defn get-tool
  [s]
  (:tool @s))

(defn get-shapes
  [s]
  (:shapes @s))

(defn get-shape-preview-override [s]
  (:shape-preview-override @s))

(defn get-shapes-with-override
  [s]
  (let [shapes (get-shapes s)
        previews (get-shape-preview-override s)]
    (mapv #(if-let [preview (get previews (:id %))]
             preview
             %) shapes)))

(defn get-materials
  [s]
  (:materials @s))

(defn get-material
  [s id]
  (id (:materials @s)))

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
  (filter
   :selected
   (get-shapes s)))

(defn map-shapes!
  [s f]
  (swap! s update-in [:shapes]
         (fn [shapes]
           (mapv f shapes))))

(defn map-selected-shapes!
  [s f]
  (swap! s update-in [:shapes]
         (fn [shapes]
           (mapv (fn [shape]
                   (if (:selected shape)
                     (f shape)
                     shape)) shapes))))

(defn map-selected-shapes-preview!
  [s f]
  (swap! s update-in [:shape-preview-override]
         (fn []
           (let [shapes (map f (get-selected s))]
             (reduce #(assoc %1 (:id %2) %2) {} shapes)))))

(defn clear-shape-preview!
  [s]
  (swap! s assoc :shape-preview-override {}))

(defn deselect-all!
  [s]
  (map-shapes! s #(assoc % :selected false)))

(defn select-id!
  ([s id selected?] (map-shapes! s #(if (= id (:id %))
                                              (merge % {:selected selected?})
                                              %)))
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

(defn set-tool!
  [s tool]
  (js/console.log "Tool selected: " tool)
  (swap! s assoc :tool tool)
  (set-suggestions! s (actions/get-key-suggestions (get-tool s))))

(defn add-shape-and-select!
  [s shape]
  (deselect-all! s)
  (swap! s update-in [:shapes] conj
         (merge shape {:selected true
                       :mat-id (:active-material @s)})))

(defn set-mouse-state!
  [s mouse-s]
  (swap! s assoc :mouse mouse-s))
