(ns svg-editor.view.app
  (:require [svg-editor.state :as state]
            [svg-editor.view.sidebar :refer [sidebar]]
            [svg-editor.shapes.protocol :refer [render-svg]]
            [svg-editor.tools.protocol :refer [ToolRenderSVG tool-render-svg]]
            [svg-editor.view.key-suggestion :refer [key-suggestion]]))

(defn app []
  (let [shapes (state/get-shapes-with-override)
        tools (state/get-tool-stack)
        [zvx zvy] (state/get-view-pos-with-zoom)
        [zdx zdy] (state/get-view-dim-with-zoom)]
    [:div.app
     [:div.viewport
      [:svg {:id "svg-root"
             :view-box (str zvx " " zvy " " zdx " " zdy)}
       (for [{:keys [id] :as shape} shapes]
         ^{:key id} [render-svg shape])

       (for [t tools]
         (when (satisfies? ToolRenderSVG t)
           ^{:key (:action t)} [tool-render-svg t]))]]
     [key-suggestion]
     [sidebar]]))
