(ns frontend.view.panels.styles-panel
  (:require
   [frontend.state.core :as state]
   [frontend.state.tools :as tools]
   [frontend.utils.styles :refer [apply-styles]]
   [frontend.view.components.input :refer [input]]
   [reagent.core :as r]))

(defn get-selection
  "Get the shape(s) that should be styled.
  When using the path tool, default to the shape being edited."
  []
  (let [paths (state/selected-paths)
        path-tool-selection (:path-id (tools/get-tool))]
    (cond (> (count paths) 0) paths
          path-tool-selection [(state/get-shape path-tool-selection)]
          :else [])))

(defn update-styles [styles]
  (let [selection (get-selection)]
    (if-not (zero? (count selection))
      (state/map-shape-ids! (into #{} (map :id selection))
                            #(apply-styles % styles))
      (state/default-styles! styles))))

(defn current-styles []
  (let [selected (get-selection)]
    (if-not (zero? (count selected))
      (:styles (first selected))
      (state/default-styles))))

(defn styles-panel []
  (r/with-let [model (r/atom (current-styles))
               merge-model! (fn [styles & {:keys [update-styles?]
                                           :or {update-styles? true}}]
                              (swap! model merge styles)
                              (when update-styles? (update-styles @model)))]
    (let [num-selected (count (get-selection))
          current-styles (current-styles)]
      (when (and (<= num-selected 1)
                 (not= @model current-styles))
        ;; TODO: This seems like an anti-pattern. Improve this.
        ;; This is called on render, and should not update the styles
        ;; in shape.core.
        (merge-model! current-styles :update-styles? false))
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
