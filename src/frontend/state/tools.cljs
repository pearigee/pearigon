(ns frontend.state.tools
  (:require [reagent.core :as r]
            [com.rpl.specter :as sp]))

(def initial-state {:tool-stack []})

(def ^:dynamic *db* (r/atom initial-state))

(defn get-tool []
  (last (:tool-stack @*db*)))

(defn get-tool-stack []
  (:tool-stack @*db*))

(defn push-tool! [tool]
  (swap! *db* assoc :tool-stack (conj (:tool-stack @*db*) tool))
  (js/console.log "Tool pushed:" (:tool-stack @*db*)))

(defn pop-tool! []
  (when-not (empty? (:tool-stack @*db*))
    (swap! *db* assoc :tool-stack (pop (:tool-stack @*db*)))
    (js/console.log "Tool popped:" (:tool-stack @*db*))))

(defn update-tool! [tool]
  (swap! *db* assoc :tool-stack
         (sp/setval [sp/LAST] tool (:tool-stack @*db*))))
