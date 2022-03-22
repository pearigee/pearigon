(ns pearigon.math)

(def pi js/Math.PI)

(defn sin [x]
  (js/Math.sin x))

(defn cos [x]
  (js/Math.cos x))

(defn atan2 [a b]
  (js/Math.atan2 a b))

(defn v+
  ([a b]
   (mapv + a b))
  ([a b & more]
   (reduce v+ (v+ a b) more)))

(defn v-
  ([a b]
   (mapv - a b))
  ([a b & more]
   (reduce v- (v- a b) more)))

(defn v*
  ([a b]
   (mapv * a b))
  ([a b & more]
   (reduce v* (v* a b) more)))

(defn sum
  [vecs]
  (reduce v+ vecs))

(defn avg
  [vecs]
  (let [[a b] (sum vecs)
        len (count vecs)]
    [(/ a len) (/ b len)]))

(defn norm
  [vec]
  (js/Math.sqrt (reduce + (v* vec vec))))

(defn dist
  [a b]
  (norm (v- a b)))

(defn dot [a b]
  (apply + (v* a b)))

(defn mv* [m v]
  ;; For our usecase, the last row of the 3x3 matrix is constant: [0 0 1].
  ;; The same is true for the for the 3D vector, the final entry is 1.
  ;; As a result, we can ignore the final row of the matrix.
  (let [[[m1 m2 m3]
         [m4 m5 m6]] m
        [x y] v]
    [(+ (* m1 x) (* m2 y) (* m3 1))
     (+ (* m4 x) (* m5 y) (* m6 1))]))

(defn mm*
  ([a b]
   ;; The final row is constant for our use case: [0 0 1]. As a result,
   ;; we can ignore that row in the function below.
   (let [[ar1 ar2] a
         [[b1 b2 b3]
          [b4 b5 b6]] b]
     [[(dot ar1 [b1 b4 0])
       (dot ar1 [b2 b5 0])
       (dot ar1 [b3 b6 1])]
      [(dot ar2 [b1 b4 0])
       (dot ar2 [b2 b5 0])
       (dot ar2 [b3 b6 1])]]))
  ([a b & more]
   (reduce mm* (mm* a b) more)))

(defn transform
  "Generates a transformation matrix from the input (apply left to right)."
  [& txs]
  (apply mm* (reverse txs)))

(defn translate [x y]
  [[1 0 x]
   [0 1 y]])

(defn scale [x y]
  [[x 0 0]
   [0 y 0]])

(defn rotate [rad]
  [[(cos rad) (sin rad) 0]
   [(- (sin rad)) (cos rad) 0]])
