(ns frontend.state-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [reagent.core :as r]
            [frontend.shapes.circle :refer [circle]]
            [frontend.state :as state]))

(defn- mock-state []
  (r/atom (state/initial-state)))

(deftest add-shape!
  (let [shape (circle [0 0] 40)
        sid (:id shape)]

    (testing "adds a shape"
      (binding [state/*db* (mock-state)]
        (state/add-shape! shape)
        (is (= (count (state/get-shapes-with-override))
               1))))

    (testing "selects shape by default"
      (binding [state/*db* (mock-state)]
        (state/add-shape! shape)
        (is (= (:selected (state/get-shape sid))
               true))))

    (testing "doesn't select shape when disabled by config"
      (binding [state/*db* (mock-state)]
        (state/add-shape! shape :selected? false)
        (is (= (:selected (state/get-shape sid))
               false))))

    (testing "appends the shape to the draw-order"
      (binding [state/*db* (mock-state)]
        (state/add-shape! shape)
        (is (= (first (state/get-draw-order))
               sid))))))

(deftest map-shape-ids!
  (let [shape-1 (circle [0 0] 40)
        sid1 (:id shape-1)
        shape-2 (circle [0 10] 40)
        sid2 (:id shape-2)
        shape-3 (circle [0 20] 40)
        sid3 (:id shape-3)]

    (testing "applys function to IDs provided"
      (binding [state/*db* (mock-state)]
        (state/add-shape! shape-1)
        (state/add-shape! shape-2)
        (state/add-shape! shape-3)

        (state/map-shape-ids! #{sid1 sid3}
                              #(assoc % :r 50))

        (is (= (:r (state/get-shape sid1)) 50))
        (is (= (:r (state/get-shape sid2)) 40))
        (is (= (:r (state/get-shape sid3)) 50))))))
