(ns frontend.utils.styles)

(defn apply-selected-style
  [shape class]
  (if (:selected shape)
    (str class " selected")
    class))

(def default-styles {:fill? true
                     :fill "#000000"
                     :fill-opacity 1.0
                     :stroke? false
                     :stroke "#000000"
                     :stroke-opacity 1.0
                     :stroke-width 2})

(defn styles->svg-attr
  "Generate the minimum set of SVG properties from the supplied style."
  [styles]
  (let [{:keys [fill?
                fill
                fill-opacity
                stroke?
                stroke
                stroke-opacity
                stroke-width
                stroke-linecap
                stroke-linejoin]} styles]
    (merge
     {}
     (if fill?
       {:fill fill
        :fill-opacity fill-opacity}
       {:fill "none"})
     (when stroke?
       (merge {:stroke stroke
               :stroke-opacity stroke-opacity}
              (when stroke-width {:stroke-width stroke-width})
              (when stroke-linecap {:stroke-linecap stroke-linecap})
              (when stroke-linejoin {:stroke-linejoin stroke-linejoin}))))))

(defn apply-styles [shape style]
  (assoc shape :styles style))
