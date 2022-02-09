(ns frontend.input.keyboard
  (:require
   [clojure.string :as str]
   [frontend.actions :as actions]
   [frontend.state.core :as state]
   [frontend.tools.protocol :refer [OnKeypress on-keypress]]
   [frontend.tools.add :refer [add]]
   [frontend.tools.grab :refer [grab]]
   [frontend.tools.rotate :refer [rotate]]
   [frontend.tools.material :refer [material]]
   [frontend.tools.scale :refer [scale]]
   [frontend.tools.delete :refer [delete]]
   [frontend.tools.path-tool :refer [path-tool]]))

(defn- eval-hotkey [k]
  (let [tool (state/get-tool)]
    (if tool
      (when (satisfies? OnKeypress tool) (on-keypress tool k))
      (cond
        (actions/active? :add k) (add)
        (actions/active? :scale k) (scale)
        (actions/active? :grab k) (grab)
        (actions/active? :rotate k) (rotate)
        (actions/active? :material k) (material)
        (actions/active? :path-tool k) (path-tool)
        (actions/active? :delete k) (delete)))))

(defn- keyboard-event->key
  [event]
  (let [key-raw (str/lower-case (.-key event))
        key (if (= key-raw " ") "space" key-raw)
        ctrl (.-ctrlKey event)]
    (keyword (str (if ctrl "ctrl-" "") key))))

(defn record-suggestions!
  "Record any actions that are polled for to populate suggestions."
  []
  (actions/clear-suggestions!)
  (eval-hotkey :record-suggestions))

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
         (eval-hotkey key)
         (record-suggestions!))))))

(defn init
  "Bind keyboard input handlers."
  []
  (bind-keys)
  (record-suggestions!))
