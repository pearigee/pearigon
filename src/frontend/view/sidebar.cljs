(ns frontend.view.sidebar
  (:require
   ["@tabler/icons" :rename {IconX icon-minimize}]
   [frontend.state.core :as state]
   [frontend.view.panels.styles-panel :refer [styles-panel]]))

(defn sidebar-panel
  [title content]
  [:div.sidebar.notification
   [:div.sidebar-header
    [:strong title]
    [:button.button.is-small
     {:on-click #(state/set-panel! nil)}
     [:span.icon.is-small [:> icon-minimize]]]]
   content])

(defn sidebar []
  (let [panel (state/get-panel)]
    (case panel
      :styles [sidebar-panel
               "Styles"
               [styles-panel]]
      nil)))
