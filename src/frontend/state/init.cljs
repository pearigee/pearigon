(ns frontend.state.init
  (:require [frontend.state.core :as state]
            [frontend.state.undo :as undo]
            [frontend.utils.local-storage :as ls]))

(defn init-state!
  "Initialize application state.

  The order matters and should reflect any dependencies
  between state namespaces."
  []
  (let [active-project (ls/get-item ls/active-project-key)]
    (undo/init!)
    (state/init! :with-state active-project)

    ;; Synchronize state to localstorage to preserve on refresh.
    (ls/debounced-sync! state/db ls/active-project-key 250)))
