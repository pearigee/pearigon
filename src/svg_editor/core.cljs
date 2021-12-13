(ns svg-editor.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [clojure.string :as str]
   [svg-editor.shapes :as shapes]
   [svg-editor.tools :as tools]
   [svg-editor.state :as state]))

(def s (state/initial-state))

(comment (defn shape-onclick [shape]
           (fn [event]
             (let [shift (aget event "shiftKey")]
               (js/console.log event shift)
               (when-not shift (state/deselect-all s))
               (state/select s shape (not (:selected shape))))))
         )

(defn shape->svg [shape]
  (case (:type shape)
    :circle [:circle (merge {:id (:id shape)
                             :cx (+ (:x shape) (:offset-x shape))
                             :cy (+ (:y shape) (:offset-y shape))
                             :r (:r shape)}
                            (if (:selected shape)
                              {:stroke "lime"
                               :stroke-width  "5px"}
                              {}))]))

(defn editor []
  [:div {:style {:height "100%"}}
   [:svg {:width "100%" :height "100%"}
    (for [shape (:shapes @s)]
      ^{:key shape} [shape->svg shape])]
   [:div "Mouse position: " (str (:mouse @s))]])

(defn eval-hotkey [key]
  (let [mouse (:mouse @s)]
    (case key
      :a (do (state/add-shape s (shapes/circle (:page-x mouse) (:page-y mouse) 40))
             (state/set-tool s (tools/grab mouse)))
      :g (state/set-tool s (tools/grab mouse)))))

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

(defn bind-keys []
  (js/document.addEventListener
   "keypress"
   (fn [event] (eval-hotkey (keyword (aget event "key"))))))

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
