(ns frontend.export.file-system
  (:require ["browser-fs-access" :as fs]))

(defn- str->blob [s mime-type]
  (js/Blob. (clj->js [s])
            (clj->js {:type mime-type})))

(defn- save-file [{:keys [blob name extensions]}]
  (fs/fileSave blob (clj->js {:fileName name :extensions extensions})))

(defn save-svg [name svg-str]
  (save-file {:blob (str->blob svg-str "image/svg+xml")
              :name name
              :extensions [".svg"]}))
