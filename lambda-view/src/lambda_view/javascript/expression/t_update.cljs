;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-update
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [smart-box
                                              operator
                                              white-space-optional]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.javascript.expression.utils :only [render-node-by-priority]]))

;; UpdateExpression
(defn render [node]
  (let [op (get node "operator")
        prefix (get node "prefix")                          ;TODO when will it be false?
        argument (get node "argument")]
    (if prefix [:div.update.expression
                (operator op)
                (white-space-optional)
                (render-node-by-priority node argument)]
               [:div.update.expression
                (render-node-by-priority node argument)
                (white-space-optional)
                (operator op)])))

(def demo ["++a;"
           "a++;"
           "--a;"
           "a--;"
           "++ a + b;"])
