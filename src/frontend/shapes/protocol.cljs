(ns frontend.shapes.protocol)

(defprotocol RenderSVG
  "Build a hiccup representation of the SVG this shape
  represents."
  (render-svg [shape]))

(defprotocol Transform
  (transform [shape matrix]))

(defprotocol OnSelect
  (on-select [shape]))
