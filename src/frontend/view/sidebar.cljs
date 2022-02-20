(ns frontend.view.sidebar
  (:require
   ["@tabler/icons" :rename {IconX icon-minimize}]
   [frontend.state.viewport :as viewport]
   [frontend.view.panels.styles-panel :refer [styles-panel]]))

(defn sidebar-panel
  [title content]
  [:div.sidebar.notification
   [:div.sidebar-header
    [:strong title]
    [:button.button.is-small
     {:on-click #(viewport/panel! nil)}
     [:span.icon.is-small [:> icon-minimize]]]]
   content])

(defn sidebar []
  (let [panel (viewport/panel)]
    (case panel
      :styles [sidebar-panel
               "Styles"
               [styles-panel]]
      nil)))
