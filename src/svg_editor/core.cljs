(ns svg-editor.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [svg-editor.shapes :as shapes]
   [svg-editor.tools :as tools]
   [svg-editor.selection :as selection]))

(def state
  (r/atom {:shapes []
           :mouse {:page-x 0
                   :page-y 0}
           :tool nil
           :next-id 0}))

(defn shape->svg [shape]
  (case (:type shape)
    :circle [:circle {:cx (:x shape) :cy (:y shape) :r (:r shape)}]))

(defn editor []
  [:div {:style {:height "100%"}}
   [:div "Mouse position: " (str (:mouse @state))]
   [:svg {:width "100%" :height "100%"}
    (for [shape (:shapes @state)]
      ^{:key shape} [shape->svg shape])]])

(defn set-tool [tool]
  (swap! state assoc :tool tool))

(defn add-shape [shape]
  (selection/deselect-all state)
  (swap! state update-in [:shapes] conj
         (merge shape {:selected true}))
  (set-tool (tools/grab (:mouse @state))))

(defn eval-hotkey [key]
  (let [mouse (:mouse @state)]
    (case key
      :a (add-shape (shapes/circle (:page-x mouse) (:page-y mouse) 40)))))

(defn eval-mouse-tool [mouse-state]
  (case (:type (:tool @state))
    :grab (if (:left mouse-state) 
            (swap! state assoc :tool nil)
            (tools/apply-grab state mouse-state))
    nil))

(defn bind-keys []
  (js/document.addEventListener
   "keypress"
   (fn [event] (eval-hotkey (keyword (aget event "key"))))))

(defn bind-mouse []
  (let [body (aget js/document "body")]
    (.addEventListener body
                       "mousemove"
                       (fn [event]
                         (let [mouse-state {:page-x (aget event "pageX")
                                            :page-y (aget event "pageY")}]
                           (swap! state assoc :mouse
                                  mouse-state)
                           (eval-mouse-tool mouse-state))))
    (.addEventListener body
                       "click"
                       (fn [event]
                         (let [mouse-state {:page-x (aget event "pageX")
                                            :page-y (aget event "pageY")
                                            :left (= (aget event "button") 0)}]
                           (js/console.log event)
                           (eval-mouse-tool mouse-state))))))

(defn mount-root []
  (d/render [editor] (.getElementById js/document "app")))

(defn init []
  (mount-root)
  (bind-keys)
  (bind-mouse))

(defn ^:export init! []
  (init))
