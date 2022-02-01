(ns frontend.actions
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
    :key :tab}

   :path-tool.add-point-sharp
   {:display "Add Sharp Point"
    :key :s}

   :path-tool.add-point-round
   {:display "Add Round Point"
    :key :r}

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

(defn get-key
  [id]
  (let [{:keys [proxy]} (get actions id)]
    (if proxy
      (get-in actions [proxy :key])
      (get-in actions [id :key]))))

(defn get-display
  [id]
  (let [{:keys [proxy]} (get actions id)]
    (if proxy
      (get-in actions [proxy :display])
      (get-in actions [id :display]))))

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
                     (fn [id] {:key-display (subs (str (get-key id)) 1)
                               :key (get-key id)
                               :display (get-display id)})
                     active-keys)]
    {:tool (:display tool)
     :keys (sort-by :key-display suggestions)}))
