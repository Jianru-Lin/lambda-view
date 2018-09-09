;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-continue
  (:use [lambda-view.javascript.common :only [js-keyword
                                              white-space-optional
                                              semicolon]]))

;; ContinueStatement
(defn render [_node]
  [:div {:class "continue statement"} (js-keyword "continue") (white-space-optional) (semicolon)])

(def demo ["while(1) continue"])