(ns pearigon.utils.async
  (:require
   [clojure.core.async :refer [<! >! alts! chan close! go-loop timeout]]))

(defn debounce [in ms]
  (let [out (chan)]
    (go-loop [last-val nil]
      (let [val   (if (nil? last-val) (<! in) last-val)
            timer (timeout ms)
            [new-val ch] (alts! [in timer])]
        (condp = ch
          timer (do (when-not (>! out val)
                      (close! in))
                    (recur nil))
          in (when new-val (recur new-val)))))
    out))
