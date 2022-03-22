(ns pearigon.tools.save
  (:require
   [pearigon.state.core :as state]
   [pearigon.utils.file-system :as fs]))

(defn save []
  (fs/save-edn "project.edn" (state/save-state)))
