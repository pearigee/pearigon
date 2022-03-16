(ns frontend.view.app
  (:require
   [frontend.shapes.protocol :refer [render-svg]]
   [frontend.state.core :as state]
   [frontend.state.tools :as tools]
   [frontend.state.viewport :as viewport]
   [frontend.tools.protocol :refer [tool-render-svg ToolRenderSVG]]
   [frontend.view.code-editor :refer [code-editor]]
   [frontend.view.key-suggestion :refer [key-suggestion]]
   [frontend.view.search :refer [search-overlay]]
   [frontend.view.sidebar :refer [sidebar]]
   [frontend.view.toolbar :refer [toolbar]]))

(defn app []
  (let [shapes (state/get-shapes-with-override)
        tools (tools/get-tool-stack)
        [zvx zvy] (viewport/pos-with-zoom)
        [zdx zdy] (viewport/dim-with-zoom)]
    [:div.app
     [:div.viewport
      [:svg {:id "svg-root"
             :view-box (str zvx " " zvy " " zdx " " zdy)}
       (for [{:keys [id] :as shape} shapes]
         ^{:key id} [render-svg shape])

       (for [t tools]
         (when (satisfies? ToolRenderSVG t)
           ^{:key (:action t)} [tool-render-svg t]))]]
     [:div.overlay
      [:div.overlay-center-pane
       [key-suggestion]
       (when (viewport/code-showing?)
         [code-editor])]
      [sidebar]
      [toolbar]]
     (when (viewport/search-showing?)
       [search-overlay])]))
