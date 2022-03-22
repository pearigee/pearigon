(ns pearigon.view.app
  (:require
   [pearigon.shapes.protocol :refer [render-svg]]
   [pearigon.state.core :as state]
   [pearigon.state.tools :as tools]
   [pearigon.state.viewport :as viewport]
   [pearigon.tools.protocol :refer [tool-render-svg ToolRenderSVG]]
   [pearigon.view.code-editor :refer [code-editor]]
   [pearigon.view.key-suggestion :refer [key-suggestion]]
   [pearigon.view.search :refer [search-overlay]]
   [pearigon.view.sidebar :refer [sidebar]]
   [pearigon.view.toolbar :refer [toolbar]]))

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
