;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-try
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

;; TryStatement
(defn try-statement-render [node]
  (let [block (get node "block")
        handler (get node "handler")
        finalizer (get node "finalizer")]
    [:div {:class "try statement"}
     (js-keyword "try") (white-space-optional) (render-node block)
     (if-not (nil? handler) (list (white-space-optional) (render-node handler)))
     (if-not (nil? finalizer) (list (white-space-optional) (js-keyword "finally") (white-space-optional) (render-node finalizer)))]))

;; CatchClause
(defn catch-clause-render [node]
  (let [id (id-of node)
        param (get node "param")
        param-id (str id ".param")
        body (get node "body")]
    (init-collapse! param-id false)
    [:div {:class "catch-clause"}
     (js-keyword "catch")
     (white-space-optional)
     (if-not (nil? param) (list (collapsable-box {:id   param-id
                                                  :pair :parenthesis} (render-node param)) (white-space-optional)))
     (render-node body)]))


(def demo ["try {a} catch {b}"
           "try {a} finally {b}"
           "try {a} catch {b} finally {c}"
           "try {a} catch(e) {b} finally {c}"])
