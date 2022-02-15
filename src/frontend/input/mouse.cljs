(ns frontend.input.mouse
  (:require
   [frontend.tools.protocol :refer [OnMouseMove on-mouse-move
                                    OnClick on-click]]
   [frontend.state.core :as state]
   [frontend.state.tools :as tools]
   [frontend.state.mouse :as mouse]
   [frontend.input.keyboard :as keyboard]))

(defn- eval-mouse-move [event]
  (let [t (tools/get-tool)]
    (when (satisfies? OnMouseMove t)
      (on-mouse-move t event))))

(defn- eval-mouse-click [event]
  (js/console.log "click" event)
  (let [t (tools/get-tool)]
    (if (satisfies? OnClick t)
      (on-click t event)
      (state/select-from-mouse-event! event)))
  ;; This click event could change what hotkeys are possible.
  ;; Make sure the suggestions are up to date.
  (keyboard/record-suggestions!))

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
         (mouse/pos! (:pos mouse-state))
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
