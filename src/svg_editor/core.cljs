(ns svg-editor.core
  (:require
    [clojure.string :as str]
    [reagent.core :as r]
    [reagent.dom :as d]
    [svg-editor.keymap :as keys]
    [svg-editor.state :as state]
    [svg-editor.svg :as svg]
    [svg-editor.tools.add :refer [add]]
    [svg-editor.tools.grab :refer [grab]]
    [svg-editor.tools.material :refer [material]]
    [svg-editor.tools.scale :refer [scale]]
    [svg-editor.view.sidebar :refer [sidebar]]))

(def s (r/atom (state/initial-state)))

(defn suggestion-display
  [suggestions]
  (let [tool-name (:tool suggestions)
        keys (:keys suggestions)]
    [:div.suggestions
     (if tool-name
       [:div.tag.is-primary tool-name]
       [:div.tag.is-primary.is-light "No Tool"])
     (for [k keys]
       ^{:key k} [:div [:span.tag.key-suggestion (:key-display k)]
                  [:span.is-size-7 (:display k)]])]))

(defn editor
  []
  (let [materials (state/get-materials s)
        [zvx zvy] (state/get-view-pos-with-zoom s)
        [zdx zdy] (state/get-view-dim-with-zoom s)]
    [:div.app
     [:div.viewport
      [:svg {:id "svg-root"
             :view-box (str zvx " " zvy " " zdx " " zdy)}
       (for [shape (:shapes @s)]
         ^{:key shape} [svg/shape->svg shape materials])]
      [suggestion-display (:suggestions @s)]]
     [sidebar s]]))

(defn eval-hotkey
  [key]
  (let [tool-key-callback (:on-keypress (state/get-tool s))]
    (if tool-key-callback
      (do
        (js/console.log "Calling tool callback: " key)
        (tool-key-callback s key))
      (let [mouse (:mouse @s)]
        (js/console.log "Getting tool for hotkey: " key)
        (condp = key
          (keys/get-key :add) (add s)
          (keys/get-key :scale) (scale s)
          (keys/get-key :grab) (grab s mouse)
          (keys/get-key :material) (material s)
          nil)))))

(defn eval-mouse-move
  [event]
  (let [tool (state/get-tool s)
        on-mousemove (:on-mousemove tool)]
    (when on-mousemove (on-mousemove s event))))

(defn eval-mouse-click
  [event]
  (js/console.log "click" event)
  (let [tool (state/get-tool s)
        on-click (:on-click tool)]
    (if on-click
      (on-click s event)
      ;; Other wise, default to selection action
      (let [target-id (:target-id event)]
        (if (str/starts-with? target-id "shape-")
          (do (when-not (:shift event) (state/deselect-all! s))
              (state/select-id! s target-id))
          (when (and (= (:target-id event) "svg-root")
                     (not (:shift event)))
            (state/deselect-all! s)))))))

(defn keyboard-event->key
  [event]
  (let [key (aget event "key")
        shift (aget event "shiftKey")
        ctrl (aget event "ctrlKey")]
    (keyword (str (if shift "shift-" "")
                  (if ctrl "ctrl-" "")
                  key))))

(defn bind-keys
  []
  (js/document.addEventListener
    "keypress"
    (fn [event]
      ;; Don't capture input events from text inputs
      (when (not= (.-tagName js/document.activeElement) "INPUT")
        (let [key (keyboard-event->key event)]
          (js/console.log "Keypress: " key)
          (eval-hotkey key))))))

(defn bind-mouse
  []
  (let [body (aget js/document "body")]
    (.addEventListener
      body
      "mousemove"
      (fn [event]
        (let [[vx vy] (state/get-view-pos-with-zoom s)
              z (state/get-view-zoom-scale s)
              mouse-state {:pos [(+ (/ (aget event "pageX") z) vx)
                                 (+ (/ (aget event "pageY") z) vy)]}]
          (state/set-mouse-state! s mouse-state)
          (eval-mouse-move mouse-state))))
    (.addEventListener
      js/document
      "mousedown"
      (fn [event]
        (let [mouse-state {:pos [(aget event "pageX")
                                 (aget event "pageY")]
                           :shift (aget event "shiftKey")
                           :target-id (aget (aget event "target") "id")}]
          (eval-mouse-click mouse-state))))))

(defn bind-scroll
  []
  (let [svg (js/document.getElementById "svg-root")]
    ;; TODO: Scale scrolling with zoom for even sensitivity.
    (.addEventListener svg
                       "mousewheel"
                       (fn [event]
                         (.preventDefault event)
                         (let [x (aget event "deltaX")
                               y (aget event "deltaY")
                               ctrl (aget event "ctrlKey")]
                           (if ctrl
                             ;; This is a pinch to zoom. Delta is in `y`.
                             (state/view-zoom! s y)
                             ;; This is a normal scroll
                             (state/move-view-pos! s [x y])))))))

(defn bind-resize
  []
  (.addEventListener js/window "resize"
                     #(state/update-view-size! s)))

(defn mount-root
  []
  (d/render [editor] (.getElementById js/document "app")))

(defn init
  []
  (mount-root)
  (bind-keys)
  (bind-mouse)
  (bind-scroll)
  (bind-resize)
  (state/update-view-size! s))

(defn ^:export init!
  []
  (init))
