(ns svg-editor.view.sidebar
  (:require
    ["@tabler/icons" :rename {IconX icon-minimize}]
    [reagent.core :as r]
    [svg-editor.state :as state]
    [svg-editor.view.material :refer [material-editor]]))

(defn sidebar-panel
  [state title content]
  [:div.sidebar.notification
   [:div.sidebar-header
    [:strong title]
    [:button.button.is-small
     {:on-click #(state/set-panel! state nil)}
     [:span.icon.is-small [:> icon-minimize]]]]
   content])

(defn sidebar
  [state]
  (r/create-class
    {:component-did-update
     (fn [_ _]
       ;; Update view dimensions so zoom can be recomputed
       ;; at the correct aspect ratio.
       (state/update-view-size! state))

     :reagent-render
     (fn [state]
       (let [panel (state/get-panel state)]
         (case panel
           :material [sidebar-panel
                      state
                      "Material Editor"
                      [material-editor state]]
           nil)))}))
