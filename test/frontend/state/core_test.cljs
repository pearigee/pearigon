(ns frontend.state.core-test
  (:require
   [cljs.test :refer-macros [deftest is testing use-fixtures]]
   [frontend.shapes.path.path :as p]
   [frontend.state.core :as state]
   [frontend.state.undo :as undo]))

(use-fixtures :each
  {:before (fn []
             (undo/init!)
             (state/init!))})

(deftest test-get-shape
  (let [shape (p/rectangle [0 0] 40)
        id (:id shape)
        points (:points shape)]

    (state/add-shape! shape)

    (testing "basic lookup"
      (is (not (nil? (state/get-shape id))))
      (is (= (:id (state/get-shape id)) id)))

    (testing "nested lookup"
      (is (= (:id (state/get-shape (:id (first points))))
             (:id (first points)))))))

(deftest test-add-shape!
  (let [shape (p/rectangle [0 0] 40)
        sid (:id shape)]

    (state/add-shape! shape)

    (testing "adds a shape"
      (is (= (count (state/get-shapes-with-override))
             1)))

    (testing "selects shape by default"
      (state/add-shape! shape)
      (is (= (:selected (state/get-shape sid))
             true)))

    (testing "doesn't select shape when disabled by config"
      (state/add-shape! shape :selected? false)
      (is (contains? #{nil false} (:selected (state/get-shape sid)))))

    (testing "appends the shape to the draw-order"
      (state/add-shape! shape)
      (is (= (first (state/get-draw-order))
             sid)))))

(deftest test-map-shape-ids!
  (let [shape-1 (p/rectangle [0 0] 10)
        sid1 (:id shape-1)
        shape-2 (p/rectangle [0 10] 10)
        sid2 (:id shape-2)
        shape-3 (p/rectangle [0 20] 10)
        sid3 (:id shape-3)]

    (state/add-shape! shape-1)
    (state/add-shape! shape-2)
    (state/add-shape! shape-3)

    (state/map-shape-ids! #{sid1 sid3}
                          #(assoc % :test-key true))

    (testing "applys function to IDs provided"
      (is (= (:test-key (state/get-shape sid1)) true))
      (is (= (:test-key (state/get-shape sid2)) nil))
      (is (= (:test-key (state/get-shape sid3)) true)))))
