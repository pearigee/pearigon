(ns svg-editor.state
  (:require [svg-editor.keymap :as keys]))

(defn initial-state []
  {:shapes []
   :mouse {:pos [0 0]}
   :materials {:default {:display "Default"
                         :color "#000"}}
   :active-material :default
   :panel nil
   :tool nil
   :next-id 0
   :suggestions (keys/get-suggestions nil)})

(defn get-mouse-state [state]
  (:mouse @state))

(defn get-tool [state]
  (:tool @state))

(defn get-shapes [state]
  (:shapes @state))

(defn get-materials [state]
  (:materials @state))

(defn get-material [state id]
  (id (:materials @state)))

(defn get-panel [state]
  (:panel @state))

(defn get-selected[state]
  (filter
   :selected
   (get-shapes state)))

(defn map-shapes! [state f]
  (swap! state update-in [:shapes]
         (fn [shapes]
           (mapv f shapes))))

(defn map-selected-shapes! [state f]
  (swap! state update-in [:shapes]
         (fn [shapes]
           (mapv (fn [shape] 
                  (if (:selected shape) 
                    (f shape)
                    shape)) shapes))))

(defn deselect-all! [state]
  (map-shapes! state #(assoc % :selected false)))

(defn select-id!
  ([state id selected?] (map-shapes! state #(if (= id (:id %))
                                                (merge % {:selected selected?})
                                                %)))
  ([state id] (select-id! state id true)))

(defn set-suggestions! [state suggestions]
  (swap! state assoc :suggestions suggestions))

(defn set-panel! [state panel]
  (swap! state assoc :panel panel))

(defn set-material! [state id value]
  (swap! state update-in [:materials] assoc id value))

(defn set-active-material! [state id]
  (swap! state assoc :active-material id))

(defn add-material! [state id value]
  (swap! state update-in [:materials] assoc id value))

(defn set-tool! [state tool]
  (js/console.log "Tool selected: " tool)
  (swap! state assoc :tool tool)
  (set-suggestions! state (keys/get-suggestions (get-tool state))))

(defn add-shape-and-select! [state shape]
  (deselect-all! state)
  (swap! state update-in [:shapes] conj
         (merge shape {:selected true
                       :material (:active-material @state)})))

(defn set-mouse-state! [state mouse-state]
  (swap! state assoc :mouse mouse-state))