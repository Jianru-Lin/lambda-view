;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-do-while
  (:require [lambda-view.utils :as utils])
  (:use [lambda-view.javascript.render :only [render-node
                                            render-node-coll]]
        [lambda-view.javascript.common :only [js-keyword
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

;; DoWhileStatement
(defn render [node]
  (let [id (id-of node)
        body (get node "body")
        test (get node "test")
        test-id (str id ".test")]
    (init-collapse! test-id false)
    [:div {:class "do-while statement"}
     (js-keyword "do")
     (white-space-optional)
     (render-node body)
     (white-space-optional)
     (js-keyword "while")
     (white-space-optional)
     (collapsable-box {:id   test-id
                       :pair :parenthesis} (render-node test))]))

(def demo ["do {1} while(2)"])
