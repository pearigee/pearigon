(ns pearigon.tools.open
  (:require
   [pearigon.state.core :as state]
   [pearigon.utils.edn :as edn]
   [pearigon.utils.file-system :as fs]))

(defn open []
  (fs/open-project
   (fn [file]
     (let [result (edn/read-string file)]
       (state/apply-save-state! result)))))
