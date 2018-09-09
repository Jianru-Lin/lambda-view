;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-this
  (:use [lambda-view.javascript.common :only [js-keyword]]))

;; ThisExpression
(defn render [_node]
  [:div.this.expression (js-keyword "this")])

(def demo ["this;"])
