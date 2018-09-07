;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.declaration.t-variable
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

;; VariableDeclaration
(defn variable-declaration-render [node]
  (let [kind (get node "kind")
        declarations (get node "declarations")]
    [:div {:class "variable declaration"}
     (js-keyword kind)
     (white-space)
     (utils/join (render-node-coll declarations) (list (comma) (white-space-optional)))]))

;; VariableDeclarator
(defn variable-declarator-render [node]
  (let [id (get node "id")
        init (get node "init")]
    [:div {:class "variable-declarator"}
     (render-node id)
     (if-not (nil? init) (list (white-space-optional)
                               (equal)
                               (white-space-optional)
                               (render-node init)))]))


(def demo ["var a1"
           "var a2 = 1"
           "var a3 = 1, a4 = 2"
           "let b1"
           "let b2 = 1"
           "let b3 = 1, b4 = 2"
           ;; "const x" is invalid
           "const c1 = 1"
           "const c2 = 1, c3 = 2"])
