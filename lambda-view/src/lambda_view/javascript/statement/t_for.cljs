;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-for
  (:require [lambda-view.utils :as utils])
  (:use [lambda-view.javascript.render :only [render-node
                                            render-node-coll]]
        [lambda-view.common :only [js-keyword
                                   white-space
                                   white-space-optional
                                   asterisk
                                   comma
                                   equal
                                   semicolon
                                   common-list
                                   collapsable-box]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.state :only [init-collapse!
                                  init-layout!]]))

;; ForStatement
(defn render [node]
  (let [id (id-of node)
        init (get node "init")
        test (get node "test")
        update (get node "update")
        body (get node "body")]
    (init-collapse! id false)
    [:div {:class "for statement"}
     (js-keyword "for")
     (white-space-optional)
     (collapsable-box {:id   id
                       :pair :parenthesis} (list (render-node init)
                                                 (semicolon) (white-space-optional)
                                                 (render-node test)
                                                 (semicolon) (white-space-optional)
                                                 (render-node update)))
     (white-space-optional)
     (render-node body)]))


(def demo ["for(;;) 1"
           "for(u;;) 1"
           "for(;v;) 1"
           "for(;;w) 1"
           "for(a;b;c) 1"
           "for (a;b;c) {1}"])