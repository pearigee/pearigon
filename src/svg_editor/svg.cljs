(ns svg-editor.svg)

(defn apply-selected-style [shape]
  (if (:selected shape)
    {:stroke "lime"
     :stroke-width  "5px"}
    {}))

(defn shape->svg [shape]
  (case (:type shape)
    :circle [:circle (merge {:id (:id shape)
                             :cx (+ (:x shape) (:offset-x shape))
                             :cy (+ (:y shape) (:offset-y shape))
                             :r (:r shape)}
                            (apply-selected-style shape))]
    :rect [:rect (merge {:id (:id shape)
                         :x (+ (:x shape) (:offset-x shape))
                         :y (+ (:y shape) (:offset-y shape))
                         :width (:w shape)
                         :height (:h shape)}
                        (apply-selected-style shape))]))