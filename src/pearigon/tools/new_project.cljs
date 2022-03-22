(ns pearigon.tools.new-project
  (:require
   [pearigon.state.actions :as actions]
   [pearigon.state.tools :as tools]
   [pearigon.tools.protocol :refer [OnKeypress]]
   [pearigon.utils.local-storage :as ls]))

(defn clear-and-reload []
  (ls/set-item! ls/active-project-key {})
  (js/location.reload))

(defrecord NewProject [display]

  OnKeypress
  (on-keypress [_ key]
    (cond
      (actions/active? :yes key)
      (clear-and-reload)

      (actions/active? :no key)
      (tools/pop-tool!))))

(defn new-project []
  (tools/push-tool! (->NewProject "New Project: Are you sure?")))
