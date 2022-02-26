(ns frontend.tools.export
  (:require [frontend.export.svg :as svg]
            [frontend.utils.file-system :as fs]))

(defn export []
  (fs/save-svg "export.svg" (svg/->svg-string)))
