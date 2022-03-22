(ns pearigon.utils.file-system
  (:require
   ["browser-fs-access" :as fs]))

(defn- flush-modifier-keys
  "This is a workaround.

  When the file dialog closes, the keyup event from the user releasing
  control is never recieved. This synthetic event is captured by
  pearigon.input.keyboard and correctly restores the suggestion state."
  []
  (js/document.dispatchEvent
   (js/KeyboardEvent. "keyup" #js{"code" "ControlLeft"})))

(defn- str->blob [s mime-type]
  (js/Blob. (clj->js [s])
            (clj->js {:type mime-type})))

(defn- save-file [{:keys [blob name extensions]}]
  (-> (fs/fileSave blob (clj->js {:fileName name :extensions extensions}))
      (.catch #(js/console.log "Error from save-file: " %))
      (.finally flush-modifier-keys)))

(defn- open-file [options on-load on-error]
  (-> (fs/fileOpen (clj->js options))
      (.then #(.text %))
      (.then #(on-load %))
      (.catch #(on-error %))
      (.finally flush-modifier-keys)))

(defn save-svg [name svg-str]
  (save-file {:blob (str->blob svg-str "application/edn")
              :name name
              :extensions [".svg"]}))

(defn save-edn [name obj]
  (save-file {:blob (str->blob (prn-str obj) "application/edn")
              :name name
              :extensions [".edn"]}))

(def ^:private open-project-config
  {:extensions [".edn"]
   :description "Project file"
   :id "project"})

(defn open-project
  ([on-load]
   (open-file open-project-config on-load
              #(js/console.log "Error from open-project: " %)))
  ([on-load on-error]
   (open-file open-project-config
              on-load
              on-error)))
