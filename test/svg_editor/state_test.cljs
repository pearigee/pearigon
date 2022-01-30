(ns svg-editor.state-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [reagent.core :as r]
            [svg-editor.shapes.circle :refer [circle]]
            [svg-editor.state :as state]))

(defn- mock-state []
  (r/atom (state/initial-state)))

(deftest add-shape!
  (let [shape (circle [0 0] 40)
        sid (:id shape)]

    (testing "adds a shape"
      (let [s (mock-state)]
        (state/add-shape! s shape)
        (is (= (count (state/get-shapes-with-override s))
               1))))

    (testing "selects shape by default"
      (let [s (mock-state)]
        (state/add-shape! s shape)
        (is (= (:selected (state/get-shape s sid))
               true))))

    (testing "doesn't select shape when disabled by config"
      (let [s (mock-state)]
        (state/add-shape! s shape :selected? false)
        (is (= (:selected (state/get-shape s sid))
               false))))

    (testing "appends the shape to the draw-order"
      (let [s (mock-state)]
        (state/add-shape! s shape)
        (is (= (first (state/get-draw-order s))
               sid))))))

(deftest map-shape-ids!
  (let [shape-1 (circle [0 0] 40)
        sid1 (:id shape-1)
        shape-2 (circle [0 10] 40)
        sid2 (:id shape-2)
        shape-3 (circle [0 20] 40)
        sid3 (:id shape-3)]

    (testing "applys function to IDs provided"
      (let [s (mock-state)]
        (state/add-shape! s shape-1)
        (state/add-shape! s shape-2)
        (state/add-shape! s shape-3)

        (state/map-shape-ids! s #{sid1 sid3}
                              #(assoc % :r 50))

        (is (= (:r (state/get-shape s sid1)) 50))
        (is (= (:r (state/get-shape s sid2)) 40))
        (is (= (:r (state/get-shape s sid3)) 50))))))
