;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-block
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

;; BlockStatement
(defn render [node]
  (let [id (id-of node)
        body (get node "body")]
    (init-collapse! id true)
    [:div.block.statement
     [collapsable-box {:id   id
                       :pair :brace} (render-node-coll body)]]))


(def demo ["{}"
           "{label: statement}"])
