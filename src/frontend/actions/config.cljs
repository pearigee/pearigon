(ns frontend.actions.config)

(def actions
  {:add
   {:key :a
    :display "Add Shape"}

   :add.circle
   {:key :c
    :display "Circle"}

   :add.rect
   {:key :r
    :display "Rectangle"}

   :delete
   {:key :x
    :display "Delete"
    :when [:num-selected-gt? 0]}

   :export
   {:key :ctrl-e
    :display "Export"}

   :grab
   {:key :g
    :display "Grab"
    :when [:num-selected-gt? 0]}

   :material
   {:key :m
    :display "Material Editor"}

   :path-tool
   {:display "Path Tool"
    :key :tab
    :when [:or
           [:num-selected-eq? 1 {:path? true}]
           [:num-selected-eq? 0]]}

   :path-tool.add-point-round
   {:display "Add Round Point"
    :key :q}

   :path-tool.add-point-sharp
   {:display "Add Sharp Point"
    :key :w}

   :path-tool.quit
   {:display "Quit"
    :key :tab}

   :path-tool.toggle-closed
   {:display "Toggle curve closed"
    :key :c}

   :rotate
   {:key :r
    :display "Rotate"
    :when [:num-selected-gt? 0]}

   :scale
   {:key :s
    :display "Scale"
    :when [:num-selected-gt? 0]}

   :scale.x-axis
   {:key :x
    :display "Lock to X axis"}

   :scale.y-axis
   {:key :y
    :display "Lock to Y axis"}})
