(ns svg-editor.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [svg-editor.input.keyboard :as keyboard]
   [svg-editor.input.mouse :as mouse]
   [svg-editor.input.resize :as resize]
   [svg-editor.state :as state]
   [svg-editor.view.app :refer [app]]))

(defn mount-root
  []
  (d/render [app] (.getElementById js/document "app")))

(defn init
  []
  (mount-root)
  (keyboard/init)
  (mouse/init)
  (resize/init)
  (state/update-view-size!))

(defn ^:export init!
  []
  (init))
