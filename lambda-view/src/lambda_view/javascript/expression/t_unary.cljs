;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-unary
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
;; UnaryExpression
(defn render [node]
  (let [op (get node "operator")
        prefix (get node "prefix")                          ;TODO when will it be false?
        argument (get node "argument")]
    [:div.unary.expression
     (operator op)
     (white-space-optional)
     (render-exp-node argument node)]))

(def demo ["-a;"
           "+a;"
           "!a;"
           "~a;"
           "typeof a;"
           "void a;"
           "delete a;"])
