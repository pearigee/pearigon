(ns e2e.utils.input
  (:require [etaoin.api :as eta]))

(defn ->bool-str [val]
  (if val "true" "false"))

(defn press-key [driver {:keys [code ctrl alt]}]
  (eta/js-execute
   driver
   (format "document.dispatchEvent(
              new KeyboardEvent('keydown',
              {code: \"%s\", ctrlKey: %s, altKey: %s}))"
           code (->bool-str ctrl) (->bool-str alt))))
