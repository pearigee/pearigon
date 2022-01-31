(ns svg-editor.tools.protocol)

(defprotocol OnMouseMove
  "Take action when the mouse is moved."
  (on-mouse-move [t event]))

(defprotocol OnClick
  "Take action when a click event occurs."
  (on-click [t event]))

(defprotocol OnKeypress
  "Take action on keypress."
  (on-keypress [t key]))

(defprotocol ToolRenderSVG
  "Render a tool layer on top of the shapes."
  (tool-render-svg [t]))
