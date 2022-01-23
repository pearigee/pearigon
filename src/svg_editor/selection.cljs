(ns svg-editor.selection
  (:require [svg-editor.state :as state]
            [clojure.string :as str]))

(defn select-from-mouse-event! [s event]
  (let [target-id (:target-id event)]
    (if (str/starts-with? target-id "shape-")
      (do (when-not (:shift event) (state/deselect-all! s))
          (state/select-id! s target-id))
      (when (and (= (:target-id event) "svg-root")
                 (not (:shift event)))
        (state/deselect-all! s)))))
