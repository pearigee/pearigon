(ns frontend.view.app
  (:require [frontend.state :as state]
            [frontend.view.sidebar :refer [sidebar]]
            [frontend.shapes.protocol :refer [render-svg]]
            [frontend.tools.protocol :refer [ToolRenderSVG tool-render-svg]]
            [frontend.view.key-suggestion :refer [key-suggestion]]))

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