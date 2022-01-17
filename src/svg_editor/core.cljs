(ns svg-editor.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [svg-editor.input.keyboard :as keyboard]
   [svg-editor.input.mouse :as mouse]
   [svg-editor.input.resize :as resize]
   [svg-editor.state :as state]
   [svg-editor.view.app :refer [app]]))

(def s (r/atom (state/initial-state)))

(defn mount-root
  []
  (d/render [app s] (.getElementById js/document "app")))

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
