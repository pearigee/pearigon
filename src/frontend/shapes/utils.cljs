(ns frontend.shapes.utils)

(defn apply-selected-style
  [shape class]
  (if (:selected shape)
    (str class " selected")
    class))
