(ns frontend.view.key-suggestion
  (:require [frontend.state :as state]))

(defn key-suggestion []
  (let [suggestions (state/get-suggestions)
        [width] (state/get-view-dimensions)
        tool-name (:tool suggestions)
        keys (:keys suggestions)]
    [:div.suggestions {:style {:width width}}
     (if tool-name
       [:div.tag.is-primary tool-name]
       [:div.tag.is-primary.is-light "No Tool"])
     (for [k keys]
       ^{:key k} [:div [:span.tag.key-suggestion (:key-display k)]
                  [:span.is-size-7 (:display k)]])]))
