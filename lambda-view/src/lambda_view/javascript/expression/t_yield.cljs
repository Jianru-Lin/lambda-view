;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-yield
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [smart-box
                                              operator
                                              white-space-optional]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.javascript.expression.utils :only [render-node-by-priority]]))

;; YieldExpression
(defn render [node]
  (let [delegate (get node "delegate")                      ;TODO when will this be true?
        argument (get node "argument")]
    [:div.yield.expression
     (operator "yield")
     (white-space-optional)
     (render-node-by-priority node argument)]))

(def demo ["function *g() {yield 1}"
           "function *g() {yield 1+2}"
           "function *g() {yield 1,2}"
           "function *g() {yield (1,2)}"])
