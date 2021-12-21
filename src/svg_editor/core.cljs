(ns svg-editor.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [clojure.string :as str]
   [svg-editor.tools :as tools]
   [svg-editor.state :as state]
   [svg-editor.svg :as svg]))

(def s (r/atom (state/initial-state)))

(defn editor []
  [:div {:style {:height "100%"}}
   [:svg {:width "100%" :height "100%"}
    (for [shape (:shapes @s)]
      ^{:key shape} [svg/shape->svg shape])]
   [:div "Mouse position: " (str (:mouse @s))]])

(defn eval-hotkey [key]
  (let [tool-key-callback (:on-keypress (state/get-tool s))]
    (if tool-key-callback
      (do 
        (js/console.log "Calling tool callback: " key)
        (tool-key-callback s key))
      (let [mouse (:mouse @s)]
        (js/console.log "Getting tool for hotkey: " key)
        (case key
          :a (tools/add-shape s)
          :g (tools/grab s mouse)
          nil)))))

(defn eval-mouse-move [mouse-state]
  (case (:type (:tool @s))
    :grab (tools/apply-grab s mouse-state)
    nil))

(defn eval-mouse-click [mouse-state]
  (js/console.log "click" mouse-state)
  (case (:type (:tool @s))
    :grab (tools/finish-grab s)
    (let [target-id (:target-id mouse-state)] 
      (if (str/starts-with? target-id "shape-")
        (do (when-not (:shift mouse-state) (state/deselect-all s))
            (state/select-id s target-id))
        (state/deselect-all s)))))

(defn keyboard-event->key [event]
  (let [key (aget event "key")
        shift (aget event "shiftKey")
        ctrl (aget event "ctrlKey")]
    (keyword (str (if shift "shift-" "")
                  (if ctrl "ctrl-" "")
                  key))))

(defn bind-keys []
  (js/document.addEventListener
   "keypress"
   (fn [event] 
     (let [key (keyboard-event->key event)]
       (js/console.log "Keypress: " key)
       (eval-hotkey key)))))

(defn bind-mouse []
  (let [body (aget js/document "body")]
    (.addEventListener
     body
     "mousemove"
     (fn [event]
       (let [mouse-state {:page-x (aget event "pageX")
                          :page-y (aget event "pageY")}]
         (state/set-mouse-state s mouse-state)
         (eval-mouse-move mouse-state))))
    (.addEventListener
     js/document
     "mousedown"
     (fn [event]
       (let [mouse-state {:page-x (aget event "pageX")
                          :page-y (aget event "pageY")
                          :shift (aget event "shiftKey")
                          :target-id (aget (aget event "target") "id")}]
         (state/set-mouse-state s (select-keys mouse-state
                                              [:page-x :page-y]))
         (eval-mouse-click mouse-state))))))

(defn mount-root []
  (d/render [editor] (.getElementById js/document "app")))

(defn init []
  (mount-root)
  (bind-keys)
  (bind-mouse))

(defn ^:export init! []
  (init))
