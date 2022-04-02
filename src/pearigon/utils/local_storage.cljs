(ns pearigon.utils.local-storage
  (:require
   [clojure.core.async :refer [<! >! chan go go-loop sliding-buffer]]
   [pearigon.utils.async :refer [debounce]]
   [pearigon.utils.edn :as edn]))

(def active-project-key :active-project)
(def viewport-key :viewport)

(defn set-item! [key value]
  (js/localStorage.setItem (prn-str key) (prn-str value)))

(defn get-item [key]
  (edn/read-string (js/localStorage.getItem (prn-str key))))

(defn debounced-sync! [a key interval]
  (let [input (chan (sliding-buffer 1))
        output (debounce input interval)]
    (go-loop [val (<! output)]
      (set-item! key val)
      (recur (<! output)))
    (add-watch a key (fn [_ _ _ val] (go (>! input val))))))
