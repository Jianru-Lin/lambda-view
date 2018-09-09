;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-sequence
  (:use [lambda-view.javascript.common :only [smart-box]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.state :only [init-layout!]]))

;; SequenceExpression
;; TODO BUG (collapse/layout)
(defn render [node]
  (let [id (id-of node)
        expressions (get node "expressions")]
    [:div.sequence.expression (smart-box {:id            id
                                          :pair          :none
                                          :exp-mode      true
                                          :init-collapse false
                                          :init-layout   (if (> (count expressions) 5) "vertical" "horizontal")} expressions)]))

(def demo [;"a, b;"
           ;"a, b, c;"
           "a, (b, c);"])
