(ns frontend.actions
  (:require [clojure.string :as str]
            [reagent.core :as r]))

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

   :path-tool.scale
   {:display "Scale"
    :key :s}

   :path-tool.delete
   {:display "Delete"
    :key :x}

   :path-tool.toggle-closed
   {:display "Toggle curve closed"
    :key :c}

   :path-tool.quit
   {:display "Quit"
    :key :tab}

   :path-tool.grab
   {:proxy :grab}

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
  [id]
  (let [{:keys [proxy]} (get actions id)]
    (if proxy
      (get-in actions [proxy :key])
      (get-in actions [id :key]))))

(defn- get-display
  [id]
  (let [{:keys [proxy]} (get actions id)]
    (if proxy
      (get-in actions [proxy :display])
      (get-in actions [id :display]))))

(defn clear-suggestions! []
  (reset! suggestions #{}))

(defn active? [action key]
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
