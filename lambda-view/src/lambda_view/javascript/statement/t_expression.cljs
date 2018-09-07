;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-expression
  (:require [lambda-view.utils :as utils])
  (:use [lambda-view.javascript.render :only [render-node
                                            render-node-coll]]
        [lambda-view.common :only [js-keyword
                                   white-space
                                   white-space-optional
                                   asterisk
                                   comma
                                   equal
                                   common-list
                                   collapsable-box]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.state :only [init-collapse!
                                  init-layout!]]))

;; ExpressionStatement
(defn render [node]
  (let [expression (get node "expression")
        directive (get node "directive")]
    [:div {:class "expression statement"} (render-node expression)]))
