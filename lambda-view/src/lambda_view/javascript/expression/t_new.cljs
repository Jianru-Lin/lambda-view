;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-new
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              smart-box
                                              operator
                                              white-space
                                              white-space-optional]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.javascript.expression.utils :only [render-node-by-priority]]))

;; NewExpression
(defn render [node]
  (let [callee (get node "callee")
        arguments (get node "arguments")]
    [:div.new.expression
     (js-keyword "new")
     (white-space)
     (render-node-by-priority node callee)
     (white-space-optional)
     (smart-box {:id            (id-of node)
                 :pair          :parenthesis
                 :seperator     :comma
                 :style         :mini
                 :init-collapse false
                 :init-layout   "horizontal"} arguments)]))

(def demo ["new f();"
           "new f(a);"
           "new f(a,b);"
           "new f(u,v)(a,b);"])
