(ns user
  (:require
    [ring.middleware.resource :refer [wrap-resource]]
    [shadow.cljs.devtools.api :as shadow]))

(def app (wrap-resource identity "public"))

(defn cljs
  []
  (shadow/repl :app))
