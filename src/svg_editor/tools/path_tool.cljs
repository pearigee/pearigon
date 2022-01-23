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

(defn add-point-at-pointer [s shape]
  (let [np (point (state/get-mouse-pos s) 10)]
    (state/add-shape! s np :draw-order? false
                      :selected? false
                      :deselect-all? false)
    (add-point s shape np)))

(defrecord PathTool [display action path-id]

  OnClick
  (on-click [_ s event]
    (select-from-mouse-event! s event))

  OnKeypress
  (on-keypress [t s k]
    (condp = k
      (actions/get-key :path-tool.add-point)
      (do
        (when (nil? path-id)
          (let [{:keys [id] :as shape} (path s)]
            (state/add-shape! s shape)
            (add-point-at-pointer s shape)
            (state/update-tool! s (assoc t :path-id id))))
        (when path-id
          (let [shape (state/get-shape s path-id)]
            (add-point-at-pointer s shape))))

      (actions/get-key :path-tool.quit)
      (state/pop-tool! s)

      (actions/get-key :path-tool.grab)
      (grab s)

      nil)))

(defn path-tool
  [s]
  (let [shapes (state/get-selected s)
        shape (first shapes)
        {id :id} (when (and (instance? Path shape)
                            (= (count shapes) 1)) shape)]
    (state/push-tool! s (PathTool. "Path Tool" :path-tool id))))
