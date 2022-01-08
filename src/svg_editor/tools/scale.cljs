(ns svg-editor.tools.scale
  (:require
    [svg-editor.keymap :as keys]
    [svg-editor.state :as state]
    [svg-editor.vector :refer [avg dist v+ v-]]))

(defn- scale-mousemove
  [state event]
  (let [tool (state/get-tool state)
        initial-dist (:dist tool)
        center (:center tool)
        impos (:impos tool)
        axis (:axis tool)
        mpos (:pos event)]
    (js/console.log mpos impos)
    (state/map-selected-shapes!
      state
      #(merge % {:offset-scale (case axis
                                 :x [(first (v- mpos impos)) 0]
                                 :y [0 (second (v- mpos impos))]
                                 (let [scale (- (dist mpos center) initial-dist)]
                                   [scale scale]))}))
    (js/console.log (state/get-shapes state))))

(defn apply-scale
  [shape]
  (case (:type shape)
    :rect (merge shape {:dim (v+ (:dim shape) (:offset-scale shape))
                        :offset-scale [0 0]})
    :circle (merge shape {:r (+ (:r shape) (apply max (:offset-scale shape)))
                          :offset-scale [0 0]})
    shape))

(defn- scale-click
  [state]
  (state/map-shapes!
    state
    #(if (:selected %)
       (apply-scale %)
       %))
  (state/set-tool! state nil))

(defn- scale-keypress
  [state key]
  (let [axis (condp = key
               (keys/get-key :scale.x-axis) :x
               (keys/get-key :scale.y-axis) :y
               nil)
        tool (state/get-tool state)]
    (js/console.log "Setting scale axis:" axis)
    (state/set-tool! state (merge tool {:axis axis}))))

(defn scale
  [state]
  (let [selection (state/get-selected state)
        center (avg (map :pos selection))
        {mpos :pos} (state/get-mouse-state state)]
    (when-not (zero? (count selection))
      (state/set-tool! state {:type :scale
                              :display "Scale"
                              :on-mousemove scale-mousemove
                              :on-click scale-click
                              :on-keypress scale-keypress
                              :center center
                              :impos mpos
                              :dist (dist center mpos)
                              :axis nil}))))
