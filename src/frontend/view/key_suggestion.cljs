(ns frontend.view.key-suggestion
  (:require [frontend.state.core :as state]
            [frontend.actions.core :as actions]
            [frontend.state.input :as input]))

(defn key-suggestion []
  (let [tool (state/get-tool)
        key-set-name (cond
                       (input/just-ctrl?) "Ctrl-"
                       (input/just-alt?) "Alt-"
                       tool (:display tool)
                       :else nil)
        suggestions (actions/get-hotkey-suggestions)]
    [:div.suggestions
     (if key-set-name
       [:div.tag.is-primary key-set-name]
       [:div.tag.is-primary.is-light "No Tool"])
     (for [s suggestions]
       ^{:key s} [:div [:span.tag.key-suggestion (:key-display s)]
                  [:span.is-size-7 (:display s)]])]))
