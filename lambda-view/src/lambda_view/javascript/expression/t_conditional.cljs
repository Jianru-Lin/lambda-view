;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-conditional
  (:use [lambda-view.javascript.common :only [smart-box
                                              operator
                                              white-space-optional]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.javascript.expression.utils :only [render-node-by-priority]]))

;; ConditionalExpression
(defn render [node]
  (let [test (get node "test")
        consequent (get node "consequent")                          ;TODO when will it be false?
        alternate (get node "alternate")]
    [:div.conditional.expression
     (render-node-by-priority node test)
     (white-space-optional)
     [:div.branch
      [:div
       (operator "?")
       (white-space-optional)
       (render-node-by-priority node consequent)
       (white-space-optional)]
      [:div
       (operator ":")
       (white-space-optional)
       (render-node-by-priority node alternate)]]]))

(def demo ["a ? b : c;"
           "a ? b ? c : d : e;"])
