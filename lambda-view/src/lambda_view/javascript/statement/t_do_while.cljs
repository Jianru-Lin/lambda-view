;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-do-while
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [js-keyword
                                              white-space-optional
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; DoWhileStatement
(defn render [node]
  (let [id (id-of node)
        body (get node "body")
        test (get node "test")]
    [:div {:class "do-while statement"}
     (js-keyword "do")
     (white-space-optional)
     (render-node body)
     (white-space-optional)
     (js-keyword "while")
     (white-space-optional)
     (smart-box {:id            (str id ".test")
                 :pair          :parenthesis
                 :init-collapse true} [test])]))

(def demo ["do {1} while(2)"])
