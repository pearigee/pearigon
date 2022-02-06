(ns frontend.tools.path-tool
  (:require [reagent.core :as r]
            [com.rpl.specter :as sp :include-macros true]
            [frontend.tools.protocol :refer [OnKeypress
                                             OnClick
                                             ToolRenderSVG]]
            [frontend.math :refer [avg]]
            [frontend.tools.grab :refer [grab]]
            [frontend.tools.scale :refer [scale]]
            [frontend.state :as state]
            [frontend.actions :as actions]
            [frontend.shapes.path :refer [Path path]]
            [frontend.shapes.point :refer [point]]
            [frontend.shapes.protocol :refer [render-svg]]
            [frontend.selection :refer [select-from-mouse-event!]]))

(defn add-point [{:keys [id points] :as path} point
                 & {:keys [index] :or {index (count points)}}]
  (state/set-shape!
   id
   (assoc path :points
          (sp/setval (sp/before-index index) point points))))

(defn add-point-at-pos [{:keys [points] :as shape}
                        pos
                        type
                        & {:keys [index selected? deselect-all?]
                           :or {index (count points)
                                selected? false
                                deselect-all? false}}]
  (let [np (point pos type)]
    (add-point shape np :index index)
    (when deselect-all? (state/deselect-all!))
    (when selected? (state/select-id! (:id np)))))

(defn is-path? [shape]
  ;; TODO: (satisfies? Path shape) broken, but fixed in CLJS 1.11.
  (contains? shape :points))

(defn key->point-type [k]
  (condp = k
    (actions/get-key :path-tool.add-point-sharp) :sharp
    (actions/get-key :path-tool.add-point-round) :round))

(defrecord PathTool [display action path-id]

  OnClick
  (on-click [_ event]
    (select-from-mouse-event! event))

  OnKeypress
  (on-keypress [t k]
    (condp contains? k
      #{(actions/get-key :path-tool.add-point-sharp)
        (actions/get-key :path-tool.add-point-round)}
      (let [type (key->point-type k)
            selection (state/get-selected)
            mpos (state/get-mouse-pos)]
        (cond (and
               (seq selection)
               ;; TODO Replace with #(satisfies? Point %) when CLJS 1.11 drops
               (every? #(#{:sharp :round} (:type %)) selection))
              (state/map-selected-shapes! #(assoc % :type type))

              (nil? path-id)
              (let [{:keys [id] :as shape} (path)]
                (state/add-shape! shape)
                (add-point-at-pos shape mpos type)
                (state/update-tool! (assoc t :path-id id)))

              path-id
              (let [shape (state/get-shape path-id)]
                (add-point-at-pos shape mpos type))))

      #{(actions/get-key :path-tool.toggle-closed)}
      (let [{:keys [closed?]} (state/get-shape path-id)]
        (state/merge-shape! path-id {:closed? (not closed?)}))

      #{(actions/get-key :path-tool.quit)}
      (state/pop-tool!)

      #{(actions/get-key :path-tool.grab)}
      (grab)

      #{(actions/get-key :path-tool.scale)}
      (scale)

      nil))

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
    (state/push-tool! (PathTool. "Path Tool" :path-tool id))))
