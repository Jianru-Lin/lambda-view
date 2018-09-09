;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-throw
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space
                                              white-space-optional
                                              semicolon]]))

;; ThrowStatement
(defn render [node]
  (let [argument (get node "argument")]
    [:div {:class "throw statement"}
     (js-keyword "throw") (white-space) (render-node argument) (white-space-optional) (semicolon)]))

(def demo ["throw 1"])