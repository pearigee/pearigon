(ns svg-editor.state-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [reagent.core :as r]
            [svg-editor.shapes.circle :refer [circle]]
            [svg-editor.state :as state]))

(defn- mock-state []
  (r/atom (state/initial-state)))

(deftest add-shape!
  (let [shape (circle [0 0] 40)
        s-id (:id shape)]

    (testing "adds a shape"
      (let [s (mock-state)]
        (state/add-shape! s shape)
        (is (= (count (state/get-shapes-with-override s))
               1))))

    (testing "selects shape by default"
      (let [s (mock-state)]
        (state/add-shape! s shape)
        (is (= (:selected (state/get-shape s s-id))
               true))))

    (testing "doesn't select shape when disabled by config"
      (let [s (mock-state)]
        (state/add-shape! s shape :selected false)
        (is (= (:selected (state/get-shape s s-id))
               false))))

    (testing "appends the shape to the draw-order"
      (let [s (mock-state)]
        (state/add-shape! s shape)
        (is (= (first (state/get-draw-order s))
               s-id))))))
