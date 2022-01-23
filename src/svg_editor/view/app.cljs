(ns svg-editor.view.app
  (:require [svg-editor.state :as state]
            [svg-editor.view.sidebar :refer [sidebar]]
            [svg-editor.shapes.protocol :refer [render-svg]]
            [svg-editor.view.key-suggestion :refer [key-suggestion]]))

(defn app
  [s]
  (let [shapes (state/get-shapes-with-override s)
        [zvx zvy] (state/get-view-pos-with-zoom s)
        [zdx zdy] (state/get-view-dim-with-zoom s)]
    [:div.app
     [:div.viewport
      [:svg {:id "svg-root"
             :view-box (str zvx " " zvy " " zdx " " zdy)}
       (for [{:keys [id] :as shape} shapes]
         ^{:key id} [render-svg shape s])]
      [key-suggestion (:suggestions @s)]]
     [sidebar s]]))
