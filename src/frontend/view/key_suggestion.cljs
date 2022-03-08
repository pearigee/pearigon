(ns frontend.view.key-suggestion
  (:require
   [frontend.state.actions :as actions]
   [frontend.state.keyboard :as keyboard]
   [frontend.state.tools :as tools]
   [frontend.input.keyboard :as hotkeys]))

(defn key-suggestion []
  (let [tool (tools/get-tool)
        key-set-name (cond
                       (keyboard/just-ctrl?) "Ctrl-"
                       (keyboard/just-alt?) "Alt-"
                       tool (:display tool)
                       :else nil)
        suggestions (actions/get-hotkey-suggestions)]
    [:div.suggestions
     (if key-set-name
       [:div.tool.tag.is-primary key-set-name]
       [:div.tool.tag.is-primary.is-light "No Tool"])
     (for [s suggestions]
       ^{:key s} [:div.action
                  {:on-click
                   ;; Execute the action if a handler exists, otherwise
                   ;; emulate the keypress (for tools with their own hotkeys).
                   #(do (or (actions/execute! (:id s))
                            (hotkeys/eval-hotkey! (:key s)))
                        (hotkeys/record-suggestions!))}
                  [:span.tag.key (:key-display s)]
                  [:span.is-size-7 (:display s)]])]))
