;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-with
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space-optional
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; WithStatement
(defn render [node]
  (let [id (id-of node)
        object (get node "object")
        object-id (str id ".objectt")
        body (get node "body")]
    [:div {:class "with statement"}
     (js-keyword "with")
     (white-space-optional)
     (smart-box {:id            object-id
                 :pair          :parenthesis
                 :init-collapse false} [object])
     (white-space-optional)
     (render-node body)]))


(def demo ["with (x) {y}"])
