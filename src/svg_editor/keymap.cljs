(ns svg-editor.keymap
  (:require
    [clojure.string :as str]))

(def keymap
  {:grab {:key :g
          :display "Grab"}

   :scale {:key :s
           :display "Scale"}
   :scale.x-axis {:key :x
                  :display "Lock to X axis"}
   :scale.y-axis {:key :y
                  :display "Lock to Y axis"}

   :add {:key :a
         :display "Add Shape"}
   :add.rect {:key :r
              :display "Rectangle"}
   :add.circle {:key :c
                :display "Circle"}

   :material {:key :m
              :display "Material Editor"}})

(defn get-key
  [id]
  (get-in keymap [id :key]))

(defn get-child-keys
  [tool-type]
  (let [actions (keys keymap)
        children (filter #(str/includes? (str %) (str tool-type "."))
                         actions)]
    children))

(defn get-active-keys
  [tool-type]
  (let [actions (keys keymap)
        root-keys (filter #(not (str/includes? (str %) ".")) actions)]
    (if (nil? tool-type)
      root-keys
      (get-child-keys tool-type))))

(defn get-suggestions
  [tool]
  (let [type (:type tool)
        active-keys (get-active-keys type)
        suggestions (map
                      #(merge % {:key-display (subs (str (:key %)) 1)})
                      (vals (select-keys keymap active-keys)))]
    {:tool (:display tool)
     :keys suggestions}))
