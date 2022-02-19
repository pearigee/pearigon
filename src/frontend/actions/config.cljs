(ns frontend.actions.config)

(def actions
  {:add
   {:key {:code "KeyA" :display "a"}
    :display "Add Shape"
    :when [:num-selected-eq? 0]}

   :add.circle
   {:key {:code "KeyC" :display "c"}
    :display "Circle"}

   :add.rect
   {:key {:code "KeyR" :display "r"}
    :display "Rectangle"}

   :delete
   {:key {:code "KeyX" :display "x"}
    :display "Delete"
    :when [:num-selected-gt? 0]}

   :export
   {:key {:code "KeyE" :ctrl true :display "e"}
    :display "Export"}

   :grab
   {:key {:code "KeyG" :display "g"}
    :display "Grab"
    :when [:num-selected-gt? 0]}

   :styles-panel
   {:key {:code "KeyS" :alt true :display "s"}
    :display "Styles (color)"}

   :path-tool
   {:display "Path Tool"
    :key {:code "Tab" :display "tab"}
    :when [:or
           [:num-selected-eq? 1 {:path? true}]
           [:num-selected-eq? 0]]}

   :path-tool.add-point-round
   {:display "Add Round Point"
    :key {:code "KeyQ" :display "q"}}

   :path-tool.add-point-sharp
   {:display "Add Sharp Point"
    :key {:code "KeyW" :display "w"}}

   :path-tool.quit
   {:display "Quit"
    :key {:code "Tab" :display "tab"}}

   :path-tool.toggle-closed
   {:display "Toggle curve closed"
    :key {:code "KeyC" :display "c"}}

   :move-up
   {:display "Move up"
    :key {:code "BracketRight" :display "]"}
    :when [:num-selected-eq? 1 {:path? true}]}

   :move-down
   {:display "Move down"
    :key {:code "BracketLeft" :display "["}
    :when [:num-selected-eq? 1 {:path? true}]}

   :rotate
   {:key {:code "KeyR" :display "r"}
    :display "Rotate"
    :when [:num-selected-gt? 0]}

   :scale
   {:key {:code "KeyS" :display "s"}
    :display "Scale"
    :when [:num-selected-gt? 0]}

   :scale.x-axis
   {:key {:code "KeyX" :display "x"}
    :display "Lock to X axis"}

   :scale.y-axis
   {:key {:code "KeyY" :display "y"}
    :display "Lock to Y axis"}})
