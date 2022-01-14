(ns svg-editor.view.material
  (:require
    ["@tabler/icons" :rename {IconPlus add-icon
                              IconPaint apply-icon}]
    ["react-colorful" :rename {HexColorPicker hex-color-picker}]
    [reagent.core :as r]
    [svg-editor.state :as state]))

(defn material-editor
  [state]
  (let [m-state (r/atom {:selected :default})
        get-selected (fn [] (state/get-material state (:selected @m-state)))]
    (fn []
      [:div.sidebar-content
       [:label "Active Material"]
       [:div.select {:style {:width "100%"}}
        [:select {:style {:width "100%"}
                  :value (:selected @m-state)
                  :on-change
                  (fn [event]
                    (js/console.log event)
                    (swap! m-state assoc
                           :selected
                           (keyword (-> event .-target .-value)))
                    (state/set-active-material! state (:selected @m-state))
                    (js/console.log @m-state))}
         (for [[k v] (into [] (state/get-materials state))]
           ^{:key k} [:option {:value k} (:display v)])]]
       [:button.button.is-small.is-success
        {:on-click (fn []
                     (let [id (keyword (str (random-uuid)))]
                       (state/add-material! state id {:display "New Material"
                                                      :color "#000"})
                       (state/set-active-material! state id)
                       (swap! m-state assoc :selected id)))}
        [:span.icon.is-small
         [:> add-icon]]
        [:span "New Material"]]
       [:button.button.is-small.is-success
        {:on-click (fn []
                     (state/map-selected-shapes!
                      state
                      #(assoc % :mat-id (:selected @m-state))))}
        [:span.icon.is-small
         [:> apply-icon]]
        [:span "Apply to Selected"]]
       [:label "Material Name"]
       [:input.input {:type "text"
                      :value (:display (get-selected))
                      :on-change (fn [event]
                                   (js/console.log event)
                                   (state/set-material!
                                    state
                                    (:selected @m-state)
                                    (merge (get-selected)
                                           {:display (-> event .-target .-value)})))}]
       [:label "Material Type"]
       [:div.select {:style {:width "100%"
                             :margin-bottom "10px"}}
        [:select {:style {:width "100%"}}
         [:option "Color"]
         [:option "Gradient"]
         [:option "Pattern"]]]
       [:> hex-color-picker
        {:color (:color (get-selected))
         :on-change (fn [color]
                      (state/set-material!
                       state
                       (:selected @m-state)
                       (merge (get-selected)
                              {:color color})))}]
       [:hr]])))
