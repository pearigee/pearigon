(ns frontend.view.panels.styles-panel
  (:require [frontend.state.core :as state]
            [frontend.view.components.input :refer [input]]
            [frontend.utils.styles :refer [apply-styles]]
            [reagent.core :as r]))

(defn update-styles [styles]
  (if-not (zero? (count (state/selected-paths)))
    (state/map-selected-shapes! #(apply-styles % styles))
    (state/default-styles! styles)))

(defn current-styles []
  (let [selected (state/selected-paths)]
    (if-not (zero? (count selected))
      (:styles (first selected))
      (state/default-styles))))

(defn styles-panel []
  (r/with-let [model (r/atom (current-styles))
               merge-model! (fn [styles]
                              (swap! model merge styles)
                              (update-styles @model))]
    (let [num-selected (count (state/selected-paths))
          current-styles (current-styles)]
      (when (<= num-selected 1)
        (merge-model! current-styles))
      [:div
       [:div
        [:label.tag.is-info
         (if (zero? num-selected)
           "Editing default styles"
           (str "Editing styles for " num-selected " shapes"))]
        [:div.field
         [:input.switch.is-rounded
          {:id "fill-checkbox"
           :type "checkbox"
           :checked (:fill? @model)
           :on-change #(merge-model! {:fill? (-> % .-target .-checked)})}]
         [:label.switch-header {:for "fill-checkbox"} "Fill"]]
        (when (:fill? @model)
          [:div
           [input {:type "color"
                   :value (:fill @model)
                   :on-change
                   #(merge-model! {:fill %})}
            "Color"]
           [input {:type "number"
                   :value (:fill-opacity @model)
                   :min 0
                   :max 1
                   :step 0.1
                   :on-change #(merge-model! {:fill-opacity %})}
            "Opacity"]])
        [:div.field
         [:input.switch.is-rounded
          {:id "stroke-checkbox"
           :type "checkbox"
           :checked (:stroke? @model)
           :on-change #(merge-model! {:stroke? (-> % .-target .-checked)})}]
         [:label.switch-header {:for "stroke-checkbox"} "Stroke"]]]
       (when (:stroke? @model)
         [:div
          [input {:type "color"
                  :value (:stroke @model)
                  :on-change #(merge-model! {:stroke %})}
           "Color"]
          [input {:type "number"
                  :value (:stroke-opacity @model)
                  :min 0
                  :max 1
                  :step 0.1
                  :on-change #(merge-model! {:stroke-opacity %})}
           "Opacity"]
          [input {:type "number"
                  :value (:stroke-width @model)
                  :min 0
                  :on-change #(merge-model! {:stroke-width %})}
           "Width"]])])))
