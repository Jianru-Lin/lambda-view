;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-member
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [smart-box
                                              operator]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.javascript.expression.utils :only [render-node-by-priority]]))

;; MemberExpression
(defn render [node]
  (let [id (id-of node)
        object (get node "object")
        property (get node "property")
        computed (get node "computed")]
    [:div.member.expression
     (render-node-by-priority node object)
     (if computed (smart-box {:id            id
                              :pair          :bracket
                              :style         :mini
                              :init-collapse false} [property])
                  (list (operator ".")
                        (render-node-by-priority node property)))]))

(def demo ["a.b;"
           "a.b.c;"
           "a[b];"
           "a[b][c];"
           "(a.b).c;"
           ;; impposible "a.(b.c)"
           "(a+b).c;"
           ])
