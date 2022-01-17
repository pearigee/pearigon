(ns svg-editor.input.keyboard
  (:require
   [clojure.string :as str]
   [svg-editor.actions :as actions]
   [svg-editor.state :as state]
   [svg-editor.tools.protocol :refer [OnKeypress on-keypress]]
   [svg-editor.tools.add :refer [add]]
   [svg-editor.tools.grab :refer [grab]]
   [svg-editor.tools.material :refer [material]]
   [svg-editor.tools.scale :refer [scale]]))

(defn- eval-hotkey
  [s key]
  (let [tool (state/get-tool s)]
    (if (satisfies? OnKeypress tool)
      (do
        (js/console.log "Calling tool callback: " key)
        (on-keypress tool s key))
      (do (js/console.log "Getting tool for hotkey: " key)
          (condp = key
            (actions/get-key :add) (add s)
            (actions/get-key :scale) (scale s)
            (actions/get-key :grab) (grab s)
            (actions/get-key :material) (material s)
            nil)))))

(defn- keyboard-event->key
  [event]
  (let [key-raw (str/lower-case (.-key event))
        key (if (= key-raw " ") "space" key-raw)
        ctrl (.-ctrlKey event)]
    (keyword (str (if ctrl "ctrl-" "") key))))

(defn- bind-keys
  [s]
  (js/document.addEventListener
   "keydown"
   (fn [event]
      ;; Don't capture input events from text inputs.
     (when (not= (.-tagName js/document.activeElement) "INPUT")
        ;; Prevent Tab from leaving the page.
       (.preventDefault event)
       (let [key (keyboard-event->key event)]
         (js/console.log "Keypress: " key)
         (eval-hotkey s key))))))

(defn init
  "Bind keyboard input handlers."
  [s]
  (bind-keys s))
