;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-break
  (:use [lambda-view.javascript.common :only [js-keyword
                                              white-space-optional
                                              semicolon]]))

;; BreakStatement
(defn render [_node]
  [:div {:class "break statement"} (js-keyword "break") (white-space-optional) (semicolon)])

(def demo ["while(1) break"
           ;"for (;;) break"
           ;"switch (1) {case 1: break}"
           ])