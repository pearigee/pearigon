(ns frontend.math-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [frontend.math :refer [v+ v-]]))

(deftest test-v+
  (testing "basic addition"
    (is (= (v+ [1 2] [1 2]) [2 4]))))

(deftest test-v-
  (testing "basic subtracting"
    (is (= (v- [1 1] [1 1]) [0 0]))))

(comment (run-tests))
