(ns frontend.tools.open
  (:require
   [frontend.utils.edn :as edn]
   [frontend.state.core :as state]
   [frontend.utils.file-system :as fs]))

(defn open []
  (fs/open-project
   (fn [file]
     (let [result (edn/read-string file)]
       (state/apply-save-state! result)))))
