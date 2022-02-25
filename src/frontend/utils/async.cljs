(ns frontend.utils.async
  (:import [goog.async Debouncer]))

(defn debounce [f interval]
  (let [dbnc (Debouncer. f interval)]
    (fn [& args] (.apply (.-fire dbnc) dbnc (to-array args)))))
