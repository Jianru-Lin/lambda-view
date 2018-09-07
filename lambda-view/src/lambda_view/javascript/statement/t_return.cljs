;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-return
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

;; ReturnStatement
(defn render [node]
  (let [argument (get node "argument")]
    (if (nil? argument)
      [:div {:class "return statement"} (js-keyword "return")]
      [:div {:class "return statement"} (js-keyword "return") (white-space) (render-node argument)])))
