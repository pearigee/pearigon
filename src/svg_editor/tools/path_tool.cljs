(ns svg-editor.tools.path-tool
  (:require [svg-editor.tools.protocol :refer [OnKeypress
                                               OnClick]]
            [svg-editor.tools.grab :refer [grab]]
            [svg-editor.state :as state]
            [svg-editor.actions :as actions]
            [svg-editor.shapes.path :refer [Path path]]
            [svg-editor.shapes.point :refer [point]]
            [svg-editor.selection :refer [select-from-mouse-event!]]))

(defn add-point [s {:keys [id points] :as path} point]
  (state/set-shape! s id
                    (assoc path :points (conj points (:id point)))))

(defn add-point-at-pointer [s shape type]
  (let [np (point (state/get-mouse-pos s) type)]
    (state/add-shape! s np :draw-order? false
                      :selected? false
                      :deselect-all? false)
    (add-point s shape np)))

(defn is-path? [shape]
  ;; TODO: (satisfies? Path shape) broken, but fixed in CLJS 1.11.
  (contains? shape :points))

(defn key->point-type [k]
  (condp = k
    (actions/get-key :path-tool.add-point-sharp) :sharp
    (actions/get-key :path-tool.add-point-round) :round))

(defrecord PathTool [display action path-id]

  OnClick
  (on-click [_ s event]
    (select-from-mouse-event! s event))

  OnKeypress
  (on-keypress [t s k]
    (condp contains? k
      #{(actions/get-key :path-tool.add-point-sharp)
        (actions/get-key :path-tool.add-point-round)}
      (let [type (key->point-type k)
            selection (state/get-selected s)]
        (cond (and
               (seq selection)
               ;; TODO Replace with #(satisfies? Point %) when CLJS 1.11 drops
               (every? #(#{:sharp :round} (:type %)) selection))
              (state/map-selected-shapes! s #(assoc % :type type))

              (nil? path-id)
              (let [{:keys [id] :as shape} (path)]
                (state/add-shape! s shape)
                (add-point-at-pointer s shape type)
                (state/update-tool! s (assoc t :path-id id)))

              path-id
              (let [shape (state/get-shape s path-id)]
                (add-point-at-pointer s shape type))))

      #{(actions/get-key :path-tool.quit)}
      (state/pop-tool! s)

      #{(actions/get-key :path-tool.grab)}
      (grab s)

      nil)))

(defn path-tool
  "Activate the path tool. If a single path is selected, it will become
  the path tool's target."
  [s]
  (let [shapes (filter is-path? (state/get-selected s))
        shape (first shapes)
        {id :id} (when (and (instance? Path shape)
                            (= (count shapes) 1)) shape)]
    (state/push-tool! s (PathTool. "Path Tool" :path-tool id))))
