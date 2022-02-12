(ns frontend.view.key-suggestion
  (:require [frontend.state.core :as state]
            [frontend.actions.core :as actions]))

(defn key-suggestion []
  (let [tool-name (:display (state/get-tool))
        suggestions (actions/get-hotkey-suggestions)]
    [:div.suggestions
     (if tool-name
       [:div.tag.is-primary tool-name]
       [:div.tag.is-primary.is-light "No Tool"])
     (for [s suggestions]
       ^{:key s} [:div [:span.tag.key-suggestion (:key-display s)]
                  [:span.is-size-7 (:display s)]])]))
