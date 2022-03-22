(ns pearigon.view.code-editor
  (:require
   ["@codemirror/fold" :as fold]
   ["@codemirror/highlight" :as highlight]
   ["@codemirror/history" :refer [history historyKeymap]]
   ["@codemirror/state" :refer [EditorState]]
   ["@codemirror/view" :as view :refer [EditorView]]
   [pearigon.api.core :as api]
   [applied-science.js-interop :as j]
   [nextjournal.clojure-mode :as cm-clj]
   [nextjournal.clojure-mode.extensions.eval-region :as eval-region]
   [nextjournal.clojure-mode.test-utils :as test-utils]
   [reagent.core :as r]))

(defn eval-string [source]
  (try (api/eval-string source)
       (catch js/Error e
         (str e))))

(j/defn eval-at-cursor [on-result ^:js {:keys [state]}]
  (some->> (eval-region/cursor-node-string state)
           (eval-string)
           (on-result))
  true)

(j/defn eval-top-level [on-result ^:js {:keys [state]}]
  (some->> (eval-region/top-level-string state)
           (eval-string)
           (on-result))
  true)

(j/defn eval-cell [on-result ^:js {:keys [state]}]
  (-> (str "(do " (.-doc state) " )")
      (eval-string)
      (on-result))
  true)

(defn extension [{:keys [modifier
                         on-result]}]
  (.of view/keymap
       (j/lit
        [{:key "Mod-Enter"
          :run (partial eval-cell on-result)}
         {:key (str modifier "-Enter")
          :shift (partial eval-top-level on-result)
          :run (partial eval-at-cursor on-result)}])))

(def theme
  (.theme EditorView
          (j/lit {".cm-content" {:white-space "pre-wrap"
                                 :padding "10px 0"}
                  "&.cm-focused" {:outline "none"}
                  ".cm-line" {:padding "0 9px"
                              :line-height "1.6"
                              :font-size "16px"
                              :font-family "var(--code-font)"}
                  ".cm-matchingBracket" {:border-bottom "1px solid var(--teal-color)"
                                         :color "inherit"}
                  ".cm-gutters" {:background "transparent"
                                 :border "none"}
                  ".cm-gutterElement" {:margin-left "5px"}
                  ;; only show cursor when focused
                  ".cm-cursor" {:visibility "hidden"}
                  "&.cm-focused .cm-cursor" {:visibility "visible"}})))

(defonce extensions #js[theme
                        (history)
                        highlight/defaultHighlightStyle
                        (view/drawSelection)
                        (fold/foldGutter)
                        (.. EditorState -allowMultipleSelections (of true))
                        cm-clj/default-extensions
                        (.of view/keymap cm-clj/complete-keymap)
                        (.of view/keymap historyKeymap)])

(defn code-editor []
  (r/with-let [*view (r/atom nil)
               *last-result (r/atom "")
               *show-results (r/atom false)
               mount! (fn [el]
                        (when el
                          (reset!
                           *view
                           (new
                            EditorView
                            (j/obj :state
                                   (test-utils/make-state
                                    (.concat
                                     extensions
                                     #js [(extension
                                           {:modifier  "Alt"
                                            :on-result (partial
                                                        reset!
                                                        *last-result)})])
                                    "")
                                   :parent el)))))]
    [:div.code-editor
     [:div.code-mirror {:ref mount!}]
     [:div.code-editor-results
      [:div.code-editor-results-titlebar
       [:div
        [:div.select.is-small [:select [:option "Playground"]]]]
       [:button.button.is-small
        {:on-click #(reset! *show-results (not @*show-results))}
        (if @*show-results "Hide results" "Show results")]]
      (when @*show-results
        [:div.code-editor-results-output
         [:div (prn-str @*last-result)]])]]
    (finally
      (j/call @*view :destroy))))
