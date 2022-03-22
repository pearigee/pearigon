(ns pearigon.view.components.input
  (:require
   [reagent.core :as r]))

(defn- event->str [event]
  (-> event .-target .-value))

(defn- event->float [event]
  (js/Number.parseFloat (event->str event)))

(defn- event->value [event type]
  (case type
    "text" (event->str event)
    "number" (event->float event)
    "color" (event->str event)
    :else (event->str event)))

(defn input [input-props label]
  (r/with-let [type (:type input-props)
               on-change (:on-change input-props)
               wrapped-on-change (when on-change
                                   #(on-change (event->value % type)))]
    [:div.horizontal-input
     [:label label]
     [:input.input (merge input-props
                          {:on-change wrapped-on-change})]]))
