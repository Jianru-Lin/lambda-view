;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-switch
  (:require [lambda-view.utils :as utils])
  (:use [lambda-view.javascript.render :only [render-node
                                            render-node-coll]]
        [lambda-view.javascript.common :only [js-keyword
                                   white-space
                                   white-space-optional
                                   asterisk
                                   comma
                                   equal
                                   colon
                                   common-list
                                   collapsable-box]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.state :only [init-collapse!
                                  init-layout!]]))

;; SwitchStatement
(defn switch-statement-render [node]
  (let [id (id-of node)
        discriminant (get node "discriminant")
        cases (get node "cases")
        cases-id (str id ".cases")]
    (init-collapse! id false)
    (init-collapse! cases-id true)
    [:div {:class "switch statement"}
     (js-keyword "switch")
     (white-space-optional)
     (collapsable-box {:id id} (render-node discriminant))
     (white-space-optional)
     [collapsable-box {:id   cases-id
                       :pair :brace} (render-node-coll cases)]]))

;; SwitchCase
(defn switch-case-render [node]
  (let [consequent (get node "consequent")
        test (get node "test")]
    [:div {:class "switch-case"}
     [:div {:class "test"} (if (nil? test) (list (js-keyword "default")
                                                 (colon))
                                           (list (js-keyword "case")
                                                 (white-space)
                                                 (render-node test)
                                                 (colon)))]
     (white-space-optional)
     [:div {:class "consequent"} (render-node-coll consequent)]]))


(def demo ["switch(a) {}"
           "switch(a) {default: 1}"
           "switch(a) {case \"a\": case \"b\": 1}"
           "switch(a) {case \"a\": 1; case \"b\": 2}"])
