(ns frontend.actions.handlers
  (:require
   [frontend.state.undo :as undo]
   [frontend.state.core :as state]
   [frontend.tools.add :refer [add]]
   [frontend.tools.grab :refer [grab]]
   [frontend.tools.rotate :refer [rotate]]
   [frontend.tools.styles-panel :refer [styles-panel]]
   [frontend.tools.draw-order :refer [move-up move-down]]
   [frontend.tools.scale :refer [scale]]
   [frontend.tools.delete :refer [delete]]
   [frontend.tools.export :refer [export]]
   [frontend.tools.path-tool :refer [path-tool]]
   [frontend.tools.save :refer [save]]
   [frontend.tools.open :refer [open]]))

(def action->handler
  {:add add
   :scale scale
   :grab grab
   :rotate rotate
   :path-tool path-tool
   :delete delete
   :export export
   :move-up move-up
   :move-down move-down
   :styles-panel styles-panel
   :undo #(undo/undo! (state/save-state))
   :redo #(undo/redo! (state/save-state))
   :save save
   :open open})
