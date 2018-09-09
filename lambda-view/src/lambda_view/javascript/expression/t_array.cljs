;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-array
  (:use [lambda-view.javascript.common :only [smart-box]]
        [lambda-view.tag :only [id-of]]))

;; ArrayExpression
(defn render [node]
  (let [id (id-of node)
        elements (get node "elements")]
    [:div.array.expression
     (smart-box {:id            id
                 :pair          :bracket
                 :exp-mode      true
                 :init-collapse false
                 :init-layout   (if (> (count elements) 5) "vertical" "horizontal")} elements)]))

(def demo ["[];"
           "[1];"
           "[1, 2];"
           "[1, 2, 3];"
           "[1, 2, 3, 4];"
           "[1, 2, 3, 4, 5];"
           "[1, 2, 3, 4, 5, 6];"
           "[[1], 2, [3, 4]]"])
