(ns format
  (:require
   [babashka.pods :as pods]))

(pods/load-pod 'com.github.clojure-lsp/clojure-lsp "2022.02.01-20.02.32")

(require '[clojure-lsp.api :as lsp])

(lsp/clean-ns! {})
(lsp/format! {})

;; Return done to prevent printing the giant result object from format.
"Done."
