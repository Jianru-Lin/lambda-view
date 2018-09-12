;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-call
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [smart-box
                                              operator
                                              white-space-optional]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.javascript.expression.utils :only [render-node-by-priority]]))

;; CallExpression
(defn render [node]
  (let [callee (get node "callee")
        arguments (get node "arguments")]
    [:div.call.expression
     (render-node-by-priority node callee)
     (white-space-optional)
     (smart-box {:id            (id-of node)
                 :pair          :parenthesis
                 :seperator     :comma
                 :style         :mini
                 :init-collapse false
                 :init-layout   "horizontal"} arguments)]))

(def demo ["f();"
           "f(a);"
           "f(a,b);"
           "f(u,v)(a,b);"])
