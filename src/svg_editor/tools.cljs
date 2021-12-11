(ns svg-editor.tools
  (:require [svg-editor.state :as state]))

(defn set-tool [state tool]
  (swap! state assoc :tool tool))

(defn grab [mouse-state]
  {:type :grab
   :x (:page-x mouse-state)
   :y (:page-y mouse-state)})

(defn apply-grab [state mouse-state]
  (let [grab (:tool @state)]
    (state/map-shapes
     state
     #(if (:selected %)
        (merge % {:offset-x (- (:page-x mouse-state) (:x grab))
                  :offset-y (- (:page-y mouse-state) (:y grab))})
        %))))

(defn finish-grab [state]
  (state/map-shapes
   state
   #(if (:select %)
      (merge % {:x (+ (:x %) (:offset-x %))
                :offset-x 0
                :y (+ (:y 0) (:offset-y %))
                :offset-y 0})
      %))
  (set-tool state nil))