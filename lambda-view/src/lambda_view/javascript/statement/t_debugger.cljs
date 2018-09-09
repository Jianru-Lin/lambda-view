;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-debugger
  (:use [lambda-view.javascript.common :only [js-keyword
                                              white-space-optional
                                              semicolon]]))

;; DebuggerStatement
(defn render [_node]
  [:div {:class "debugger statement"} (js-keyword "debugger") (white-space-optional) (semicolon)])

(def demo ["debugger"])