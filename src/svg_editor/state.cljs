(ns svg-editor.state)

(defn map-shapes [state f]
  (swap! state update-in [:shapes]
         (fn [shapes]
           (map f shapes))))