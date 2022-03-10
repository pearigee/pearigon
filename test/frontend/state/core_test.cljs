(ns frontend.state.core-test
  (:require
   [cljs.test :refer-macros [deftest is testing use-fixtures]]
   [frontend.shapes.path.path :as p]
   [frontend.state.core :as state]
   [frontend.state.undo :as undo]))

(use-fixtures :each
  {:before (fn []
             (undo/init!)
             (state/init! {}))})

(deftest test-get-shape
  (let [shape (state/add-shape! (p/rectangle [0 0] 40))
        id (:id shape)
        points (:points shape)]

    (testing "basic lookup"
      (is (not (nil? (state/get-shape id))))
      (is (= (:id (state/get-shape id)) id)))

    (testing "nested lookup"
      (is (= (:id (state/get-shape (:id (first points))))
             (:id (first points)))))))

(deftest test-add-shape!
  (let [shape (p/rectangle [0 0] 40)]
    (let [{sid :id} (state/add-shape! shape)]

      (testing "adds a shape"
        (is (= (count (state/get-shapes-with-override))
               1)))

      (testing "selects shape by default"
        (is (= (:selected (state/get-shape sid))
               true)))

      (testing "appends the shape to the draw-order"
        (is (= (first (state/get-draw-order))
               sid))))

    (let [{sid :id} (state/add-shape! shape :selected? false)]
      (testing "doesn't select shape when disabled by config"
        (is (contains? #{nil false} (:selected (state/get-shape sid))))))))

(deftest test-map-shape-ids!
  (let [shape (p/rectangle [0 0] 10)
        {sid1 :id} (state/add-shape! shape)
        {sid2 :id} (state/add-shape! shape)
        {sid3 :id} (state/add-shape! shape)]

    (state/map-shape-ids! #{sid1 sid3}
                          #(assoc % :test-key true))

    (testing "applys function to IDs provided"
      (is (= (:test-key (state/get-shape sid1)) true))
      (is (= (:test-key (state/get-shape sid2)) nil))
      (is (= (:test-key (state/get-shape sid3)) true)))))
