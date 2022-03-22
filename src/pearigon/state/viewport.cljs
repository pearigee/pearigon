(ns pearigon.state.viewport
  (:require
   [pearigon.math :as m]
   [reagent.core :as r]))

(def ^:private initial-state {:pos [0 0]
                              :dim [0 0]
                              :zoom 1
                              :search-showing false
                              :code-showing false
                              :panel nil})

(def *db (r/atom initial-state))

(defn pos []
  (:pos @*db))

(defn dim []
  (:dim @*db))

(defn zoom []
  (:zoom @*db))

(defn panel []
  (:panel @*db))

(defn search-showing? []
  (:search-showing @*db))

(defn code-showing? []
  (:code-showing @*db))

(defn pos-with-zoom []
  (let [[vx vy] (pos)
        [dx dy] (dim)
        z (zoom)
        zpos [(+ vx (/ (* dx (- 1 z)) 2))
              (+ vy (/ (* dy (- 1 z)) 2))]]
    zpos))

(defn dim-with-zoom []
  (let [[dx dy] (dim)
        z (zoom)
        zdim [(* dx z) (* dy z)]]
    zdim))

(defn zoom-scale []
  (let [[dx _] (dim)
        [zdx _] (dim-with-zoom)]
    (/ dx zdx)))

(defn dim! [dim]
  (swap! *db assoc :dim dim))

(defn on-resize! []
  ;; TODO: Figure out how to remove this DOM reference.
  (let [svg (js/document.getElementById "svg-root")
        height (.-clientHeight svg)
        width (.-clientWidth svg)]
    (dim! [width height])))

(defn panel! [panel]
  (swap! *db assoc :panel panel))

(defn move-pos! [delta-vec]
  (swap! *db assoc :pos (m/v+ (:pos @*db) delta-vec)))

;; TODO: Constrain zoom to valid values (i.e. viewBox has positive area).
(defn zoom! [delta]
  (swap! *db assoc :zoom (+ (:zoom @*db) (/ delta 100))))

(defn reset-zoom! []
  (swap! *db assoc :zoom 1))

(defn toggle-search-showing! []
  (swap! *db assoc :search-showing (not (search-showing?))))

(defn toggle-code-showing! []
  (swap! *db assoc :code-showing (not (code-showing?))))

(defn init! [state]
  (reset! *db (merge initial-state state)))
