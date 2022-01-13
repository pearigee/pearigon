(ns svg-editor.input.mouse
  (:require
   [clojure.string :as str]
   [svg-editor.tools.protocol :refer [OnMouseMove on-mouse-move
                                      OnClick on-click]]
   [svg-editor.state :as state]))

(defn- eval-mouse-move
  [s event]
  (let [t (state/get-tool s)]
    (when (satisfies? OnMouseMove t)
      (on-mouse-move t s event))))

(defn- eval-mouse-click
  [s event]
  (js/console.log "click" event)
  (let [t (state/get-tool s)]
    (if (satisfies? OnClick t)
      (on-click t s event)
      ;; Other wise, default to selection action
      (let [target-id (:target-id event)]
        (if (str/starts-with? target-id "shape-")
          (do (when-not (:shift event) (state/deselect-all! s))
              (state/select-id! s target-id))
          (when (and (= (:target-id event) "svg-root")
                     (not (:shift event)))
            (state/deselect-all! s)))))))

(defn- event->mouse-state
  [s event]
  (let [[vx vy] (state/get-view-pos-with-zoom s)
        z (state/get-view-zoom-scale s)]
    {:pos [(+ (/ (.-pageX event) z) vx)
           (+ (/ (.-pageY event) z) vy)]
     :shift (.-shiftKey event)
     :target-id (-> event .-target .-id)}))

(defn- bind-mouse
  "Bind mouse movement and clicks."
  [s]
  (let [body (.-body js/document)]
    (.addEventListener
      body
      "mousemove"
      (fn [event]
        (let [mouse-state (event->mouse-state s event)]
          (state/set-mouse-state! s mouse-state)
          (eval-mouse-move s mouse-state))))
    (.addEventListener
      js/document
      "mousedown"
      (fn [event]
        (let [mouse-state (event->mouse-state s event)]
          (eval-mouse-click s mouse-state))))))

(defn- bind-scroll
  "Set up scrolling and zooming behavior, specifically multitouch."
  [s]
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
           (state/view-zoom! s y)
           ;; This is a normal scroll
           (state/move-view-pos! s [x y])))))))

(defn init
  "Bind mouse events to input handlers."
  [s]
  (bind-mouse s)
  (bind-scroll s))
