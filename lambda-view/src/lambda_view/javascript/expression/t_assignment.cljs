;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-assignment
  (:use [lambda-view.javascript.common :only [js-keyword
                                              white-space
                                              white-space-optional
                                              asterisk
                                              comma
                                              common-list
                                              operator
                                              collapsable-box
                                              render-exp-node]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.state :only [init-collapse!
                                  init-layout!]]))

;; AssignmentExpression
(defn render [node]
  (let [op (get node "operator")
        left (get node "left")
        right (get node "right")]
    [:div.assignment.expression
     (render-exp-node left node)
     (white-space-optional)
     (operator op)
     (white-space-optional)
     (render-exp-node right node)]))

(def demo ["a = 1"
           "c = d = 2"])
