;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-for-of
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

;; ForOfStatement
(defn render [node]
  (let [id (id-of node)
        left (get node "left")
        right (get node "right")
        body (get node "body")]
    (init-collapse! id false)
    [:div {:class "for-of statement"}
     (js-keyword "for")
     (white-space-optional)
     (collapsable-box {:id   id
                       :pair :parenthesis} (list (render-node left)
                                                 (white-space) (js-keyword "of") (white-space)
                                                 (render-node right)))
     (white-space-optional)
     (render-node body)]))

(def demo ["for(a of b) 1"
           "for(a of b) {1}"
           ;; TODO "for await (const x of xs) {}"
           ])
