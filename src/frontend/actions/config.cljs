(ns frontend.actions.config)

(def actions
  {:grab
   {:key :g
    :display "Grab"
    :when [:num-selected-gt? 0]}

   :scale
   {:key :s
    :display "Scale"
    :when [:num-selected-gt? 0]}

   :delete
   {:key :x
    :display "Delete"
    :when [:num-selected-gt? 0]}

   :rotate
   {:key :r
    :display "Rotate"
    :when [:num-selected-gt? 0]}

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
    :key :tab
    :when [:or
           [:num-selected-eq? 1 {:path? true}]
           [:num-selected-eq? 0]]}

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
