(ns pearigon.utils.layers-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [pearigon.utils.layers :refer [move-down move-up]]))

(deftest move-up-test
  (testing "moves value forward 1 place"
    (is (= (move-up [1 2 3 4] 2) [1 3 2 4])))

  (testing "does not move past the end of the list"
    (is (= (move-up [1 2 3 4] 4) [1 2 3 4])))

  (testing "does nothing if value not found"
    (is (= (move-up [1 2 3 4] 5) [1 2 3 4])))

  (testing "does nothing if the list is empty"
    (is (= (move-up [] 5) []))))

(deftest move-down-test
  (testing "moves value backward 1 place"
    (is (= (move-down [1 2 3 4] 2) [2 1 3 4])))

  (testing "does not move past the begining of the list"
    (is (= (move-down [1 2 3 4] 1) [1 2 3 4])))

  (testing "does nothing if value not found"
    (is (= (move-down [1 2 3 4] 5) [1 2 3 4])))

  (testing "does nothing if the list is empty"
    (is (= (move-down [] 5) []))))
