(ns frontend.actions.config)

(def config
  {:add
   {:key {:code "KeyA" :display "a"}
    :display "Add Shape"
    :when [:num-selected-eq 0]
    :searchable true}

   :add.circle
   {:key {:code "KeyC" :display "c"}
    :display "Add Circle"}

   :add.rect
   {:key {:code "KeyR" :display "r"}
    :display "Add Rectangle"}

   :delete
   {:key {:code "KeyX" :display "x"}
    :display "Delete"
    :when [:num-selected-gt 0]
    :searchable true}

   :duplicate
   {:key {:code "KeyD" :display "d"}
    :display "Duplicate"
    :when [:num-selected-gt 0]
    :searchable true}

   :export
   {:display "Export (SVG)"
    :searchable true}

   :grab
   {:key {:code "KeyG" :display "g"}
    :display "Grab"
    :when [:num-selected-gt 0]}

   :undo
   {:key {:code "KeyZ" :ctrl true :display "z"}
    :display "Undo"
    :searchable true}

   :redo
   {:key {:code "KeyR" :ctrl true :display "r"}
    :display "Redo"
    :searchable true}

   :styles-panel
   {:key {:code "KeyS" :alt true :display "s"}
    :display "Styles (color and stroke)"
    :searchable true}

   :path-tool
   {:display "Path Tool"
    :key {:code "Tab" :display "tab"}
    :when [:or
           [:num-selected-eq 1 {:path true}]
           [:num-selected-eq 0]]}

   :path-tool.add-point-round
   {:display "Add round point"
    :key {:code "KeyQ" :display "q"}
    :when [:num-selected-eq 0]}

   :path-tool.add-point-sharp
   {:display "Add sharp point"
    :key {:code "KeyW" :display "w"}
    :when [:num-selected-eq 0]}

   :path-tool.make-point-round
   {:display "Make point round"
    :key {:code "KeyQ" :display "q"}
    :when [:num-selected-gt 0 {:point true}]}

   :path-tool.make-point-sharp
   {:display "Make point sharp"
    :key {:code "KeyW" :display "w"}
    :when [:num-selected-gt 0 {:point true}]}

   :path-tool.quit
   {:display "Quit"
    :key {:code "Tab" :display "tab"}}

   :path-tool.toggle-closed
   {:display "Toggle curve closed"
    :key {:code "KeyC" :display "c"}}

   :move-up
   {:display "Move up"
    :key {:code "BracketRight" :display "]"}
    :when [:num-selected-eq 1 {:path true}]
    :searchable true}

   :move-down
   {:display "Move down"
    :key {:code "BracketLeft" :display "["}
    :when [:num-selected-eq 1 {:path true}]
    :searchable true}

   :new-project
   {:display "New project"
    :key {:code "KeyN" :ctrl true :display "n"}
    :searchable true}

   :rotate
   {:key {:code "KeyR" :display "r"}
    :display "Rotate"
    :when [:num-selected-gt 0]}

   :save
   {:display "Save"
    :searchable true}

   :open
   {:display "Open"
    :searchable true}

   :scale
   {:key {:code "KeyS" :display "s"}
    :display "Scale"
    :when [:num-selected-gt 0]}

   :scale.x-axis
   {:key {:code "KeyX" :display "x"}
    :display "Lock to X axis"}

   :scale.y-axis
   {:key {:code "KeyY" :display "y"}
    :display "Lock to Y axis"}

   :search
   {:key {:code "Space" :display "space"}
    :display "Search"}

   :yes
   {:key {:code "KeyY" :display "y"}
    :display "Yes"}

   :no
   {:key {:code "KeyN" :display "n"}
    :display "No"}})
