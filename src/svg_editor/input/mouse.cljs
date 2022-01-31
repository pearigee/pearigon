(ns svg-editor.input.mouse
  (:require
   [clojure.string :as str]
   [svg-editor.tools.protocol :refer [OnMouseMove on-mouse-move
                                      OnClick on-click]]
   [svg-editor.state :as state]
   [svg-editor.selection :refer [select-from-mouse-event!]]))

(defn- eval-mouse-move [event]
  (let [t (state/get-tool)]
    (when (satisfies? OnMouseMove t)
      (on-mouse-move t event))))

(defn- eval-mouse-click [event]
  (js/console.log "click" event)
  (let [t (state/get-tool)]
    (if (satisfies? OnClick t)
      (on-click t event)
      ;; Other wise, default to selection action
      (select-from-mouse-event! event))))

(defn- event->mouse-state
  [event]
  (let [[vx vy] (state/get-view-pos-with-zoom)
        z (state/get-view-zoom-scale)]
    {:pos [(+ (/ (.-pageX event) z) vx)
           (+ (/ (.-pageY event) z) vy)]
     :shift (.-shiftKey event)
     :target-id (-> event .-target .-id)}))

(defn- bind-mouse
  "Bind mouse movement and clicks."
  []
  (let [body (.-body js/document)]
    (.addEventListener
     body
     "mousemove"
     (fn [event]
       (let [mouse-state (event->mouse-state event)]
         (state/set-mouse-state! mouse-state)
         (eval-mouse-move mouse-state))))
    (.addEventListener
     js/document
     "mousedown"
     (fn [event]
       (let [mouse-state (event->mouse-state event)]
         (eval-mouse-click mouse-state))))))

(defn- bind-scroll
  "Set up scrolling and zooming behavior, specifically multitouch."
  []
  (let [svg (js/document.getElementById "svg-root")]
    ;; TODO: Scale scrolling with zoom for even sensitivity.
    (.addEventListener
     svg
     "mousewheel"
     (fn [event]
       (.preventDefault event)
       (let [x (.-deltaX event)
             y (.-deltaY event)
             ctrl (.-ctrlKey event)]
         (if ctrl
           ;; This is a pinch to zoom. Delta is in `y`.
           (state/view-zoom! y)
           ;; This is a normal scroll
           (state/move-view-pos! [x y])))))))

(defn init
  "Bind mouse events to input handlers."
  []
  (bind-mouse)
  (bind-scroll))
