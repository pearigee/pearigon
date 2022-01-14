(ns svg-editor.shapes.utils)

(defn apply-selected-style
  [shape]
  (if (:selected shape)
    {:stroke "#1fd2ff"
     :stroke-width  "3px"
     :stroke-dasharray "4"}
    {}))

(defn new-shape-id []
  (str "shape-" (random-uuid)))
