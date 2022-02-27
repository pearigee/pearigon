(ns frontend.state.tools
  (:require
   [com.rpl.specter :as sp]
   [reagent.core :as r]))

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
