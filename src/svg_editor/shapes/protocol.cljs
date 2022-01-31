(ns svg-editor.shapes.protocol)

(defprotocol RenderSVG
  "Build a hiccup representation of the SVG this shape
  represents."
  (render-svg [shape]))

(defprotocol Transform
  (translate [shape vect])
  (scale [shape vect]))

(defprotocol OnSelect
  (on-select [shape]))
