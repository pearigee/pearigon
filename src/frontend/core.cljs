(ns frontend.core
  (:require
   [frontend.input.keyboard :as keyboard]
   [frontend.input.mouse :as mouse]
   [frontend.input.resize :as resize]
   [frontend.state.init :refer [init-state!]]
   [frontend.state.viewport :as viewport]
   [frontend.view.app :refer [app]]
   [reagent.dom :as d]))

(defn mount-root
  []
  (d/render [app] (.getElementById js/document "app")))

(defn init
  []
  (mount-root)
  (init-state!)
  (keyboard/init)
  (mouse/init)
  (resize/init)
  (viewport/on-resize!))

(defn ^:export init!
  "This is called by index.html to initialize the app."
  []
  (init))
