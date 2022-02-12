(ns frontend.input.hotkeys
  (:require [reagent.core :as r]))

(def actions
  {:grab
   {:key :g
    :display "Grab"}

   :scale
   {:key :s
    :display "Scale"}

   :delete
   {:key :x
    :display "Delete"}

   :rotate
   {:key :r
    :display "Rotate"}

   :export
   {:key :ctrl-e
    :display "Export"}

   :scale.x-axis
   {:key :x
    :display "Lock to X axis"}

   :scale.y-axis
   {:key :y
    :display "Lock to Y axis"}

   :path-tool
   {:display "Path Tool"
    :key :tab}

   :path-tool.add-point-sharp
   {:display "Add Sharp Point"
    :key :w}

   :path-tool.add-point-round
   {:display "Add Round Point"
    :key :q}

   :path-tool.toggle-closed
   {:display "Toggle curve closed"
    :key :c}

   :path-tool.quit
   {:display "Quit"
    :key :tab}

   :add
   {:key :a
    :display "Add Shape"}

   :add.rect
   {:key :r
    :display "Rectangle"}

   :add.circle
   {:key :c
    :display "Circle"}

   :material
   {:key :m
    :display "Material Editor"}})

(def suggestions (r/atom #{}))

(defn- get-key
  "Get the hotkey for this action."
  [id]
  (-> actions (get id) :key))

(defn- get-display
  "Get the label for this action."
  [id]
  (-> actions (get id) :display))

(defn clear-suggestions!
  "Clear the suggestion set.
   This should be done before recording new suggestions."
  []
  (reset! suggestions #{}))

(defn active?
  "Is the current action active based on the pressed key?
  If we recieve the fake hotkey :record-suggestions, add this
  action to the current suggestion set. "
  [action key]
  (if (= key :record-suggestions)
    (do (swap! suggestions conj action)
        false)
    (= key (get-key action))))

(defn get-hotkey-suggestions
  "Get active actions sorted by hotkey."
  []
  (->> @suggestions
       (map (fn [id] {:key-display (subs (str (get-key id)) 1)
                      :key (get-key id)
                      :display (get-display id)}))
       (sort-by :key-display)))
