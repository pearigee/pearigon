(ns svg-editor.tools.path-tool
  (:require [reagent.core :as r]
            [svg-editor.tools.protocol :refer [OnKeypress
                                               OnClick
                                               ToolRenderSVG]]
            [svg-editor.tools.grab :refer [grab]]
            [svg-editor.state :as state]
            [svg-editor.actions :as actions]
            [svg-editor.shapes.path :refer [Path path]]
            [svg-editor.shapes.point :refer [point]]
            [svg-editor.shapes.protocol :refer [render-svg]]
            [svg-editor.selection :refer [select-from-mouse-event!]]))

(defn add-point [{:keys [id points] :as path} point]
  (state/set-shape! id
                    (assoc path :points (conj points (:id point)))))

(defn add-point-at-pointer [shape type]
  (let [np (point (state/get-mouse-pos) type)]
    (state/add-shape! np :draw-order? false
                      :selected? false
                      :deselect-all? false)
    (add-point shape np)))

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
            selection (state/get-selected)]
        (cond (and
               (seq selection)
               ;; TODO Replace with #(satisfies? Point %) when CLJS 1.11 drops
               (every? #(#{:sharp :round} (:type %)) selection))
              (state/map-selected-shapes! #(assoc % :type type))

              (nil? path-id)
              (let [{:keys [id] :as shape} (path)]
                (state/add-shape! shape)
                (add-point-at-pointer shape type)
                (state/update-tool! (assoc t :path-id id)))

              path-id
              (let [shape (state/get-shape path-id)]
                (add-point-at-pointer shape type))))

      #{(actions/get-key :path-tool.toggle-closed)}
      (let [{:keys [closed?]} (state/get-shape path-id)]
        (state/merge-shape! path-id {:closed? (not closed?)}))

      #{(actions/get-key :path-tool.quit)}
      (state/pop-tool!)

      #{(actions/get-key :path-tool.grab)}
      (grab)

      nil))

  ToolRenderSVG
  (tool-render-svg [_]
    (when path-id
      (let [{pids :points} (state/get-shape path-id)
            ps (state/get-shapes-by-id-with-override pids)]
        (for [p ps]
          (r/as-element ^{:key (:id p)}  [render-svg p]))))))

(defn path-tool
  "Activate the path tool. If a single path is selected, it will become
  the path tool's target."
  []
  (let [shapes (filter is-path? (state/get-selected))
        shape (first shapes)
        {id :id} (when (and (instance? Path shape)
                            (= (count shapes) 1)) shape)]
    (state/push-tool! (PathTool. "Path Tool" :path-tool id))))
