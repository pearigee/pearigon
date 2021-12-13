(ns svg-editor.tools
  (:require [svg-editor.state :as state]))

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
        %))
    (js/console.log (:shapes @state))))

(defn finish-grab [state]
  (state/map-shapes
   state
   #(if (:selected %)
      (merge % {:x (+ (:x %) (:offset-x %))
                :offset-x 0
                :y (+ (:y %) (:offset-y %))
                :offset-y 0})
      %))
  (js/console.log (:shapes @state))
  (state/set-tool state nil))