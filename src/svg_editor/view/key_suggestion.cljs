(ns svg-editor.view.key-suggestion)

(defn key-suggestion
  [suggestions]
  (let [tool-name (:tool suggestions)
        keys (:keys suggestions)]
    [:div.suggestions
     (if tool-name
       [:div.tag.is-primary tool-name]
       [:div.tag.is-primary.is-light "No Tool"])
     (for [k keys]
       ^{:key k} [:div [:span.tag.key-suggestion (:key-display k)]
                  [:span.is-size-7 (:display k)]])]))
