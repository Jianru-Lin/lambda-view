;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-sequence
  (:use [lambda-view.javascript.render :only [render-exp-node]]
        [lambda-view.common :only [js-keyword
                                   white-space
                                   white-space-optional
                                   asterisk
                                   comma
                                   common-list
                                   operator
                                   collapsable-box
                                   toggle-layout-element]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.state :only [init-collapse!
                                  init-layout!]]))

;; SequenceExpression
;; TODO BUG (collapse/layout)
(defn render [node]
  (let [id (id-of node)
        expressions (get node "expressions")
        tail-idx (- (count expressions) 1)]
    (println "sequence-expression-render" id)
    [:div.sequence.expression (map-indexed (fn [idx e] [:div.box-element
                                                        (render-exp-node e node)
                                                        (if (not= idx tail-idx) (list (toggle-layout-element id (comma))
                                                                                      (white-space-optional)))])
                                           expressions)]))

(def demo [;"a, b;"
           ;"a, b, c;"
           "a, (b, c);"])
