(ns svg-editor.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [svg-editor.shapes :as shapes]
   [svg-editor.tools :as tools]
   [svg-editor.state :as state]))

(def s (state/initial-state))

(defn shape->svg [shape]
  (case (:type shape)
    :circle [:circle {:cx (+ (:x shape) (:offset-x shape)) 
                      :cy (+ (:y shape) (:offset-y shape)) 
                      :r (:r shape)
                      :opacity (if (:selected shape) 0.5 1)}]))

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

(defn eval-mouse-tool [mouse-state]
  (case (:type (:tool @s))
    :grab (if (:left mouse-state) 
            (tools/finish-grab s)
            (tools/apply-grab s mouse-state))
    nil))

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
         (eval-mouse-tool mouse-state))))
    (.addEventListener
     body
     "click"
     (fn [event]
       (let [mouse-state {:page-x (aget event "pageX")
                          :page-y (aget event "pageY")
                          :left (= (aget event "button") 0)}]
         (state/set-mouse-state s (select-keys mouse-state
                                              [:page-x :page-y]))
         (eval-mouse-tool mouse-state))))))

(defn mount-root []
  (d/render [editor] (.getElementById js/document "app")))

(defn init []
  (mount-root)
  (bind-keys)
  (bind-mouse))

(defn ^:export init! []
  (init))
