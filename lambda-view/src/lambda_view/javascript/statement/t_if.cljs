;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-if
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

;; IfStatement
(defn render [node]
  (let [id (id-of node)
        test (get node "test")
        test-id (str id ".test")
        consequent (get node "consequent")
        alternate (get node "alternate")]
    (init-collapse! test-id false)
    [:div {:class "if statement"}
     (js-keyword "if") (white-space-optional) (collapsable-box {:id   test-id
                                                                :pair :parenthesis} (render-node test)) (white-space-optional)
     (render-node consequent)
     (if (nil? alternate)
       nil
       (list (white-space-optional) (js-keyword "else") (white-space-optional) (render-node alternate)))]))

(def demo ["if (1) 2"
           "if (1) {2}"
           ;; "if (1) 2 else 3" NOT WORKING, Invalid Grammer
           "if (1) {2} else 3"
           "if (1) {2} else {3}"
           "if (1) {2} else if (3) {4}"
           "if (1) {2} else if (3) {4} else {5}"])
