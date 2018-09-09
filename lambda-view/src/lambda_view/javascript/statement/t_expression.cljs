;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.statement.t-expression
  (:use [lambda-view.javascript.render :only [render-node]]
        [lambda-view.javascript.common :only [semicolon
                                              white-space-optional
                                              smart-box]]
        [lambda-view.tag :only [id-of]]))

;; ExpressionStatement
(defn render [node]
  (let [id (id-of node)
        expression (get node "expression")
        directive (get node "directive")]
    [:div {:class "expression statement"}
     (smart-box {:id            id
                 ;; TODO only show pairs for object/array/? expression is required?
                 :pair          :parenthesis
                 :seperator     :none
                 :init-collapse false
                 :init-layout   "horizontal"} [expression])
     (white-space-optional)
     (semicolon)]))
