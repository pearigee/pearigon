(ns frontend.view.search
  (:require
   [reagent.core :as r]
   [frontend.state.viewport :as viewport]
   [frontend.input.keyboard :as keyboard]
   [frontend.state.actions :as actions]))

(def max-result-size 15)

(defn search-overlay []
  (r/with-let
    [*results (r/atom (take max-result-size
                            (actions/search-actions "")))
     *selected (r/atom 0)
     search (fn [query]
              (reset! *selected 0)
              (reset! *results
                      (take max-result-size
                            (actions/search-actions query))))
     on-keydown (fn [code]
                  (case code
                    "ArrowUp"
                    (reset! *selected
                            (max 0 (dec @*selected)))
                    "ArrowDown"
                    (reset! *selected
                            (min (max 0 (dec (count @*results)))
                                 (inc @*selected)))
                    "Enter"
                    (let [id (-> @*results
                                 (nth @*selected)
                                 :id)]
                      (actions/execute! id)
                      ;; Check for updated suggestions
                      (keyboard/record-suggestions!)
                      (viewport/toggle-search-showing!))
                    "Escape"
                    (viewport/toggle-search-showing!)
                    nil))]
    (let [selected @*selected
          results @*results]
      [:<>
       [:div.search-backdrop {:on-click #(viewport/toggle-search-showing!)}]
       [:div.search-overlay
        [:div.input-container
         [:input.input {:auto-focus true
                        :on-key-down #(on-keydown (.-code %))
                        :on-change #(search (-> % .-target .-value))}]]
        [:div.search-results
         (doall (map-indexed
                 (fn [i r]
                   ^{:key (:id r)}
                   [:div.search-result
                    {:class (when (= i selected) "selected")
                     :on-click (fn []
                                 (js/console.log "on click!")
                                 (reset! *selected i)
                                 (on-keydown "Enter"))}
                    (:display r)])
                 results))]]])))
