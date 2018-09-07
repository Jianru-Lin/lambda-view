;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-member
  (:use [lambda-view.javascript.render :only [render-exp-node]]
        [lambda-view.common :only [js-keyword
                                   white-space
                                   white-space-optional
                                   asterisk
                                   comma
                                   common-list
                                   operator
                                   collapsable-box]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.state :only [init-collapse!
                                  init-layout!]]))

;; MemberExpression
(defn render [node]
  (let [object (get node "object")
        property (get node "property")
        computed (get node "computed")]
    [:div.member.expression
     (render-exp-node object node)
     (if computed (list "["
                        (render-exp-node property node)
                        "]")
                  (list (operator ".")
                        (render-exp-node property node)))]))

(def demo ["a.b;"
           "a.b.c;"
           "a[b];"
           "a[b][c];"])
