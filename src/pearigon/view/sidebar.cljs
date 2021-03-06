(ns pearigon.view.sidebar
  (:require
   [pearigon.state.viewport :as viewport]
   [pearigon.view.panels.styles-panel :refer [styles-panel]]))

(defn sidebar-panel
  [title content]
  [:div.sidebar
   [:div.sidebar-header
    [:strong title]
    [:button.button.is-small
     {:on-click #(viewport/panel! nil)}
     "Close"]]
   [:div.sidebar-content content]])

(defn sidebar []
  (let [panel (viewport/panel)]
    (case panel
      :styles [sidebar-panel
               "Styles"
               [styles-panel]]
      nil)))
