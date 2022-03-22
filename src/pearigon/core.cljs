(ns pearigon.core
  (:require
   [api.core] ;; Require to expose in dev tools.
   [pearigon.input.keyboard :as keyboard]
   [pearigon.input.mouse :as mouse]
   [pearigon.input.resize :as resize]
   [pearigon.state.init :refer [init-state!]]
   [pearigon.state.viewport :as viewport]
   [pearigon.view.app :refer [app]]
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
