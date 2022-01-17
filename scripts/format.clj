(ns format
  (:require
   [babashka.pods :as pods]))

(pods/load-pod 'com.github.clojure-lsp/clojure-lsp "2022.01.03-19.46.10")

(require '[clojure-lsp.api :as lsp])

(lsp/format! {})
