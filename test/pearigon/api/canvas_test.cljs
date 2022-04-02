(ns pearigon.api.canvas-test
  (:require
   [cljs.test :refer-macros [deftest is testing use-fixtures]]
   [pearigon.state.core :as state]
   [pearigon.state.undo :as undo]
   [pearigon.api.canvas :as canvas]
   [pearigon.api.shapes :as shapes]))

(use-fixtures :each
  {:before (fn []
             (undo/init!)
             (state/init! {}))})

(deftest test-add-shape!
  (let [shape (shapes/rectangle [0 0] 40)]
    (let [{sid :id} (canvas/add-shape! shape)]

      (testing "adds a shape"
        (is (= (count (canvas/shapes))
               1)))

      (testing "selects shape by default"
        (is (= (canvas/selected? sid)
               true)))

      (testing "appends the shape to the draw-order"
        (is (= (first (canvas/draw-order))
               sid))))

    (let [{sid :id} (canvas/add-shape! shape {:selected false})]
      (testing "doesn't select shape when disabled by config"
        (is (not (canvas/selected? sid)))))))
