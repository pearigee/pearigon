(ns svg-editor.shapes.utils)

(defn apply-selected-style
  [shape]
  (if (:selected shape)
    {:class "selected"}
    {}))

(defn new-shape-id []
  (str "shape-" (random-uuid)))
