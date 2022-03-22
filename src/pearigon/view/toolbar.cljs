(ns pearigon.view.toolbar
  (:require
   [pearigon.state.actions :as actions]
   [pearigon.state.viewport :as viewport]))

(defn toolbar []
  [:div.toolbar
   [:button.button.is-primary.has-tooltip-left
    {:on-click #(actions/execute! :new-project)
     :data-tooltip (actions/display :new-project)}
    [:span.icon.material-icons "note_add"]]
   [:button.button.is-primary.has-tooltip-left
    {:on-click #(actions/execute! :save)
     :data-tooltip (actions/display :save)}
    [:span.icon.material-icons "save"]]
   [:button.button.is-primary.has-tooltip-left
    {:on-click #(actions/execute! :open)
     :data-tooltip (actions/display :open)}
    [:span.icon.material-icons "folder_open"]]
   [:button.button.is-primary.has-tooltip-left
    {:on-click #(actions/execute! :export)
     :data-tooltip (actions/display :export)}
    [:span.icon.material-icons "image"]]
   [:button.button.is-info.has-tooltip-left
    {:on-click #(actions/execute! :styles-panel)
     :class (when (= (viewport/panel) :styles) "active")
     :data-tooltip (actions/display :styles-panel)}
    [:span.icon.material-icons "palette"]]
   [:div.seperator]
   [:button.button.is-info.has-tooltip-left
    {:on-click #(actions/execute! :code)
     :class (when (viewport/code-showing?) "active")
     :data-tooltip (actions/display :code)}
    [:span.icon.material-icons "code"]]])
