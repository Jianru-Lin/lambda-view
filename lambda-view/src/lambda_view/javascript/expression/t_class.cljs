;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-class
  (:require [lambda-view.javascript.declaration.t-class :as t-class])
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space
                                              white-space-optional
                                              asterisk
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; ClassExpression
(defn render [node]
  (let [id (get node "id")
        super-class (get node "superClass")
        body (get node "body")]
    [:div {:class "class expression"}
     (js-keyword "class")
     (white-space)
     (render-node id)
     (if-not (nil? super-class) [:div
                                 (white-space)
                                 (js-keyword "extends")
                                 (white-space)
                                 (render-node super-class)])
     (white-space-optional)
     (render-node body)]))

;; ClassBody
;; Ref -> Class Declaration File

;; MethodDefinition
;; Ref -> Class Declaration File

(def demo (doall (map (fn [e] (str "(" e ");"))
                      t-class/demo)))