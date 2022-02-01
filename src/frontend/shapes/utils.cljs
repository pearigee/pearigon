(ns frontend.shapes.utils)

(defn apply-selected-style
  [shape class]
  (if (:selected shape)
    (str class " selected")
    class))

(defn new-shape-id []
  (str "shape-" (random-uuid)))
