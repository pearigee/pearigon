(ns frontend.input.keyboard
  (:require
   [frontend.actions.handlers :refer [action->handler]]
   [frontend.state.actions :as actions]
   [frontend.state.keyboard :as keyboard]
   [frontend.state.tools :as tools]
   [frontend.tools.protocol :refer [on-keypress OnKeypress]]))

(def ctrl-codes #{"ControlLeft" "ControlRight"})
(def alt-codes #{"AltLeft" "AltRight"})

(defn- eval-keys-by [pred k]
  (let [actions
        (filter pred (keys action->handler))
        active (first (filter #(actions/active? % k) actions))]
    (when active ((get action->handler active)))))

(defn- eval-root-keys! [k]
  (eval-keys-by actions/uses-no-modifiers? k))

(defn- eval-ctrl-keys! [k]
  (eval-keys-by actions/uses-ctrl? k))

(defn- eval-alt-keys! [k]
  (eval-keys-by actions/uses-alt? k))

(defn eval-hotkey! [k]
  (let [tool (tools/get-tool)]
    (cond
      (keyboard/just-ctrl?) (eval-ctrl-keys! k)
      (keyboard/just-alt?) (eval-alt-keys! k)
      (satisfies? OnKeypress tool) (on-keypress tool k)
      :else (eval-root-keys! k))))

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
  (eval-hotkey! :record-suggestions))

(defn- bind-keys []
  (js/document.addEventListener
   "keydown"
   (fn [event]
      ;; Don't capture input events from inputs or the code editor.
     (when (and (not= (.-tagName js/document.activeElement) "INPUT")
                (nil? (-> event .-target .-cmView)))
        ;; Prevent Tab from leaving the page.
       (.preventDefault event)
       (let [{:keys [code] :as key} (keyboard-event->key event)]
         (when (ctrl-codes code) (keyboard/ctrl-down?! true))
         (when (alt-codes code) (keyboard/alt-down?! true))
         (js/console.log "Keypress: " key)
         (eval-hotkey! key)
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
