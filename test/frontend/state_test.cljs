(ns frontend.state-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [reagent.core :as r]
            [frontend.state :as state]))

(defn- mock-state []
  (r/atom (state/initial-state)))

(defn shape [pos]
  {:id (str "shape-" (random-uuid))
   :pos pos})

(deftest add-shape!
  (let [shape (shape [0 0])
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
  (let [shape-1 (shape [0 0])
        sid1 (:id shape-1)
        shape-2 (shape [0 10])
        sid2 (:id shape-2)
        shape-3 (shape [0 20])
        sid3 (:id shape-3)]

    (testing "applys function to IDs provided"
      (binding [state/*db* (mock-state)]
        (state/add-shape! shape-1)
        (state/add-shape! shape-2)
        (state/add-shape! shape-3)

        (state/map-shape-ids! #{sid1 sid3}
                              #(assoc % :pos [0 10]))

        (is (= (:pos (state/get-shape sid1)) [0 10]))
        (is (= (:pos (state/get-shape sid2)) [0 10]))
        (is (= (:pos (state/get-shape sid3)) [0 10]))))))
