(ns frontend.input.keyboard
  (:require
   [clojure.string :as str]
   [frontend.actions :as actions]
   [frontend.state :as state]
   [frontend.tools.protocol :refer [OnKeypress on-keypress]]
   [frontend.tools.add :refer [add]]
   [frontend.tools.grab :refer [grab]]
   [frontend.tools.material :refer [material]]
   [frontend.tools.scale :refer [scale]]
   [frontend.tools.path-tool :refer [path-tool]]))

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
