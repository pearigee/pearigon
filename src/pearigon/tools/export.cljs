(ns pearigon.tools.export
  (:require
   [pearigon.export.svg :as svg]
   [pearigon.utils.file-system :as fs]))

(defn export []
  (fs/save-svg "export.svg" (svg/->svg-string)))
