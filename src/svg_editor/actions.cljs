(ns svg-editor.actions
  (:require
   [clojure.string :as str]))

(def actions
  {:grab
   {:key :g
    :display "Grab"}

   :scale
   {:key :s
    :display "Scale"}

   :scale.x-axis
   {:key :x
    :display "Lock to X axis"}

   :scale.y-axis
   {:key :y
    :display "Lock to Y axis"}

   :path-tool
   {:display "Path Tool"
    :key :p}

   :path-tool.add-point
   {:display "Add Point"
    :key :a}

   :path-tool.quit
   {:display "Quit"
    :key :q}

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

(defn get-key
  [id]
  (get-in actions [id :key]))

(defn get-child-keys
  [tool-type]
  (let [actions (keys actions)
        children (filter #(str/includes? (str %) (str tool-type "."))
                         actions)]
    children))

(defn get-active-keys
  [action]
  (let [actions (keys actions)
        root-keys (filter #(not (str/includes? (str %) ".")) actions)]
    (if (nil? action)
      root-keys
      (get-child-keys action))))

(defn get-key-suggestions
  "Get active hotkeys (for the current tool) sorted by key."
  [tool]
  (let [action (:action tool)
        active-keys (get-active-keys action)
        suggestions (map
                     #(merge % {:key-display (subs (str (:key %)) 1)})
                     (vals (select-keys actions active-keys)))]
    {:tool (:display tool)
     :keys (sort-by :key-display suggestions)}))
