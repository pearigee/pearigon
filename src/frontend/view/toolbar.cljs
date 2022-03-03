(ns frontend.view.toolbar
  (:require
   [frontend.actions.handlers :refer [execute-action!]]
   [frontend.state.actions :as actions]
   [frontend.state.viewport :as viewport]))

(defn toolbar []
  [:div.toolbar
   [:button.button.is-primary.has-tooltip-left
    {:on-click #(execute-action! :save)
     :data-tooltip (actions/get-display :save)}
    [:span.icon.material-icons "save"]]
   [:button.button.is-primary.has-tooltip-left
    {:on-click #(execute-action! :open)
     :data-tooltip (actions/get-display :open)}
    [:span.icon.material-icons "folder_open"]]
   [:button.button.is-primary.has-tooltip-left
    {:on-click #(execute-action! :export)
     :data-tooltip (actions/get-display :export)}
    [:span.icon.material-icons "image"]]
   [:button.button.is-info.has-tooltip-left
    {:on-click #(execute-action! :styles-panel)
     :class (when (= (viewport/panel) :styles) "active")
     :data-tooltip (actions/get-display :styles-panel)}
    [:span.icon.material-icons "palette"]]])
