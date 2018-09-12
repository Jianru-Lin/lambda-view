;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.basic.t-literal
  (:use [lambda-view.tag :only [id-of]]
        [lambda-view.javascript.common :only [smart-box]]))

;; Literal
(defn render [node]
  (let [id (id-of node)
        value (get node "value")
        raw (get node "raw")
        regex (get node "regex")]
    (cond
      ;; string literal
      (string? value)
      [:div.literal.string
       (smart-box {:id            id
                   :style         (if-not (clojure.string/includes? value "\n") :mini)
                   :pair          :double-quote
                   :init-collapse false
                   :auto-render   false} [value])]

      ;; regex
      (not (nil? regex))
      (let [pattern (get regex "pattern")
            flags (get regex "flags")]
        [:div.literal.regex
         (smart-box {:id            id
                     :style         :mini
                     :pair          :slash
                     :init-collapse false
                     :auto-render   false} [pattern])
         [:div.regex-flags flags]])

      ;; number
      (number? value)
      (let [[_ prefix body] (re-matches #"(0[bBoOxX])(.+)" raw)]
        (if (nil? prefix) [:div.literal.number [:div.body raw]]
                          [:div.literal.number
                           [:div.prefix (clojure.string/lower-case prefix)]
                           [:div.body (clojure.string/upper-case body)]]))

      ;; boolean
      (boolean? value)
      [:div.literal.boolean (str raw)]

      ;; null
      (nil? value)
      [:div.literal.null (str raw)]

      ;; Impossible
      true
      (throw (js/Error. (str "Unknown literal type: " raw))))))

;; [NOTE] "undefined" is not a literal"
(def demo ["null;"
           ;; boolean
           "true;"
           "false;"
           ;; number
           "123;"
           "1.23e4;"
           "0b1101;"
           "0B1101;"
           "0o7777;"
           "0O7777;"
           "0xFFFF;"
           "0XFFFF;"
           ;; string
           "\"Hello World!\";"
           "'Hello World!';"
           "\"mutilple\\nline\";"
           ;; regex
           "/a\\//g;"])
