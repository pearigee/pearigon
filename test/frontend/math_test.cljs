(ns frontend.math-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [frontend.math :refer [v+ v- v* dot mv* mm* sin
                                   cos pi translate rotate scale
                                   transform]]))

(defn difference [a b]
  (js/Math.abs (- a b)))

(defn- v-close-too? [[x y] [a b]]
  (let [tolerance 0.0001]
    (and (< (difference x a) tolerance)
         (< (difference y b) tolerance))))

(deftest test-v+
  (testing "basic addition"
    (is (= (v+ [1 2] [1 2]) [2 4]))
    (is (= (v+ [1 2] [1 2] [1 2]) [3 6]))))

(deftest test-v-
  (testing "basic subtracting"
    (is (= (v- [1 1] [1 1]) [0 0]))
    (is (= (v- [3 3] [2 2] [1 1]) [0 0]))))

(deftest test-v*
  (testing "basic multiplication"
    (is (= (v* [2 2] [2 2]) [4 4]))
    (is (= (v* [2 2] [2 2] [2 2]) [8 8]))))

(deftest test-dot
  (is (= (dot [1 1 1] [1 1 1]) 3))
  (is (= (dot [2 2 2] [2 2 2]) 12)))

(deftest test-mv*
  (testing "basic transformations"
    ;; Transformation by [-1 1]
    (is (= (mv* [[1 0 -1]
                 [0 1 1]]
                [1 1])
           [0 2]))

    ;; Scale by [2 3]
    (is (= (mv* [[2 0 0]
                 [0 3 0]]
                [1 1])
           [2 3]))

    ;; Rotation PI/2 radians
    (is (v-close-too? (mv* [[(cos (/ pi 2)) (sin (/ pi 2)) 0]
                            [(- (sin (/ pi 2))) (cos (/ pi 2)) 0]
                            [0 0 1]]
                            [1 1])
                      [1 -1]))))

(deftest test-mm*

  (testing "basic multiplication"
    (is (= (mm* [[1 0 2]
                 [0 1 2]]
                [[2 0 0]
                 [0 2 0]])
           [[2 0 2]
            [0 2 2]])))

  (testing "compound transformations"

    (testing "[1 1] -> trans(1 1) + scale(2 2) = [-4 4]"
      (is (v-close-too? (mv* (translate 1 1) [1 1]) [2 2]))
      (is (v-close-too? (mv* (scale 2 2) [2 2]) [4 4]))

      (is (v-close-too? (mv* (mm* (scale 2 2) (translate 1 1)) [1 1])
                        [4 4])))

    (testing "[1 1] -> trans(1 1) + rot(PI/2) = [-2 2]"
      (is (v-close-too? (mv* (mm* (rotate (/ pi 2)) (translate 1 1)) [1 1])
                        [2 -2])))

    (testing "[1 1] -> trans(1 1) + rot(PI/2) + trans(-1 -1) = [1 -3]"
      (is (v-close-too? (mv* (mm* (translate -1 -1)
                                  (rotate (/ pi 2))
                                  (translate 1 1))
                             [1 1])
                        [1 -3])))))

(deftest test-transform
  (testing "matrix generation"
    (is (v-close-too? (mv* (transform
                            (translate 1 1)
                            (rotate (/ pi 2)))
                           [1 1])
                      (mv* (mm* (rotate (/ pi 2))
                                (translate 1 1))
                           [1 1])))))

(comment (run-tests))
