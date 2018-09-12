;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-await
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [smart-box
                                              operator
                                              white-space-optional]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.javascript.expression.utils :only [render-node-by-priority]]))

;; AwaitExpression
(defn render [node]
  (let [argument (get node "argument")]
    [:div.await.expression
     (operator "await")
     (white-space-optional)
     (render-node-by-priority node argument)]))

(def demo ["async function f() {await 1}"
           "async function f() {await 1+2}"
           "async function f() {await 1,2}"
           "async function f() {await (1,2)}"])
