(ns svg-editor.input.keyboard
  (:require
   [clojure.string :as str]
   [svg-editor.actions :as actions]
   [svg-editor.state :as state]
   [svg-editor.tools.protocol :refer [OnKeypress on-keypress]]
   [svg-editor.tools.add :refer [add]]
   [svg-editor.tools.grab :refer [grab]]
   [svg-editor.tools.material :refer [material]]
   [svg-editor.tools.scale :refer [scale]]
   [svg-editor.tools.path-tool :refer [path-tool]]))

(defn- eval-hotkey [key]
  (let [tool (state/get-tool)]
    (if (satisfies? OnKeypress tool)
      (do
        (js/console.log "Calling tool callback: " key)
        (on-keypress tool key))
      (do (js/console.log "Getting tool for hotkey: " key)
          (condp = key
            (actions/get-key :add) (add)
            (actions/get-key :scale) (scale)
            (actions/get-key :grab) (grab)
            (actions/get-key :material) (material)
            (actions/get-key :path-tool) (path-tool)
            nil)))))

(defn- keyboard-event->key
  [event]
  (let [key-raw (str/lower-case (.-key event))
        key (if (= key-raw " ") "space" key-raw)
        ctrl (.-ctrlKey event)]
    (keyword (str (if ctrl "ctrl-" "") key))))

(defn- bind-keys []
  (js/document.addEventListener
   "keydown"
   (fn [event]
      ;; Don't capture input events from text inputs.
     (when (not= (.-tagName js/document.activeElement) "INPUT")
        ;; Prevent Tab from leaving the page.
       (.preventDefault event)
       (let [key (keyboard-event->key event)]
         (js/console.log "Keypress: " key)
         (eval-hotkey key))))))

(defn init
  "Bind keyboard input handlers."
  []
  (bind-keys))
