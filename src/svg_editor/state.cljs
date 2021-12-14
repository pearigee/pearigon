(ns svg-editor.state)

(defn initial-state []
  {:shapes []
   :mouse {:page-x 0
           :page-y 0}
   :tool nil
   :next-id 0})

(defn get-mouse-state [state]
  (:mouse @state))

(defn get-tool [state]
  (:tool @state))

(defn get-shapes [state]
  (:shapes @state))

(defn get-selected[state]
  (filter
   :selected
   (get-shapes state)))

(defn map-shapes [state f]
  (swap! state update-in [:shapes]
         (fn [shapes]
           (map f shapes))))

(defn deselect-all [state]
  (map-shapes state #(assoc % :selected false)))

(defn select-id
  ([state id selected?] (map-shapes state #(if (= id (:id %))
                                                (merge % {:selected selected?})
                                                %)))
  ([state id] (select-id state id true)))

(defn set-tool [state tool]
  (js/console.log "Tool selected: " tool)
  (swap! state assoc :tool tool))

(defn add-shape [state shape]
  (deselect-all state)
  (swap! state update-in [:shapes] conj
         (merge shape {:selected true})))

(defn set-mouse-state [state mouse-state]
  (swap! state assoc :mouse mouse-state))