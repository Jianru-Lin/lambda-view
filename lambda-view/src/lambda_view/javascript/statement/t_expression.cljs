;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-expression
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [semicolon
                                              white-space-optional]]
        [lambda-view.tag :only [id-of]]))

;; ExpressionStatement
(defn render [node]
  (let [expression (get node "expression")
        directive (get node "directive")]
    [:div {:class "expression statement"} (render-node expression) (white-space-optional) (semicolon)]))
