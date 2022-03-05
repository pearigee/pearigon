(ns frontend.view.toolbar
  (:require
   [frontend.state.actions :as actions]
   [frontend.input.keyboard :as keyboard]
   [frontend.state.viewport :as viewport]))

(defn execute! [action-id]
  (actions/execute! action-id)
  ;; Scan for new hotkey suggestions that may have been caused by the action.
  (keyboard/record-suggestions!))

(defn toolbar []
  [:div.toolbar
   [:button.button.is-primary.has-tooltip-left
    {:on-click #(execute! :new-project)
     :data-tooltip (actions/display :new-project)}
    [:span.icon.material-icons "note_add"]]
   [:button.button.is-primary.has-tooltip-left
    {:on-click #(execute! :save)
     :data-tooltip (actions/display :save)}
    [:span.icon.material-icons "save"]]
   [:button.button.is-primary.has-tooltip-left
    {:on-click #(execute! :open)
     :data-tooltip (actions/display :open)}
    [:span.icon.material-icons "folder_open"]]
   [:button.button.is-primary.has-tooltip-left
    {:on-click #(execute! :export)
     :data-tooltip (actions/display :export)}
    [:span.icon.material-icons "image"]]
   [:button.button.is-info.has-tooltip-left
    {:on-click #(execute! :styles-panel)
     :class (when (= (viewport/panel) :styles) "active")
     :data-tooltip (actions/display :styles-panel)}
    [:span.icon.material-icons "palette"]]])
