(ns frontend.tools.save
  (:require [frontend.state.core :as state]
            [frontend.utils.file-system :as fs]))

(defn save []
  (fs/save-edn "project.edn" (state/save-state)))
