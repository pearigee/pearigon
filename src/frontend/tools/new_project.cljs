(ns frontend.tools.new-project
  (:require [frontend.tools.protocol :refer [OnKeypress]]
            [frontend.state.actions :as actions]
            [frontend.state.tools :as tools]
            [frontend.utils.local-storage :as ls]))

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
