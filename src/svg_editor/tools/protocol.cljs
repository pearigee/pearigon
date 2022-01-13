(ns svg-editor.tools.protocol)

(defprotocol OnMouseMove
  "Take action when the mouse is moved."
  (on-mouse-move [t state event]))

(defprotocol OnClick
  "Take action when a click event occurs."
  (on-click [t state event]))

(defprotocol OnKeypress
  "Take action on keypress."
  (on-keypress [t state key]))
