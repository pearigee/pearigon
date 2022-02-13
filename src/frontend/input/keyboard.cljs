(ns frontend.input.keyboard
  (:require
   [frontend.actions.core :as actions]
   [frontend.state.core :as state]
   [frontend.state.keyboard :as keyboard]
   [frontend.tools.protocol :refer [OnKeypress on-keypress]]
   [frontend.tools.add :refer [add]]
   [frontend.tools.grab :refer [grab]]
   [frontend.tools.rotate :refer [rotate]]
   [frontend.tools.material :refer [material]]
   [frontend.tools.scale :refer [scale]]
   [frontend.tools.delete :refer [delete]]
   [frontend.tools.export :refer [export]]
   [frontend.tools.path-tool :refer [path-tool]]))

(def ctrl-codes #{"ControlLeft" "ControlRight"})
(def alt-codes #{"AltLeft" "AltRight"})

(def action-bindings
  {:add add
   :scale scale
   :grab grab
   :rotate rotate
   :path-tool path-tool
   :delete delete
   :export export
   :material material})

(defn- eval-keys-by [pred k]
  (let [actions
        (filter pred (keys action-bindings))
        active (first (filter #(actions/active? % k) actions))]
    (when active ((get action-bindings active)))))

(defn- eval-root-keys [k]
  (eval-keys-by actions/uses-no-modifiers? k))

(defn- eval-ctrl-keys [k]
  (eval-keys-by actions/uses-ctrl? k))

(defn- eval-alt-keys [k]
  (eval-keys-by actions/uses-alt? k))

(defn- eval-hotkey [k]
  (let [tool (state/get-tool)]
    (if tool
      (when (satisfies? OnKeypress tool) (on-keypress tool k))
      (cond
        (keyboard/just-ctrl?) (eval-ctrl-keys k)
        (keyboard/just-alt?) (eval-alt-keys k)
        :else (eval-root-keys k)))))

(defn- keyboard-event->key
  [event]
  {:code (.-code event)
   :ctrl (.-ctrlKey event)
   :alt (.-altKey event)
   :shift (.-shiftKey event)})

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
       (let [{:keys [code] :as key} (keyboard-event->key event)]
         (when (ctrl-codes code) (keyboard/ctrl-down?! true))
         (when (alt-codes code) (keyboard/alt-down?! true))
         (js/console.log "Keypress: " key)
         (eval-hotkey key)
         (record-suggestions!)))))
  (js/document.addEventListener
   "keyup"
   (fn [event]
     (let [{:keys [code]} (keyboard-event->key event)]
       (when (ctrl-codes code) (keyboard/ctrl-down?! false))
       (when (alt-codes code) (keyboard/alt-down?! false)))
     (record-suggestions!))))

(defn init
  "Bind keyboard input handlers."
  []
  (bind-keys)
  (record-suggestions!))
