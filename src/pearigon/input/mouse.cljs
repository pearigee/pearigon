(ns pearigon.input.mouse
  (:require
   [pearigon.input.keyboard :as keyboard]
   [pearigon.state.core :as state]
   [pearigon.state.mouse :as mouse]
   [pearigon.state.tools :as tools]
   [pearigon.state.viewport :as viewport]
   [pearigon.tools.protocol :refer [on-click on-mouse-move OnClick OnMouseMove]]))

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
  (let [[vx vy] (viewport/pos-with-zoom)
        z (viewport/zoom-scale)]
    {:pos [(+ (/ (.-pageX event) z) vx)
           (+ (/ (.-pageY event) z) vy)]
     :shift (.-shiftKey event)
     :target-id (-> event .-target .-id)
     :button (-> event .-button)}))

(defn- bind-mouse
  "Bind mouse movement and clicks."
  []
  (let [body (.-body js/document)]
    (.addEventListener
     body
     "mousemove"
     (fn [event]
       (let [mouse-state (event->mouse-state event)
             dx (- (.-movementX event))
             dy (- (.-movementY event))]
         (mouse/pos! (:pos mouse-state))
         ;; Support drag panning with the middle mouse button.
         (if (mouse/button-down? 1)
           (viewport/move-pos! [dx dy])
           (eval-mouse-move mouse-state)))))
    (.addEventListener
     js/document
     "mousedown"
     (fn [event]
       (let [mouse-state (event->mouse-state event)]
         (mouse/merge-buttons-down! {(:button mouse-state) true})
         (eval-mouse-click mouse-state))))
    (.addEventListener
     js/document
     "mouseup"
     (fn [event]
       (let [mouse-state (event->mouse-state event)]
         (mouse/merge-buttons-down! {(:button mouse-state) false}))))))

(defn- bind-scroll
  "Set up scrolling and zooming behavior, specifically multitouch."
  []
  (let [svg (js/document.getElementById "svg-root")]
    ;; TODO: Scale scrolling with zoom for even sensitivity.
    (.addEventListener
     svg
     "wheel"
     (fn [event]
       (.preventDefault event)
       (let [dx (.-deltaX event)
             dy (.-deltaY event)
             ctrl (.-ctrlKey event)]
         (if ctrl
           ;; This is a pinch to zoom. Delta is in `y`.
           (viewport/zoom! dy)
           ;; This is a normal scroll
           (viewport/move-pos! [dx dy])))))))

(defn init
  "Bind mouse events to input handlers."
  []
  (bind-mouse)
  (bind-scroll))
