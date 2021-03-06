(ns pearigon.tools.path-tool
  (:require
   [com.rpl.specter :as sp :include-macros true]
   [pearigon.math :refer [avg]]
   [pearigon.shapes.path.path :refer [Path path]]
   [pearigon.shapes.path.point :refer [point]]
   [pearigon.shapes.protocol :refer [render-svg]]
   [pearigon.state.actions :as actions]
   [pearigon.state.core :as state]
   [pearigon.state.mouse :as mouse]
   [pearigon.state.tools :as tools]
   [pearigon.state.viewport :as viewport]
   [pearigon.tools.delete :refer [delete]]
   [pearigon.tools.grab :refer [grab]]
   [pearigon.tools.protocol :refer [OnClick OnKeypress ToolRenderSVG]]
   [pearigon.tools.rotate :refer [rotate]]
   [pearigon.tools.scale :refer [scale]]
   [reagent.core :as r]))

(defn add-point [{:keys [id points]} point
                 & {:keys [index] :or {index (count points)}}]
  (state/merge-shape!
   id
   {:points (sp/setval (sp/before-index index) point points)}))

(defn add-point-at-pos [{:keys [points id] :as shape}
                        pos
                        type
                        & {:keys [index selected? deselect-all?]
                           :or {index (count points)
                                selected? false
                                deselect-all? false}}]
  (let [np (point pos type id)]
    (add-point shape np :index index)
    (when deselect-all? (state/deselect-all!))
    (when selected? (state/select-id! (:id np)))))

(defn is-path? [shape]
  ;; TODO: (satisfies? Path shape) broken, but fixed in CLJS 1.11.
  (contains? shape :points))

(defn key->point-type [k]
  (cond
    (actions/active? :path-tool.add-point-sharp k) :sharp
    (actions/active? :path-tool.add-point-round k) :round))

(defrecord PathTool [display action path-id]

  OnClick
  (on-click [_ event]
    (state/select-from-mouse-event! event))

  OnKeypress
  (on-keypress [t k]
    (cond
      (or (actions/active? :path-tool.add-point-sharp k)
          (actions/active? :path-tool.add-point-round k))
      (let [type (key->point-type k)
            mpos (mouse/pos)]
        (cond (nil? path-id)
              (let [{:keys [id] :as shape} (path)]
                (state/add-shape! shape :selected? false)
                (add-point-at-pos shape mpos type)
                (tools/update-tool! (assoc t :path-id id)))

              path-id
              (let [shape (state/get-shape path-id)]
                (add-point-at-pos shape mpos type))))

      (actions/active? :path-tool.make-point-round k)
      (state/map-selected-shapes! #(assoc % :type :round))

      (actions/active? :path-tool.make-point-sharp k)
      (state/map-selected-shapes! #(assoc % :type :sharp))

      (actions/active? :path-tool.toggle-closed k)
      (let [{:keys [closed?]} (state/get-shape path-id)]
        (state/merge-shape! path-id {:closed? (not closed?)}))

      (actions/active? :path-tool.quit k)
      (tools/pop-tool!)

      (actions/active? :grab k)
      (grab)

      (actions/active? :scale k)
      (scale)

      (actions/active? :rotate k)
      (rotate)

      (actions/active? :delete k)
      (do (delete)
          ;; If the root has been deleted, exit the tool.
          (when (nil? (state/get-shape path-id))
            (tools/pop-tool!)))

      (actions/active? :search k)
      (viewport/toggle-search-showing!)

      :else nil))

  ToolRenderSVG
  (tool-render-svg [_]
    (r/as-element
     (when path-id
       (let [{points :points closed? :closed? :as path}
             (state/get-shape path-id)
             ps (state/get-shapes-by-id-with-override (map :id points))
             edges (into [] (partition 2 1 (if (and (> (count ps) 2)
                                                    closed?)
                                             (conj ps (first ps))
                                             ps)))]
         [:g
          (for [[{[x1 y1] :pos id :id type :type :as p} {[x2 y2] :pos}] edges]
            (let [[cx cy] (avg [[x1 y1] [x2 y2]])]
              ^{:key id}
              [:g
               [:line {:x1 x1 :y1 y1 :x2 x2 :y2 y2
                       :class "mesh-line"}]
               [:circle {:cx cx :cy cy :r 5
                         :class "add-point-button"
                         :on-click
                         #(add-point-at-pos
                           path [cx cy] type
                           :index (inc (.indexOf points p))
                           :selected? true
                           :deselect-all? true)}]]))
          (for [p ps]
            ^{:key (:id p)} [render-svg p])])))))

(defn path-tool
  "Activate the path tool. If a single path is selected, it will become
  the path tool's target."
  []
  (let [shapes (filter is-path? (state/get-selected))
        shape (first shapes)
        {id :id} (when (and (instance? Path shape)
                            (= (count shapes) 1)) shape)]
    (tools/push-tool! (->PathTool "Path Tool" :path-tool id))))
