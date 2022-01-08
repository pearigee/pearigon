(ns svg-editor.vector)

(defn v+
  [a b]
  (mapv + a b))

(defn v-
  [a b]
  (mapv - a b))

(defn v*
  [a b]
  (mapv * a b))

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

(comment
  (v+ [2 2] [1 1])
  (v- [2 2] [1 1])
  (v* [2 2] [2 2])
  (norm [1 1]) ; -> 1.41...
  (sum [[1 1] [2 2] [3 3] [4 4] [5 5]])
  (avg [[1 1] [2 2] [3 3] [4 4] [5 5]])
  (dist [1 1] [2 2]) ; -> 1.41
  )
