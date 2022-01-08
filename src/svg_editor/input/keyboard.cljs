(ns svg-editor.input.keyboard
  (:require
    [svg-editor.keymap :as keys]
    [svg-editor.state :as state]
    [svg-editor.tools.add :refer [add]]
    [svg-editor.tools.grab :refer [grab]]
    [svg-editor.tools.material :refer [material]]
    [svg-editor.tools.scale :refer [scale]]))

(defn- eval-hotkey
  [s key]
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

(defn- keyboard-event->key
  [event]
  (let [key (.-key event)
        ctrl (.-ctrlKey event)]
    (keyword (str (if ctrl "ctrl-" "") key))))

(defn- bind-keys
  [s]
  (js/document.addEventListener
    "keypress"
    (fn [event]
      ;; Don't capture input events from text inputs
      (when (not= (.-tagName js/document.activeElement) "INPUT")
        (let [key (keyboard-event->key event)]
          (js/console.log "Keypress: " key)
          (eval-hotkey s key))))))

(defn init
  "Bind keyboard input handlers."
  [s]
  (bind-keys s))
