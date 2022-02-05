(ns frontend.selection
  (:require [frontend.state :as state]
            [clojure.string :as str]))

(defn select-from-mouse-event! [event]
  (let [target-id (:target-id event)]
    (if (str/starts-with? target-id "shape-")
      (do (when-not (:shift event) (state/deselect-all!))
          (state/toggle-selected! target-id))
      (when (and (= (:target-id event) "svg-root")
                 (not (:shift event)))
        (state/deselect-all!)))))
