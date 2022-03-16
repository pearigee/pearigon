(ns frontend.state.init
  (:require
   [frontend.actions.config :as action-config]
   [frontend.actions.handlers :as action-handlers]
   [frontend.input.keyboard :as hotkeys]
   [frontend.state.actions :as actions]
   [frontend.state.core :as state]
   [frontend.state.undo :as undo]
   [frontend.state.viewport :as viewport]
   [frontend.utils.local-storage :as ls]))

(defn init-state!
  "Initialize application state.

  The order matters and should reflect any dependencies
  between state namespaces."
  []
  (undo/init!)
  (state/init! (ls/get-item ls/active-project-key))
  (actions/init! {:action->handler action-handlers/action->handler
                  :config action-config/config
                  :eval-hotkey hotkeys/eval-hotkey!
                  ;; Check for new suggestions after actions are executed.
                  :after-action #(hotkeys/record-suggestions!)})
  (viewport/init! (ls/get-item ls/viewport-key))

  ;; Synchronize state to localstorage to preserve on refresh.
  (ls/debounced-sync! state/*db ls/active-project-key 250)
  (ls/debounced-sync! viewport/*db ls/viewport-key 250))
