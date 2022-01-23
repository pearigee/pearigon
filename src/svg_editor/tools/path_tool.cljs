(ns svg-editor.tools.path-tool
  (:require [svg-editor.tools.protocol :refer [OnKeypress
                                               OnClick]]
            [svg-editor.state :as state]
            [svg-editor.actions :as actions]
            [svg-editor.shapes.path :refer [Path path]]
            [svg-editor.shapes.point :refer [point]]))

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
  (on-click [t s event]
    (js/console.log event))

  OnKeypress
  (on-keypress [t s k]
    (when (and (nil? path-id)
               (= k (actions/get-key :path-tool.add-point)))
      (let [{:keys [id] :as shape} (path s)]
        (state/add-shape! s shape)
        (add-point-at-pointer s shape)
        (state/set-tool! s (assoc t :path-id id))))

    (when (and path-id
               (= k (actions/get-key :path-tool.add-point)))
      (let [shape (state/get-shape s path-id)]
        (add-point-at-pointer s shape)))

    (when (= k (actions/get-key :path-tool.quit))
      (state/set-tool! s nil))))

(defn path-tool
  [s]
  (let [shapes (state/get-selected s)
        shape (first shapes)
        {id :id} (when (and (instance? Path shape)
                            (= (count shapes) 1)) shape)]
    (state/set-tool! s (PathTool. "Path Tool" :path-tool id))))
