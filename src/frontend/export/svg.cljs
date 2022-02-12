(ns frontend.export.svg
  (:require [reagent.dom.server :refer [render-to-string]]
            [frontend.state.core :as state]
            [frontend.shapes.protocol :refer [render-svg]]))

(defn- svg-hiccup []
  (let [shapes (state/get-shapes-with-override)
        [zvx zvy] (state/get-view-pos-with-zoom)
        [zdx zdy] (state/get-view-dim-with-zoom)]
    [:svg {:xmlns "http://www.w3.org/2000/svg"
           :view-box (str zvx " " zvy " " zdx " " zdy)}
     (for [{:keys [id] :as shape} shapes]
       ^{:key id} [render-svg shape])]))

(defn ->svg-string []
  (render-to-string [svg-hiccup]))
