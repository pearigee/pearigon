(ns svg-editor.core
  (:require
    [reagent.core :as r]
    [reagent.dom :as d]
    [svg-editor.input.keyboard :as keyboard]
    [svg-editor.input.mouse :as mouse]
    [svg-editor.input.resize :as resize]
    [svg-editor.state :as state]
    [svg-editor.render.svg :as svg]
    [svg-editor.view.sidebar :refer [sidebar]]))

(def s (r/atom (state/initial-state)))

(defn suggestion-display
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

(defn editor
  []
  (let [materials (state/get-materials s)
        [zvx zvy] (state/get-view-pos-with-zoom s)
        [zdx zdy] (state/get-view-dim-with-zoom s)]
    [:div.app
     [:div.viewport
      [:svg {:id "svg-root"
             :view-box (str zvx " " zvy " " zdx " " zdy)}
       (for [shape (:shapes @s)]
         ^{:key shape} [svg/shape->svg shape materials])]
      [suggestion-display (:suggestions @s)]]
     [sidebar s]]))

(defn mount-root
  []
  (d/render [editor] (.getElementById js/document "app")))

(defn init
  []
  (mount-root)
  (keyboard/init s)
  (mouse/init s)
  (resize/init s)
  (state/update-view-size! s))

(defn ^:export init!
  []
  (init))
