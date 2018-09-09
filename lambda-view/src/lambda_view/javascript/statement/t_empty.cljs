;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-empty
  (:use [lambda-view.javascript.common :only [semicolon]]))

;; EmptyStatement
(defn render [_node]
  [:div {:class "empty statement"} (semicolon)])

(def demo [";"
           ;"while(1) ;"
           ;"if (1) ;"
           ])