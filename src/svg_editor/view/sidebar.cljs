(ns svg-editor.view.sidebar
  (:require [svg-editor.state :as state]
            [svg-editor.view.material :refer [material-editor]]
            ["@tabler/icons" :rename {IconX icon-minimize}]))

(defn sidebar-panel [state title content]
  [:div.sidebar.notification
   [:div.sidebar-header
    [:strong title] 
    [:button.button.is-small 
     {:on-click #(state/set-panel! state nil)} 
     [:span.icon.is-small [:> icon-minimize]]]]
   content])

(defn sidebar [state]
  (let [panel (state/get-panel state)]
    (case panel
      :material [sidebar-panel
                 state
                 "Material Editor"
                 [material-editor state]]
      nil)))