(ns frontend.view.sidebar
  (:require
   ["@tabler/icons" :rename {IconX icon-minimize}]
   [reagent.core :as r]
   [frontend.state.core :as state]
   [frontend.view.material :refer [material-editor]]))

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
  (r/create-class
   {:component-did-update
    (fn [_ _]
       ;; Update view dimensions so zoom can be recomputed
       ;; at the correct aspect ratio.
      (state/update-view-size!))

    :reagent-render
    (fn []
      (let [panel (state/get-panel)]
        (case panel
          :material [sidebar-panel
                     "Material Editor"
                     [material-editor]]
          nil)))}))
