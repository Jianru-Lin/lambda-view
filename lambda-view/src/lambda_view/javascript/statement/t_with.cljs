;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-with
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

;; WithStatement
(defn render [node]
  (let [id (id-of node)
        object (get node "object")
        object-id (str id ".objectt")
        body (get node "body")]
    (init-collapse! object-id false)
    [:div {:class "with statement"}
     (js-keyword "with")
     (white-space-optional)
     (collapsable-box {:id   object-id
                       :pair :parenthesis} (render-node object))
     (white-space-optional)
     (render-node body)]))


(def demo ["with (x) {y}"])
