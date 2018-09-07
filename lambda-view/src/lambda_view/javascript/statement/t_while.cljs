;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-while
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

;; WhileStatement
(defn render [node]
  (let [id (id-of node)
        test (get node "test")
        test-id (str id ".test")
        body (get node "body")]
    (init-collapse! test-id false)
    [:div {:class "while statement"}
     (js-keyword "while")
     (white-space-optional)
     (collapsable-box {:id   test-id
                       :pair :parenthesis} (render-node test))
     (white-space-optional)
     (render-node body)]))

(def demo ["while(1) {2}"])
