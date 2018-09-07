;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-this
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

;; ThisExpression
(defn render [_node]
  [:div.this.expression (js-keyword "this")])

(def demo ["this;"])
