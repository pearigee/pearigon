(ns frontend.export.svg
  (:require
   [frontend.shapes.protocol :refer [render-svg]]
   [frontend.state.core :as state]
   [frontend.state.viewport :as viewport]
   [reagent.dom.server :refer [render-to-string]]))

(defn- svg-hiccup []
  (let [shapes (state/get-shapes-with-override)
        [zvx zvy] (viewport/pos-with-zoom)
        [zdx zdy] (viewport/dim-with-zoom)]
    [:svg {:xmlns "http://www.w3.org/2000/svg"
           :view-box (str zvx " " zvy " " zdx " " zdy)}
     (for [{:keys [id] :as shape} shapes]
       ^{:key id} [render-svg shape])]))

(defn ->svg-string []
  (render-to-string [svg-hiccup]))
