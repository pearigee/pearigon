(ns e2e.tests.search-test
  (:require [clojure.test :refer [deftest is run-tests]]
            [clojure.string :as str]
            [e2e.utils.input :as input]
            [e2e.utils.elements :as ele]
            [etaoin.api :as eta]
            [etaoin.query :as query]
            [etaoin.keys :as k]))

(comment
  ;; For convient REPL execution of the tests
  (def driver (eta/chrome))
  (run-tests))

(def url "http://localhost:3000")

(deftest search-for-color-and-open-styles-with-enter
  (let [driver (eta/chrome-headless)]
    (eta/go driver url)
    (eta/wait-visible driver ele/svg-root)

    (input/press-key driver {:code "Space"})
    (eta/wait-visible driver ele/search-input)

    (eta/fill-active driver "color")
    (eta/wait 1)

    (is (str/includes?
         (eta/get-element-text driver ele/selected-search-result)
         "Styles"))

    (eta/fill-active driver k/enter)
    (eta/wait-visible driver ele/sidebar)

    (is (str/includes?
         (eta/get-element-text driver ele/sidebar-header)
         "Styles"))

    (is (= (eta/visible? driver ele/search-input)
           false))

    (eta/quit driver)))

(deftest search-for-color-and-open-styles-with-click
  (let [driver (eta/chrome-headless)]
    (eta/go driver url)
    (eta/wait-visible driver ele/svg-root)

    (input/press-key driver {:code "Space"})
    (eta/wait-visible driver ele/search-input)

    (eta/fill-active driver "color")
    (eta/wait 1)

    (is (str/includes?
         (eta/get-element-text driver ele/selected-search-result)
         "Styles"))

    (eta/click driver ele/selected-search-result)
    (eta/wait-visible driver ele/sidebar)

    (is (str/includes?
         (eta/get-element-text driver ele/sidebar-header)
         "Styles"))

    (is (= (eta/visible? driver ele/search-input)
           false))

    (eta/quit driver)))
