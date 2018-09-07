;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.basic.t-literal)

;; Literal
(defn render [node]
  [:div {:class "literal"} (get node "raw")])

(def demo ["undefined;"
           "null;"
           "true;"
           "1.23e4;"
           "\"Hello World!\";"
           "/a\\//g;"
           "[0,1,2];"
           "({key: value});"])
