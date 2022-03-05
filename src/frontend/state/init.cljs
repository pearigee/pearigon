(ns frontend.state.init
  (:require [frontend.state.core :as state]
            [frontend.state.undo :as undo]
            [frontend.state.actions :as actions]
            [frontend.actions.config :as action-config]
            [frontend.actions.handlers :as action-handlers]
            [frontend.utils.local-storage :as ls]))

(defn init-state!
  "Initialize application state.

  The order matters and should reflect any dependencies
  between state namespaces."
  []
  (let [active-project (ls/get-item ls/active-project-key)]
    (undo/init!)
    (state/init! :with-state active-project)
    (actions/init! {:action->handler action-handlers/action->handler
                    :config action-config/config})

    ;; Synchronize state to localstorage to preserve on refresh.
    (ls/debounced-sync! state/db ls/active-project-key 250)))
