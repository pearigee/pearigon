(ns svg-editor.tools
  (:require [svg-editor.state :as state]
            [svg-editor.shapes :as shapes]))

(defn grab [state initial-mouse-state]
  (state/set-tool state {:type :grab
                         :x (:page-x initial-mouse-state)
                         :y (:page-y initial-mouse-state)}))

(defn apply-grab [state mouse-state]
  (let [grab (state/get-tool state)]
    (state/map-shapes
     state
     #(if (:selected %)
        (merge % {:offset-x (- (:page-x mouse-state) (:x grab))
                  :offset-y (- (:page-y mouse-state) (:y grab))})
        %))))

(defn finish-grab [state]
  (state/map-shapes
   state
   #(if (:selected %)
      (merge % {:x (+ (:x %) (:offset-x %))
                :offset-x 0
                :y (+ (:y %) (:offset-y %))
                :offset-y 0})
      %))
  (state/set-tool state nil))

(defn add-shape-keypress [state key]
  (let [mouse (state/get-mouse-state state)
        {x :page-x
         y :page-y} mouse
        shape (case key
                :r (shapes/rect x y 40 40)
                :c (shapes/circle x y 20)
                nil)]
    (js/console.log "Shape selected:" shape)
    (if shape
      (do (state/add-shape state shape)
          (grab state mouse))
      (state/set-tool state nil))))

(defn add-shape [state]
  (state/set-tool state {:type :add-shape
                         :on-keypress add-shape-keypress}))