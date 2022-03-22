(ns pearigon.utils.layers)

(defn move-up
  "Moves a value up one place in a vector. If at end, do nothing."
  [xs val]
  (let [index (.indexOf xs val)
        next (inc index)
        length (count xs)]
    (if (and (not= index -1) (< next length))
      (assoc xs next (xs index) index (xs next))
      xs)))

(defn move-down
  "Moves a value down one place in a vector. If at begining, do nothing."
  [xs val]
  (let [index (.indexOf xs val)
        next (dec index)]
    (if (and (not= index -1) (>= next 0))
      (assoc xs next (xs index) index (xs next))
      xs)))
