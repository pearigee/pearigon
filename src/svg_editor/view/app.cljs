(ns svg-editor.view.app
  (:require [svg-editor.state :as state]
            [svg-editor.view.sidebar :refer [sidebar]]
            [svg-editor.render.svg :refer [shape->svg]]
            [svg-editor.view.key-suggestion :refer [key-suggestion]]))

(defn app
  [s]
  (let [materials (state/get-materials s)
        [zvx zvy] (state/get-view-pos-with-zoom s)
        [zdx zdy] (state/get-view-dim-with-zoom s)]
    [:div.app
     [:div.viewport
      [:svg {:id "svg-root"
             :view-box (str zvx " " zvy " " zdx " " zdy)}
       (for [shape (:shapes @s)]
         ^{:key shape} [shape->svg shape materials])]
      [key-suggestion (:suggestions @s)]]
     [sidebar s]]))
