(ns frontend.actions.handlers
  (:require
   [frontend.state.core :as state]
   [frontend.tools.add :refer [add]]
   [frontend.tools.delete :refer [delete]]
   [frontend.tools.draw-order :refer [move-down move-up]]
   [frontend.tools.export :refer [export]]
   [frontend.tools.grab :refer [grab]]
   [frontend.tools.open :refer [open]]
   [frontend.tools.new-project :refer [new-project]]
   [frontend.tools.path-tool :refer [path-tool]]
   [frontend.tools.rotate :refer [rotate]]
   [frontend.tools.save :refer [save]]
   [frontend.tools.scale :refer [scale]]
   [frontend.tools.styles-panel :refer [styles-panel]]))

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
   :undo state/undo!
   :redo state/redo!
   :new-project new-project
   :save save
   :open open})

(defn execute-action! [key]
  (when-let [action (get action->handler key)]
    (action)))
