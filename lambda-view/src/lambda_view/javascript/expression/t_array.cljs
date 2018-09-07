;; ESTree Spec
;; https://github.com/estree/estree

(ns lambda-view.javascript.expression.t-array
  (:use [lambda-view.javascript.render :only [render-node
                                            render-node-coll]]
        [lambda-view.common :only [js-keyword
                                   white-space
                                   white-space-optional
                                   asterisk
                                   comma
                                   common-list
                                   collapsable-box]]
        [lambda-view.tag :only [id-of]]
        [lambda-view.state :only [init-collapse!
                                  init-layout!]]))


;; ArrayExpression
(defn render [node]
  (let [id (id-of node)
        elements (get node "elements")]
    (init-collapse! id true)
    [:div.array.expression
     [collapsable-box {:id   id
                       :pair :bracket} (common-list {:id id} elements)]]))


(def demo ["[];"
           "[1];"
           "[1, 2];"
           "[1, 2, 3];"
           "[1, 2, 3, 4];"
           "[1, 2, 3, 4, 5];"
           "[1, 2, 3, 4, 5, 6];"])
