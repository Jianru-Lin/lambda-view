;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-while
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space-optional
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; WhileStatement
(defn render [node]
  (let [id (id-of node)
        test (get node "test")
        test-id (str id ".test")
        body (get node "body")]
    [:div {:class "while statement"}
     (js-keyword "while")
     (white-space-optional)
     (smart-box {:id            test-id
                 :pair          :parenthesis
                 :init-collapse false} [test])
     (white-space-optional)
     (render-node body)]))

(def demo ["while(1) {2}"])
