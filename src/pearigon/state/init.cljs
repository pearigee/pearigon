(ns pearigon.state.init
  (:require
   [pearigon.actions.config :as action-config]
   [pearigon.actions.handlers :as action-handlers]
   [pearigon.input.keyboard :as hotkeys]
   [pearigon.state.actions :as actions]
   [pearigon.state.core :as state]
   [pearigon.state.undo :as undo]
   [pearigon.state.viewport :as viewport]
   [pearigon.utils.local-storage :as ls]))

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
