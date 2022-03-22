(ns pearigon.actions.handlers
  (:require
   [pearigon.state.core :as state]
   [pearigon.state.viewport :as viewport]
   [pearigon.tools.add :refer [add]]
   [pearigon.tools.delete :refer [delete]]
   [pearigon.tools.draw-order :refer [move-down move-up]]
   [pearigon.tools.duplicate :refer [duplicate]]
   [pearigon.tools.export :refer [export]]
   [pearigon.tools.grab :refer [grab]]
   [pearigon.tools.new-project :refer [new-project]]
   [pearigon.tools.open :refer [open]]
   [pearigon.tools.path-tool :refer [path-tool]]
   [pearigon.tools.rotate :refer [rotate]]
   [pearigon.tools.save :refer [save]]
   [pearigon.tools.scale :refer [scale]]
   [pearigon.tools.styles-panel :refer [styles-panel]]))

(def action->handler
  {:add add
   :code viewport/toggle-code-showing!
   :scale scale
   :grab grab
   :rotate rotate
   :path-tool path-tool
   :delete delete
   :duplicate duplicate
   :export export
   :move-up move-up
   :move-down move-down
   :styles-panel styles-panel
   :undo state/undo!
   :redo state/redo!
   :reset-zoom viewport/reset-zoom!
   :new-project new-project
   :save save
   :search viewport/toggle-search-showing!
   :open open})
